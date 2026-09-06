package standardNaast.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import standardNaast.backend.dto.SeasonCreateUpdateDto;
import standardNaast.backend.dto.SeasonDto;
import standardNaast.backend.dto.TeamDto;
import standardNaast.backend.exception.GlobalExceptionHandler;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.service.SeasonService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SeasonControllerIntegrationTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private SeasonService seasonService;

    @InjectMocks
    private SeasonController seasonController;

    private SeasonDto sampleSeasonDto;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        this.mockMvc = MockMvcBuilders.standaloneSetup(this.seasonController)
                .setMessageConverters(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(this.objectMapper))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        this.sampleSeasonDto = new SeasonDto(
                "2024-2025",
                LocalDate.of(2024, 7, 1),
                LocalDate.of(2025, 6, 30),
                LocalDate.of(2024, 7, 28),
                true,
                BigDecimal.valueOf(15.00)
        );
    }

    @Test
    void getAllSeasons_shouldReturnSeasonList() throws Exception {
        when(this.seasonService.getAllSeasons()).thenReturn(List.of(this.sampleSeasonDto));

        this.mockMvc.perform(get("/api/seasons")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is("2024-2025")))
                .andExpect(jsonPath("$[0].european", is(true)));
    }

    @Test
    void getSeasonById_whenExists_shouldReturnSeason() throws Exception {
        when(this.seasonService.getSeasonById("2024-2025")).thenReturn(this.sampleSeasonDto);

        this.mockMvc.perform(get("/api/seasons/{id}", "2024-2025")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("2024-2025")));
    }

    @Test
    void getSeasonById_whenNotFound_shouldReturn404() throws Exception {
        when(this.seasonService.getSeasonById("9999-9999"))
                .thenThrow(new ResourceNotFoundException("Saison non trouvée"));

        this.mockMvc.perform(get("/api/seasons/{id}", "9999-9999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCurrentSeason_whenExists_shouldReturnCurrent() throws Exception {
        when(this.seasonService.getCurrentSeason()).thenReturn(Optional.of(this.sampleSeasonDto));

        this.mockMvc.perform(get("/api/seasons/current")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("2024-2025")));
    }

    @Test
    void createSeason_shouldReturnCreated() throws Exception {
        final SeasonCreateUpdateDto createDto = new SeasonCreateUpdateDto(
                "2024-2025",
                LocalDate.of(2024, 7, 1),
                LocalDate.of(2025, 6, 30),
                LocalDate.of(2024, 7, 28),
                true,
                BigDecimal.valueOf(15.00)
        );

        when(this.seasonService.createSeason(any(SeasonCreateUpdateDto.class))).thenReturn(this.sampleSeasonDto);

        this.mockMvc.perform(post("/api/seasons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("2024-2025")));
    }

    @Test
    void updateSeason_shouldReturnUpdated() throws Exception {
        final SeasonCreateUpdateDto updateDto = new SeasonCreateUpdateDto(
                "2024-2025",
                LocalDate.of(2024, 7, 1),
                LocalDate.of(2025, 6, 30),
                LocalDate.of(2024, 7, 28),
                false,
                BigDecimal.valueOf(20.00)
        );

        final SeasonDto updatedDto = new SeasonDto(
                "2024-2025",
                LocalDate.of(2024, 7, 1),
                LocalDate.of(2025, 6, 30),
                LocalDate.of(2024, 7, 28),
                false,
                BigDecimal.valueOf(20.00)
        );

        when(this.seasonService.updateSeason(eq("2024-2025"), any(SeasonCreateUpdateDto.class))).thenReturn(updatedDto);

        this.mockMvc.perform(put("/api/seasons/{id}", "2024-2025")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.european", is(false)));
    }

    @Test
    void deleteSeason_shouldReturnNoContent() throws Exception {
        doNothing().when(this.seasonService).deleteSeason("2024-2025");

        this.mockMvc.perform(delete("/api/seasons/{id}", "2024-2025"))
                .andExpect(status().isNoContent());

        verify(this.seasonService).deleteSeason("2024-2025");
    }

    @Test
    void getTeamsForSeason_shouldReturnTeamList() throws Exception {
        final TeamDto teamDto = new TeamDto(1L, "Anderlecht");
        when(this.seasonService.getTeamsForSeason("2024-2025")).thenReturn(List.of(teamDto));

        this.mockMvc.perform(get("/api/seasons/{id}/teams", "2024-2025")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Anderlecht")));
    }
}
