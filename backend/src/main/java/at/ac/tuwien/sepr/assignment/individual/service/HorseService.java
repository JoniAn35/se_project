package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

import java.util.List;
import java.util.stream.Stream;

/**
 * Service for working with horses.
 */
public interface HorseService {
  /**
   * Lists all horses stored in the system.
   *
   * @return list of all stored horses
   */
  Stream<HorseListDto> allHorses();

  /**
   * Creates a horse with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to create
   * @return the created horse
   * @throws ValidationException if the horse could not be created because the data given is in itself incorrect (description too long, no name, …)
   * @throws ConflictException if the update data given for the horse is in conflict the data currently in the system (owner does not exist, …)
   */
  HorseDetailDto create(
      HorseCreateDto horse
  ) throws ValidationException, ConflictException;

  /**
   * Get the horse with given ID, with more detail information.
   * This includes the owner of the horse, and its parents.
   * The parents of the parents are not included.
   *
   * @param id the ID of the horse to get
   * @return the horse with ID {@code id}
   * @throws NotFoundException if the horse with the given ID does not exist in the persistent data store
   */
  HorseDetailDto getById(long id) throws NotFoundException;

  /**
   * Updates an existing horse in the system.
   *
   * @param id the ID of the horse to update
   * @param horse the updated horse data
   * @return the updated horse
   * @throws NotFoundException if the horse with the given ID does not exist
   * @throws ValidationException if the update data is invalid
   * @throws ConflictException if the update data conflicts with existing data
   */
  HorseDetailDto update(long id, HorseCreateDto horse)
          throws NotFoundException, ValidationException, ConflictException;

  /**
   * Deletes a horse from the system.
   *
   * @param id the ID of the horse to delete
   * @throws NotFoundException if the horse with the given ID does not exist
   */
  void delete(long id) throws NotFoundException;

  /**
   * Searches for horses matching the given criteria.
   *
   * @param searchCriteria the search parameters
   * @return a stream of horses matching the criteria
   */
  Stream<HorseListDto> search(HorseSearchDto searchCriteria);

  /**
   * Gets the parent horses of a given horse.
   *
   * @param horseId the ID of the horse
   * @return a list of parent horses
   * @throws NotFoundException if the horse does not exist
   */
  List<HorseDetailDto> getParents(long horseId) throws NotFoundException;

  /**
   * Adds a parent relationship between two horses.
   *
   * @param horseId the ID of the child horse
   * @param parentId the ID of the parent horse
   * @throws NotFoundException if either horse does not exist
   * @throws ConflictException if the parents have the same sex (required: different sex)
   */
  void addParent(long horseId, long parentId) throws NotFoundException, ConflictException;

  /**
   * Removes a parent relationship between two horses.
   *
   * @param horseId the ID of the child horse
   * @param parentId the ID of the parent horse
   */
  void removeParent(long horseId, long parentId);
}
