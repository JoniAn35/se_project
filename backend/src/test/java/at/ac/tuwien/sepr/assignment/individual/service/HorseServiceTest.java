package at.ac.tuwien.sepr.assignment.individual.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for {@link HorseService}.
 */
@ActiveProfiles({"test", "datagen"}) // Enables "test" Spring profile during test execution
@SpringBootTest
public class HorseServiceTest extends TestBase {

  @Autowired
  HorseService horseService;

  /**
   * Test retrieving all stored horses through the service.
   * Verifies that the service returns the expected number of horses
   * and that specific horses with correct attributes exist in the test dataset.
   */
  @Test
  public void getAllReturnsAllStoredHorses() {
    List<HorseListDto> horses = horseService.allHorses()
            .toList();

    assertThat(horses)
            .hasSize(15)
            .extracting(HorseListDto::id, HorseListDto::name, HorseListDto::sex)
            .contains(
                    tuple(-1L, "Wendy", Sex.FEMALE),
                    tuple(-2L, "Rocky", Sex.MALE),
                    tuple(-3L, "Bella", Sex.FEMALE),
                    tuple(-4L, "Storm", Sex.MALE),
                    tuple(-5L, "Coco", Sex.FEMALE),
                    tuple(-6L, "Grace", Sex.FEMALE),
                    tuple(-7L, "Midnight", Sex.MALE),
                    tuple(-8L, "Princess", Sex.FEMALE),
                    tuple(-9L, "Daisy", Sex.FEMALE),
                    tuple(-10L, "Spirit", Sex.MALE),
                    tuple(-11L, "Misty", Sex.FEMALE),
                    tuple(-12L, "Lily", Sex.FEMALE),
                    tuple(-13L, "Comet", Sex.MALE),
                    tuple(-14L, "Rosie", Sex.FEMALE),
                    tuple(-15L, "Bolt", Sex.MALE)
            );
  }

  /**
   * Test creating a valid horse through the service.
   * Verifies that the service correctly validates and creates a horse.
   * This is a positive test with a write operation.
   */
  @Test
  public void testCreateHorseValid() throws ValidationException, ConflictException {
    HorseCreateDto newHorse = new HorseCreateDto(
            "Storm",
            "A gray horse with a spirited personality",
            LocalDate.of(2017, 5, 10),
            Sex.MALE,
            null
    );

    HorseDetailDto created = horseService.create(newHorse);

    assertThat(created)
            .isNotNull();
    assertThat(created.id())
            .isPositive();
    assertThat(created.name())
            .isEqualTo("Storm");
    assertThat(created.description())
            .isEqualTo("A gray horse with a spirited personality");
    assertThat(created.dateOfBirth())
            .isEqualTo(LocalDate.of(2017, 5, 10));
    assertThat(created.sex())
            .isEqualTo(Sex.MALE);
  }

  /**
   * Test updating an existing horse through the service.
   * Verifies that the service correctly validates and updates a horse with new data.
   * This is a positive test with a write operation.
   */
  @Test
  public void testUpdateHorseValid() throws NotFoundException, ValidationException, ConflictException {
    // Get an existing horse
    HorseDetailDto horse = horseService.getById(-1L);  // Wendy
    assertThat(horse).isNotNull();

    // Update horse data
    HorseCreateDto updatedData = new HorseCreateDto(
            "Wendy Updated",
            "The famous one - now updated!",
            LocalDate.of(2012, 12, 12),
            Sex.FEMALE,
            null
    );

    // Update through service
    HorseDetailDto updated = horseService.update(horse.id(), updatedData);

    assertThat(updated)
            .isNotNull();
    assertThat(updated.id())
            .isEqualTo(horse.id());
    assertThat(updated.name())
            .isEqualTo("Wendy Updated");
    assertThat(updated.description())
            .isEqualTo("The famous one - now updated!");

    // Verify update persisted by retrieving again
    HorseDetailDto retrieved = horseService.getById(horse.id());
    assertThat(retrieved.name())
            .isEqualTo("Wendy Updated");
    assertThat(retrieved.description())
            .isEqualTo("The famous one - now updated!");
  }

  /**
   * Test creating a horse with invalid (missing) name.
   * Verifies that the service throws ValidationException for missing name.
   * This is a negative test.
   */
  @Test
  public void testCreateHorseInvalidName() {
    HorseCreateDto invalidHorse = new HorseCreateDto(
            null,  // Invalid: name is required
            "A horse with no name",
            LocalDate.of(2017, 5, 10),
            Sex.MALE,
            null
    );

    assertThrows(ValidationException.class, () -> {
      horseService.create(invalidHorse);
    });
  }

  /**
   * Test retrieving a non-existent horse through the service.
   * Verifies that the service throws NotFoundException for missing horse.
   * This is a negative test.
   */
  @Test
  public void testGetHorseNotFound() {
    assertThrows(NotFoundException.class, () -> {
      horseService.getById(999L);
    });
  }
}
