package standardNaast.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import standardNaast.backend.domain.CompetitionType;
import standardNaast.backend.domain.Place;
import standardNaast.backend.dto.MatchTravelOverviewDto;
import standardNaast.backend.dto.PersonTravelCreateDto;
import standardNaast.backend.dto.PersonTravelDto;
import standardNaast.backend.exception.GlobalExceptionHandler;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.service.TravelService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TravelControllerIntegrationTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private TravelService travelService;

    @InjectMocks
    private TravelController travelController;

    private PersonTravelDto sampleTravelDto;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        this.mockMvc = MockMvcBuilders.standaloneSetup(this.travelController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(this.objectMapper))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        this.sampleTravelDto = new PersonTravelDto(
                1001L,
                10L,
                42L,
                "Jean",
                "Dupont",
                true,
                501L,
                LocalDateTime.of(2024, 10, 20, 18, 30),
                Place.AWAY,
                "Anderlecht",
                CompetitionType.CHAMPIONSHIP,
                "2024-2025",
                15L
        );
    }

    @Test
    void searchTravels_shouldReturnPagedTravels() throws Exception {
        when(this.travelService.searchTravels(isNull(), isNull(), isNull(), any()))
                .thenReturn(new PageImpl<>(List.of(this.sampleTravelDto), org.springframework.data.domain.PageRequest.of(0, 20), 1));

        this.mockMvc.perform(get("/api/travels")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName", is("Jean")))
                .andExpect(jsonPath("$.content[0].opponentName", is("Anderlecht")))
                .andExpect(jsonPath("$.content[0].isMember", is(true)));
    }

    @Test
    void getTravelsByMatch_shouldReturnList() throws Exception {
        when(this.travelService.getTravelsByMatch(501L))
                .thenReturn(List.of(this.sampleTravelDto));

        this.mockMvc.perform(get("/api/travels/match/{matchId}", 501L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName", is("Jean")));
    }

    @Test
    void getTravelsByPerson_shouldReturnList() throws Exception {
        when(this.travelService.getTravelsByPerson(10L))
                .thenReturn(List.of(this.sampleTravelDto));

        this.mockMvc.perform(get("/api/travels/person/{personId}", 10L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].memberId", is(10)));
    }

    @Test
    void getMatchTravelOverview_shouldReturnOverview() throws Exception {
        final MatchTravelOverviewDto overviewDto = new MatchTravelOverviewDto(
                501L,
                "Anderlecht",
                "2024-2025",
                1,
                1,
                0,
                15L,
                List.of(this.sampleTravelDto),
                List.of()
        );

        when(this.travelService.getMatchTravelOverview(501L)).thenReturn(overviewDto);

        this.mockMvc.perform(get("/api/travels/match/{matchId}/overview", 501L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matchId", is(501)))
                .andExpect(jsonPath("$.opponentName", is("Anderlecht")))
                .andExpect(jsonPath("$.totalPassengers", is(1)))
                .andExpect(jsonPath("$.totalAmountCollected", is(15)));
    }

    @Test
    void countMemberAwayTravels_shouldReturnCount() throws Exception {
        when(this.travelService.countMemberAwayTravels("2024-2025", 10L)).thenReturn(5L);

        this.mockMvc.perform(get("/api/travels/count")
                        .param("seasonId", "2024-2025")
                        .param("personId", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void getTravelById_whenExists_shouldReturnTravel() throws Exception {
        when(this.travelService.getTravelById(1001L)).thenReturn(this.sampleTravelDto);

        this.mockMvc.perform(get("/api/travels/{id}", 1001L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1001)))
                .andExpect(jsonPath("$.firstName", is("Jean")));
    }

    @Test
    void getTravelById_whenNotFound_shouldReturn404() throws Exception {
        when(this.travelService.getTravelById(999L))
                .thenThrow(new ResourceNotFoundException("Inscription déplacement non trouvée"));

        this.mockMvc.perform(get("/api/travels/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void registerPersonTravel_shouldReturnCreated() throws Exception {
        final PersonTravelCreateDto createDto = new PersonTravelCreateDto(10L, 501L, 15L);

        when(this.travelService.registerPersonTravel(any(PersonTravelCreateDto.class))).thenReturn(this.sampleTravelDto);

        this.mockMvc.perform(post("/api/travels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1001)))
                .andExpect(jsonPath("$.firstName", is("Jean")));
    }

    @Test
    void removePersonTravel_shouldReturnNoContent() throws Exception {
        doNothing().when(this.travelService).removePersonTravel(1001L);

        this.mockMvc.perform(delete("/api/travels/{id}", 1001L))
                .andExpect(status().isNoContent());

        verify(this.travelService).removePersonTravel(1001L);
    }
}
