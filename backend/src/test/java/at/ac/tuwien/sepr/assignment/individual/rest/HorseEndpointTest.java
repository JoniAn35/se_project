package at.ac.tuwien.sepr.assignment.individual.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;

import java.time.LocalDate;
import java.util.List;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tools.jackson.databind.json.JsonMapper;

/**
 * Integration tests for the Horse REST API endpoint.
 */
@ActiveProfiles({"test", "datagen"}) // Enables "test" Spring profile during test execution
@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
public class HorseEndpointTest extends TestBase {

  @Autowired
  private WebApplicationContext webAppContext;
  private MockMvc mockMvc;

  @Autowired
  private JsonMapper jsonMapper;

  /**
   * Sets up the MockMvc instance before each test.
   */
  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
  }

  /**
   * Test retrieving all horses from the endpoint.
   *
   * @throws Exception if the request fails
   */
  @Test
  public void gettingAllHorses() throws Exception {
    byte[] body = mockMvc
            .perform(MockMvcRequestBuilders
                    .get("/horses")
                    .accept(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

    List<HorseListDto> horseResult = jsonMapper.readerFor(HorseListDto.class).<HorseListDto>readValues(body).readAll();

    assertThat(horseResult)
            .isNotNull()
            .hasSizeGreaterThanOrEqualTo(1)
            .extracting(HorseListDto::id, HorseListDto::name)
            .contains(tuple(-1L, "Wendy"));

  }

  /**
   * Test that accessing a nonexistent URL returns a 404 status.
   *
   * @throws Exception if the request fails
   */
  @Test
  public void gettingNonexistentUrlReturns404() throws Exception {
    mockMvc
            .perform(MockMvcRequestBuilders
                    .get("/asdf123")
            ).andExpect(status().isNotFound());
  }

  /**
   * Test creating a new horse via REST endpoint.
   * Verifies that a POST request returns 201 Created and the horse is stored.
   * This is a positive test with a write operation.
   */
  @Test
  public void testCreateHorse() throws Exception {
    HorseCreateDto newHorse = new HorseCreateDto(
            "Princess",
            "An elegant white horse",
            LocalDate.of(2017, 2, 14),
            Sex.FEMALE,
            null
    );

    byte[] body = mockMvc
            .perform(MockMvcRequestBuilders
                    .post("/horses")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(newHorse))
            )
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsByteArray();

    HorseDetailDto createdHorse = jsonMapper.readValue(body, HorseDetailDto.class);

    assertThat(createdHorse)
            .isNotNull();
    assertThat(createdHorse.id())
            .isPositive();
    assertThat(createdHorse.name())
            .isEqualTo("Princess");
    assertThat(createdHorse.sex())
            .isEqualTo(Sex.FEMALE);
  }

  /**
   * Test creating a horse with invalid data via REST endpoint.
   * Verifies that a POST request with missing name returns 422 Unprocessable Entity.
   * This is a negative test.
   */
  @Test
  public void testCreateHorseInvalid() throws Exception {
    HorseCreateDto invalidHorse = new HorseCreateDto(
            null,  // Invalid: name is required
            "A horse with no name",
            LocalDate.of(2017, 2, 14),
            Sex.FEMALE,
            null
    );

    mockMvc
            .perform(MockMvcRequestBuilders
                    .post("/horses")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(invalidHorse))
            )
            .andExpect(status().isUnprocessableEntity());
  }

  /**
   * Test retrieving a non-existent horse via REST endpoint.
   * Verifies that a GET request for a non-existent horse returns 404 Not Found.
   * This is a negative test.
   */
  @Test
  public void testGetHorseNotFound() throws Exception {
    mockMvc
            .perform(MockMvcRequestBuilders
                    .get("/horses/999")
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound());
  }
}
