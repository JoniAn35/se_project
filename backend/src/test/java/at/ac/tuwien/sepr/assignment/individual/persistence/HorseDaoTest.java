package at.ac.tuwien.sepr.assignment.individual.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;

import java.time.LocalDate;
import java.util.List;

import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for {@link HorseDao}, ensuring database operations function correctly.
 */
@ActiveProfiles({"test", "datagen"}) // Enables "test" Spring profile to load test data
@SpringBootTest
public class HorseDaoTest extends TestBase {

  @Autowired
  HorseDao horseDao;

  /**
   * Test retrieving all stored horses, returns exact number of entries
   * and verifies that specific horses exist in the test dataset.
   */
  @Test
  public void getAllReturnsAllStoredHorses() {
    List<Horse> horses = horseDao.getAll();
    assertThat(horses)
            .hasSize(15)  // Based on the 15 horses in test data
            .extracting(Horse::id, Horse::name)
            .contains(
                    tuple(-1L, "Wendy"),
                    tuple(-2L, "Rocky"),
                    tuple(-3L, "Bella"),
                    tuple(-4L, "Storm"),
                    tuple(-5L, "Coco"),
                    tuple(-6L, "Grace"),
                    tuple(-7L, "Midnight"),
                    tuple(-8L, "Princess"),
                    tuple(-9L, "Daisy"),
                    tuple(-10L, "Spirit"),
                    tuple(-11L, "Misty"),
                    tuple(-12L, "Lily"),
                    tuple(-13L, "Comet"),
                    tuple(-14L, "Rosie"),
                    tuple(-15L, "Bolt")
            );
  }
  /**
   * Test creating a new horse in the database.
   * Verifies that the horse is stored with correct attributes and receives a generated ID.
   * This is a positive test with a write operation.
   */
  @Test
  public void testCreateHorse() throws NotFoundException {
    HorseCreateDto newHorse = new HorseCreateDto(
            "Bella",
            "A gentle mare",
            LocalDate.of(2016, 11, 8),
            Sex.FEMALE,
            null
    );

    Horse created = horseDao.create(newHorse);

    assertThat(created)
            .isNotNull();
    assertThat(created.id())
            .isPositive();
    assertThat(created.name())
            .isEqualTo("Bella");
    assertThat(created.description())
            .isEqualTo("A gentle mare");
    assertThat(created.dateOfBirth())
            .isEqualTo(LocalDate.of(2016, 11, 8));
    assertThat(created.sex())
            .isEqualTo(Sex.FEMALE);
    assertThat(created.ownerId())
            .isNull();

    // Verify the horse was actually stored by retrieving it
    Horse retrieved = horseDao.getById(created.id());
    assertThat(retrieved)
            .isNotNull()
            .isEqualTo(created);
  }

  /**
   * Test retrieving a non-existent horse by ID.
   * Verifies that a NotFoundException is thrown when trying to access a horse that doesn't exist.
   * This is a negative test.
   */
  @Test
  public void testGetHorseNotFound() {
    assertThrows(NotFoundException.class, () -> {
      horseDao.getById(999L);
    });
  }

  /**
   * Test updating an existing horse in the database.
   * Verifies that all fields can be updated and persisted correctly.
   * This is a positive test with a write operation.
   */
  @Test
  public void testUpdateHorse() throws NotFoundException {
    // Get an existing horse
    Horse horse = horseDao.getById(-1L);  // Wendy
    assertThat(horse).isNotNull();

    // Create updated horse entity
    Horse updatedHorse = new Horse(
            horse.id(),
            "Wendy Updated",
            "The famous one - updated!",
            LocalDate.of(2012, 12, 12),
            Sex.FEMALE,
            horse.ownerId()
    );

    // Update in database
    Horse result = horseDao.update(updatedHorse);

    assertThat(result)
            .isNotNull();
    assertThat(result.name())
            .isEqualTo("Wendy Updated");
    assertThat(result.description())
            .isEqualTo("The famous one - updated!");

    // Verify update persisted
    Horse retrieved = horseDao.getById(horse.id());
    assertThat(retrieved.name())
            .isEqualTo("Wendy Updated");
    assertThat(retrieved.description())
            .isEqualTo("The famous one - updated!");
  }

  /**
   * Test deleting a non-existent horse.
   * Verifies that a NotFoundException is thrown when trying to delete a horse that doesn't exist.
   * This is a negative test.
   */
  @Test
  public void testDeleteNonExistentHorse() {
    assertThrows(NotFoundException.class, () -> {
      horseDao.delete(999L);
    });
  }
}
