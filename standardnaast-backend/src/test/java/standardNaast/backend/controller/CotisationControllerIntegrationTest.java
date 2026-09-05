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
import standardNaast.backend.dto.CotisationCreateUpdateDto;
import standardNaast.backend.dto.CotisationDto;
import standardNaast.backend.exception.GlobalExceptionHandler;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.service.CotisationService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CotisationControllerIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private CotisationService cotisationService;

    @InjectMocks
    private CotisationController cotisationController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cotisationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void shouldGetAllCotisations() throws Exception {
        CotisationDto cot1 = new CotisationDto(2023L, BigDecimal.valueOf(20.0));
        CotisationDto cot2 = new CotisationDto(2024L, BigDecimal.valueOf(25.0));
        when(cotisationService.getAllCotisations()).thenReturn(List.of(cot1, cot2));

        mockMvc.perform(get("/api/cotisations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].anneeCotisation").value(2023))
                .andExpect(jsonPath("$[1].anneeCotisation").value(2024));
    }

    @Test
    void shouldGetCotisationByYear() throws Exception {
        CotisationDto cot = new CotisationDto(2024L, BigDecimal.valueOf(25.0));
        when(cotisationService.getCotisationByYear(2024L)).thenReturn(cot);

        mockMvc.perform(get("/api/cotisations/2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.anneeCotisation").value(2024))
                .andExpect(jsonPath("$.montantCotisation").value(25.0));
    }

    @Test
    void shouldReturn404WhenCotisationNotFound() throws Exception {
        when(cotisationService.getCotisationByYear(9999L))
                .thenThrow(new ResourceNotFoundException("Cotisation non trouvée pour l'année : 9999"));

        mockMvc.perform(get("/api/cotisations/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateCotisation() throws Exception {
        CotisationCreateUpdateDto input = new CotisationCreateUpdateDto(2025L, BigDecimal.valueOf(30.0));
        CotisationDto output = new CotisationDto(2025L, BigDecimal.valueOf(30.0));
        when(cotisationService.createCotisation(any(CotisationCreateUpdateDto.class))).thenReturn(output);

        mockMvc.perform(post("/api/cotisations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.anneeCotisation").value(2025))
                .andExpect(jsonPath("$.montantCotisation").value(30.0));
    }

    @Test
    void shouldUpdateCotisation() throws Exception {
        CotisationCreateUpdateDto input = new CotisationCreateUpdateDto(2024L, BigDecimal.valueOf(28.0));
        CotisationDto output = new CotisationDto(2024L, BigDecimal.valueOf(28.0));
        when(cotisationService.updateCotisation(eq(2024L), any(CotisationCreateUpdateDto.class))).thenReturn(output);

        mockMvc.perform(put("/api/cotisations/2024")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.montantCotisation").value(28.0));
    }

    @Test
    void shouldDeleteCotisation() throws Exception {
        doNothing().when(cotisationService).deleteCotisation(2024L);

        mockMvc.perform(delete("/api/cotisations/2024"))
                .andExpect(status().isNoContent());

        verify(cotisationService).deleteCotisation(2024L);
    }
}
