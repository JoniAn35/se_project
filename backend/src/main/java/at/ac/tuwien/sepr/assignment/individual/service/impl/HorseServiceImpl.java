package at.ac.tuwien.sepr.assignment.individual.service.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.HorseMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;
import at.ac.tuwien.sepr.assignment.individual.service.OwnerService;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link HorseService} for handling image storage and retrieval.
 */
@Service
public class HorseServiceImpl implements HorseService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseDao dao;
  private final HorseMapper mapper;
  private final HorseValidator validator;
  private final OwnerService ownerService;


  @Autowired
  public HorseServiceImpl(HorseDao dao,
                          HorseMapper mapper,
                          HorseValidator validator,
                          OwnerService ownerService) {
    this.dao = dao;
    this.mapper = mapper;
    this.validator = validator;
    this.ownerService = ownerService;
  }

  @Override
  public Stream<HorseListDto> allHorses() {
    LOG.trace("allHorses()");
    var horses = dao.getAll();
    var ownerIds = horses.stream()
        .map(Horse::ownerId)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableSet());
    Map<Long, OwnerDto> ownerMap;
    try {
      ownerMap = ownerService.getAllById(ownerIds);
    } catch (NotFoundException e) {
      throw new FatalException("Horse, that is already persisted, refers to non-existing owner", e);
    }
    return horses.stream()
        .map(horse -> mapper.entityToListDto(horse, ownerMap));
  }

  @Override
  public HorseDetailDto create(
      HorseCreateDto horse
  ) throws ValidationException, ConflictException {
    LOG.trace("create({})", horse);
    validator.validateForCreate(horse);
    var newHorse = dao.create(
        horse
    );
    var ownerMap = ownerMapForSingleId(newHorse.ownerId());
    return mapper.entityToDetailDto(
        newHorse,
        ownerMap);
  }

  @Override
  public HorseDetailDto getById(long id) throws NotFoundException {
    LOG.trace("details({})", id);
    Horse horse = dao.getById(id);
    return mapper.entityToDetailDto(
        horse,
        ownerMapForSingleId(horse.ownerId()));
  }

  @Override
  public HorseDetailDto update(long id, HorseCreateDto horseDto)
          throws NotFoundException, ValidationException, ConflictException {
    LOG.trace("update({}, {})", id, horseDto);
    validator.validateForCreate(horseDto);

    // Get existing horse to verify it exists
    Horse existingHorse = dao.getById(id);

    // Create updated horse entity
    Horse updatedHorse = new Horse(
            id,
            horseDto.name(),
            horseDto.description(),
            horseDto.dateOfBirth(),
            horseDto.sex(),
            horseDto.ownerId()
    );

    // Update in database
    Horse result = dao.update(updatedHorse);
    return mapper.entityToDetailDto(result, ownerMapForSingleId(result.ownerId()));
  }

  @Override
  public void delete(long id) throws NotFoundException {
    LOG.trace("delete({})", id);
    // Verify horse exists
    dao.getById(id);
    dao.delete(id);
  }

  @Override
  public Stream<HorseListDto> search(HorseSearchDto searchCriteria) {
    LOG.trace("search({})", searchCriteria);
    var horses = dao.search(searchCriteria);
    var ownerIds = horses.stream()
            .map(Horse::ownerId)
            .filter(Objects::nonNull)
            .collect(Collectors.toUnmodifiableSet());
    Map<Long, OwnerDto> ownerMap;
    try {
      ownerMap = ownerService.getAllById(ownerIds);
    } catch (NotFoundException e) {
      throw new FatalException("Horse, that is already persisted, refers to non-existing owner", e);
    }
    return horses.stream()
            .map(horse -> mapper.entityToListDto(horse, ownerMap));
  }

  @Override
  public List<HorseDetailDto> getParents(long horseId) throws NotFoundException {
    LOG.trace("getParents({})", horseId);
    // Verify horse exists
    dao.getById(horseId);

    var parentHorses = dao.getParents(horseId);
    var ownerIds = parentHorses.stream()
            .map(Horse::ownerId)
            .filter(Objects::nonNull)
            .collect(Collectors.toUnmodifiableSet());
    Map<Long, OwnerDto> ownerMap;
    try {
      ownerMap = ownerService.getAllById(ownerIds);
    } catch (NotFoundException e) {
      throw new FatalException("Horse, that is already persisted, refers to non-existing owner", e);
    }
    return parentHorses.stream()
            .map(horse -> mapper.entityToDetailDto(horse, ownerMap))
            .collect(Collectors.toList());
  }

  @Override
  public void addParent(long horseId, long parentId) throws NotFoundException, ConflictException {
    LOG.trace("addParent({}, {})", horseId, parentId);

    // Verify both horses exist
    Horse horse = dao.getById(horseId);
    Horse parent = dao.getById(parentId);

    // Check that parents have different sexes
    if (horse.sex() == parent.sex()) {
      throw new ConflictException(
              "Parent and horse must have different sexes",
              java.util.List.of("Horse and parent have the same sex: " + horse.sex())
      );
    }

    dao.addParent(horseId, parentId);
  }

  @Override
  public void removeParent(long horseId, long parentId) {
    LOG.trace("removeParent({}, {})", horseId, parentId);
    dao.removeParent(horseId, parentId);
  }


  private Map<Long, OwnerDto> ownerMapForSingleId(Long ownerId) {
    try {
      return ownerId == null
          ? null
          : Collections.singletonMap(ownerId, ownerService.getById(ownerId));
    } catch (NotFoundException e) {
      throw new FatalException("Owner %d referenced by horse not found".formatted(ownerId));
    }
  }

}
