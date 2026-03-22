package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

/**
 * JDBC implementation of {@link HorseDao} for interacting with the database.
 */
@Repository
public class HorseJdbcDao implements HorseDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String TABLE_NAME = "horse";

  private static final String SQL_SELECT_ALL =
      "SELECT * FROM " + TABLE_NAME;

  private static final String SQL_SELECT_BY_ID =
      "SELECT * FROM " + TABLE_NAME
              + " WHERE ID = :id";


  // Note: The weird formatting below is related to how we generate the template. You can remove the literal concatenations.
  private static final String SQL_INSERT =
      "INSERT INTO " + TABLE_NAME + """ 
      (name,
       description,
       date_of_birth,
       sex,
      owner_id
      """
      + """  
          )
          VALUES (
          :name,
          :description,
          :date_of_birth,
          :sex,
          """
      + """
          :owner_id)
          """;

  private static final String SQL_UPDATE =
          "UPDATE " + TABLE_NAME
                  + " SET name = :name, description = :description, date_of_birth = :date_of_birth, "
                  + "sex = :sex, owner_id = :owner_id WHERE id = :id";

  private static final String SQL_DELETE =
          "DELETE FROM " + TABLE_NAME
                  + " WHERE id = :id";

  private static final String SQL_SELECT_PARENTS =
          "SELECT h.* FROM " + TABLE_NAME
                  + " h INNER JOIN horse_parents hp ON h.id = hp.parent_id "
                  + "WHERE hp.horse_id = :horse_id";

  private static final String SQL_INSERT_PARENT =
          "INSERT INTO horse_parents (horse_id, parent_id) VALUES (:horse_id, :parent_id)";

  private static final String SQL_DELETE_PARENT =
          "DELETE FROM horse_parents WHERE horse_id = :horse_id AND parent_id = :parent_id";


  private final JdbcClient jdbcClient;

  @Autowired
  public HorseJdbcDao(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  @Override
  public List<Horse> getAll() {
    LOG.trace("getAll()");
    return jdbcClient
        .sql(SQL_SELECT_ALL)
        .query(this::mapRow)
        .list();
  }

  @Override
  public Horse getById(long id) throws NotFoundException {
    LOG.trace("getById({})", id);
    List<Horse> horses = jdbcClient
        .sql(SQL_SELECT_BY_ID)
        .param("id", id)
        .query(this::mapRow)
        .list();

    if (horses.isEmpty()) {
      throw new NotFoundException("No horse with ID %d found".formatted(id));
    }
    if (horses.size() > 1) {
      // This should never happen!!
      throw new FatalException("Too many horses with ID %d found".formatted(id));
    }

    return horses.getFirst();
  }


  @Override
  public Horse create(HorseCreateDto horse) {
    LOG.trace("create({})", horse);

    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

    int created = jdbcClient
        .sql(SQL_INSERT)
        .param("name", horse.name())
        .param("description", horse.description())
        .param("date_of_birth", horse.dateOfBirth())
        .param("sex", horse.sex().toString())
        .param("owner_id", horse.ownerId())
        .update(keyHolder);

    if (created != 1) {
      throw new FatalException("%d horses inserted, expected exactly 1".formatted(created));
    }

    Number key = keyHolder.getKey();

    if (key == null) {
      throw new FatalException("No generated key returned for inserted horse.");
    }

    long generatedId = key.longValue();
    return new Horse(
        generatedId,
        horse.name(),
        horse.description(),
        horse.dateOfBirth(),
        horse.sex(),
        horse.ownerId());
  }

  @Override
  public Horse update(Horse horse) throws NotFoundException {
    LOG.trace("update({})", horse);

    int updated = jdbcClient
            .sql(SQL_UPDATE)
            .param("id", horse.id())
            .param("name", horse.name())
            .param("description", horse.description())
            .param("date_of_birth", horse.dateOfBirth())
            .param("sex", horse.sex().toString())
            .param("owner_id", horse.ownerId())
            .update();

    if (updated == 0) {
      throw new NotFoundException("No horse with ID %d found".formatted(horse.id()));
    }

    return horse;
  }

  @Override
  public void delete(long id) throws NotFoundException {
    LOG.trace("delete({})", id);

    // First verify horse exists
    getById(id);

    // Delete all parent relationships for this horse
    jdbcClient
            .sql("DELETE FROM horse_parents WHERE horse_id = :id OR parent_id = :id")
            .param("id", id)
            .update();

    // Then delete the horse
    int deleted = jdbcClient
            .sql(SQL_DELETE)
            .param("id", id)
            .update();

    if (deleted == 0) {
      throw new NotFoundException("No horse with ID %d found".formatted(id));
    }
  }

  @Override
  public List<Horse> search(HorseSearchDto searchCriteria) {
    LOG.trace("search({})", searchCriteria);

    StringBuilder sql = new StringBuilder("SELECT * FROM " + TABLE_NAME + " WHERE 1=1");
    Map<String, Object> params = new HashMap<>();

    if (searchCriteria.name() != null && !searchCriteria.name().isBlank()) {
      sql.append(" AND UPPER(name) LIKE UPPER(:name)");
      params.put("name", "%" + searchCriteria.name() + "%");
    }

    if (searchCriteria.description() != null && !searchCriteria.description().isBlank()) {
      sql.append(" AND UPPER(description) LIKE UPPER(:description)");
      params.put("description", "%" + searchCriteria.description() + "%");
    }

    if (searchCriteria.dateOfBirthBefore() != null) {
      sql.append(" AND date_of_birth < :dateOfBirth");
      params.put("dateOfBirth", searchCriteria.dateOfBirthBefore());
    }

    if (searchCriteria.sex() != null) {
      sql.append(" AND sex = :sex");
      params.put("sex", searchCriteria.sex().toString());
    }

    if (searchCriteria.ownerName() != null && !searchCriteria.ownerName().isBlank()) {
      sql.append(" AND owner_id IN (SELECT id FROM owner WHERE UPPER(first_name || ' ' || last_name) LIKE UPPER(:ownerName))");
      params.put("ownerName", "%" + searchCriteria.ownerName() + "%");
    }

    var query = jdbcClient.sql(sql.toString());
    for (var entry : params.entrySet()) {
      query = query.param(entry.getKey(), entry.getValue());
    }

    return query.query(this::mapRow).list();
  }

  @Override
  public List<Horse> getParents(long horseId) {
    LOG.trace("getParents({})", horseId);

    return jdbcClient
            .sql(SQL_SELECT_PARENTS)
            .param("horse_id", horseId)
            .query(this::mapRow)
            .list();
  }

  @Override
  public void addParent(long horseId, long parentId) throws NotFoundException {
    LOG.trace("addParent({}, {})", horseId, parentId);

    // Verify both horses exist
    getById(horseId);
    getById(parentId);

    jdbcClient
            .sql(SQL_INSERT_PARENT)
            .param("horse_id", horseId)
            .param("parent_id", parentId)
            .update();
  }

  @Override
  public void removeParent(long horseId, long parentId) {
    LOG.trace("removeParent({}, {})", horseId, parentId);

    jdbcClient
            .sql(SQL_DELETE_PARENT)
            .param("horse_id", horseId)
            .param("parent_id", parentId)
            .update();
  }


  private Horse mapRow(ResultSet result, int rownum) throws SQLException {
    return new Horse(
        result.getLong("id"),
        result.getString("name"),
        result.getString("description"),
        result.getDate("date_of_birth").toLocalDate(),
        Sex.valueOf(result.getString("sex")),
        result.getObject("owner_id", Long.class));
  }
}
