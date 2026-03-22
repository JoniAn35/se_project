package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import java.util.List;

/**
 * Data Access Object for horses.
 * Implements access functionality to the application's persistent data store regarding horses.
 */
public interface HorseDao {
  /**
   * Get all horses stored in the persistent data store.
   *
   * @return a list of all stored horses
   */
  List<Horse> getAll();


  /**
   * Create a horse with the data given in {@code horse}
   *  in the persistent data store.
   *
   * @param horse the data to use to create the horse
   * @return the created horse
   */
  Horse create(HorseCreateDto horse);


  /**
   * Get a horse by its ID from the persistent data store.
   *
   * @param id the ID of the horse to get
   * @return the horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  Horse getById(long id) throws NotFoundException;

  /**
   * Update an existing horse in the persistent data store.
   *
   * @param horse the horse with updated data (must have id set)
   * @return the updated horse
   * @throws NotFoundException if no horse with the given ID exists
   */
  Horse update(Horse horse) throws NotFoundException;

  /**
   * Delete a horse from the persistent data store by its ID.
   *
   * @param id the ID of the horse to delete
   * @throws NotFoundException if no horse with the given ID exists
   */
  void delete(long id) throws NotFoundException;

  /**
   * Search for horses matching the given criteria.
   *
   * @param searchCriteria the search parameters (name, description, dateOfBirth, sex, ownerName)
   * @return a list of horses matching the criteria
   */
  List<Horse> search(HorseSearchDto searchCriteria);

  /**
   * Get the parent horses of a given horse.
   *
   * @param horseId the ID of the horse
   * @return a list of parent horses (empty if no parents exist)
   */
  List<Horse> getParents(long horseId);

  /**
   * Add a parent relationship between two horses.
   *
   * @param horseId the ID of the child horse
   * @param parentId the ID of the parent horse
   * @throws NotFoundException if either horse ID doesn't exist
   */
  void addParent(long horseId, long parentId) throws NotFoundException;

  /**
   * Remove a parent relationship between two horses.
   *
   * @param horseId the ID of the child horse
   * @param parentId the ID of the parent horse
   */
  void removeParent(long horseId, long parentId);
}
