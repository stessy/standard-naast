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
import standardNaast.backend.domain.MatchType;
import standardNaast.backend.domain.Place;
import standardNaast.backend.domain.PriceType;
import standardNaast.backend.dto.MatchCreateUpdateDto;
import standardNaast.backend.dto.MatchDto;
import standardNaast.backend.exception.GlobalExceptionHandler;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.service.MatchService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MatchControllerIntegrationTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private MatchService matchService;

    @InjectMocks
    private MatchController matchController;

    private MatchDto sampleMatchDto;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        this.mockMvc = MockMvcBuilders.standaloneSetup(this.matchController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(this.objectMapper))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        this.sampleMatchDto = new MatchDto(
                1L,
                "2024-2025",
                10L,
                "RSC Anderlecht",
                LocalDateTime.of(2024, 10, 20, 18, 30),
                Place.HOME,
                CompetitionType.CHAMPIONSHIP,
                MatchType.GROUP_MATCH,
                PriceType.TOP
        );
    }

    @Test
    void getMatches_shouldReturnPagedMatches() throws Exception {
        when(this.matchService.getMatches(isNull(), isNull(), isNull(), any()))
                .thenReturn(new PageImpl<>(List.of(this.sampleMatchDto), org.springframework.data.domain.PageRequest.of(0, 20), 1));

        this.mockMvc.perform(get("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].opponentName", is("RSC Anderlecht")))
                .andExpect(jsonPath("$.content[0].place", is("HOME")));
    }

    @Test
    void getMatchesBySeason_shouldReturnList() throws Exception {
        when(this.matchService.getMatchesBySeason("2024-2025"))
                .thenReturn(List.of(this.sampleMatchDto));

        this.mockMvc.perform(get("/api/matches/by-season/{seasonId}", "2024-2025")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].opponentName", is("RSC Anderlecht")));
    }

    @Test
    void getMatchById_whenExists_shouldReturnMatch() throws Exception {
        when(this.matchService.getMatchById(1L)).thenReturn(this.sampleMatchDto);

        this.mockMvc.perform(get("/api/matches/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.opponentName", is("RSC Anderlecht")));
    }

    @Test
    void getMatchById_whenNotFound_shouldReturn404() throws Exception {
        when(this.matchService.getMatchById(999L))
                .thenThrow(new ResourceNotFoundException("Match non trouvé"));

        this.mockMvc.perform(get("/api/matches/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createMatch_shouldReturnCreated() throws Exception {
        final MatchCreateUpdateDto createDto = new MatchCreateUpdateDto(
                "2024-2025",
                10L,
                LocalDateTime.of(2024, 10, 20, 18, 30),
                Place.HOME,
                CompetitionType.CHAMPIONSHIP,
                MatchType.GROUP_MATCH,
                PriceType.TOP
        );

        when(this.matchService.createMatch(any(MatchCreateUpdateDto.class))).thenReturn(this.sampleMatchDto);

        this.mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.opponentName", is("RSC Anderlecht")));
    }

    @Test
    void updateMatch_shouldReturnUpdated() throws Exception {
        final MatchCreateUpdateDto updateDto = new MatchCreateUpdateDto(
                "2024-2025",
                10L,
                LocalDateTime.of(2024, 10, 20, 18, 30),
                Place.AWAY,
                CompetitionType.CHAMPIONSHIP,
                MatchType.GROUP_MATCH,
                PriceType.NORMAL
        );

        final MatchDto updatedDto = new MatchDto(
                1L,
                "2024-2025",
                10L,
                "RSC Anderlecht",
                LocalDateTime.of(2024, 10, 20, 18, 30),
                Place.AWAY,
                CompetitionType.CHAMPIONSHIP,
                MatchType.GROUP_MATCH,
                PriceType.NORMAL
        );

        when(this.matchService.updateMatch(eq(1L), any(MatchCreateUpdateDto.class))).thenReturn(updatedDto);

        this.mockMvc.perform(put("/api/matches/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.place", is("AWAY")))
                .andExpect(jsonPath("$.priceType", is("NORMAL")));
    }

    @Test
    void deleteMatch_shouldReturnNoContent() throws Exception {
        doNothing().when(this.matchService).deleteMatch(1L);

        this.mockMvc.perform(delete("/api/matches/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(this.matchService).deleteMatch(1L);
    }
}
