package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for managing horse-related operations.
 * Provides endpoints for searching, retrieving, creating, updating, and deleting horses,
 * as well as managing their family tree.
 */
@RestController
@RequestMapping(path = HorseEndpoint.BASE_PATH)
public class HorseEndpoint {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  static final String BASE_PATH = "/horses";

  private final HorseService service;

  @Autowired
  public HorseEndpoint(HorseService service) {
    this.service = service;
  }

  /**
   * Searches for horses based on the given search parameters.
   *
   * @param searchParameters the parameters to filter the horse search
   * @return a stream of {@link HorseListDto} matching the search criteria
   */
  @GetMapping
  public Stream<HorseListDto> searchHorses(HorseSearchDto searchParameters) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("request parameters: {}", searchParameters);
    return service.search(searchParameters);
  }

  /**
   * Retrieves the details of a horse by its ID.
   *
   * @param id the unique identifier of the horse
   * @return the detailed information of the requested horse
   */
  @GetMapping("{id}")
  public HorseDetailDto getById(@PathVariable("id") long id) {
    LOG.info("GET " + BASE_PATH + "/{}", id);
    try {
      return service.getById(id);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to get details of not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  /**
   * Creates a new horse entry in the system.
   *
   * @param toCreate the horse data to be created
   * @return the created horse details
   */
  @PostMapping
  public HorseDetailDto create(@RequestBody HorseCreateDto toCreate) {
    LOG.info("POST " + BASE_PATH);
    LOG.debug("Request body: {}", toCreate);
    try {
      return service.create(toCreate);
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      logClientError(status, "Horse creation validation failed", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ConflictException e) {
      HttpStatus status = HttpStatus.CONFLICT;
      logClientError(status, "Horse creation conflict", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  /**
   * Updates an existing horse.
   *
   * @param id the ID of the horse to update
   * @param toUpdate the updated horse data
   * @return the updated horse details
   */
  @PutMapping("{id}")
  public HorseDetailDto update(
          @PathVariable("id") long id,
          @RequestBody HorseCreateDto toUpdate) {
    LOG.info("PUT " + BASE_PATH + "/{}", id);
    LOG.debug("Request body: {}", toUpdate);
    try {
      return service.update(id, toUpdate);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to update not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      logClientError(status, "Horse update validation failed", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ConflictException e) {
      HttpStatus status = HttpStatus.CONFLICT;
      logClientError(status, "Horse update conflict", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  /**
   * Deletes a horse from the system.
   *
   * @param id the ID of the horse to delete
   */
  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") long id) {
    LOG.info("DELETE " + BASE_PATH + "/{}", id);
    try {
      service.delete(id);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to delete not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  /**
   * Retrieves all parent horses of a given horse.
   *
   * @param horseId the ID of the horse
   * @return a list of parent horses
   */
  @GetMapping("{horseId}/parents")
  public List<HorseDetailDto> getParents(@PathVariable("horseId") long horseId) {
    LOG.info("GET " + BASE_PATH + "/{}/parents", horseId);
    try {
      return service.getParents(horseId);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  /**
   * Adds a parent relationship for a horse.
   *
   * @param horseId the ID of the child horse
   * @param parentId the ID of the parent horse
   */
  @PostMapping("{horseId}/parents/{parentId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void addParent(
          @PathVariable("horseId") long horseId,
          @PathVariable("parentId") long parentId) {
    LOG.info("POST " + BASE_PATH + "/{}/parents/{}", horseId, parentId);
    try {
      service.addParent(horseId, parentId);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse or parent not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ConflictException e) {
      HttpStatus status = HttpStatus.CONFLICT;
      logClientError(status, "Parent conflict - same sex", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  /**
   * Removes a parent relationship for a horse.
   *
   * @param horseId the ID of the child horse
   * @param parentId the ID of the parent horse
   */
  @DeleteMapping("{horseId}/parents/{parentId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeParent(
          @PathVariable("horseId") long horseId,
          @PathVariable("parentId") long parentId) {
    LOG.info("DELETE " + BASE_PATH + "/{}/parents/{}", horseId, parentId);
    service.removeParent(horseId, parentId);
  }

  /**
   * Logs client-side errors with relevant details.
   *
   * @param status  the HTTP status code of the error
   * @param message a brief message describing the error
   * @param e       the exception that occurred
   */
  private void logClientError(HttpStatus status, String message, Exception e) {
    LOG.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
  }

}
