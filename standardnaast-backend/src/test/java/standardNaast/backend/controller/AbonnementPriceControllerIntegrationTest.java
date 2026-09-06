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
import standardNaast.backend.domain.CompetitionType;
import standardNaast.backend.domain.PersonType;
import standardNaast.backend.dto.AbonnementPriceCreateUpdateDto;
import standardNaast.backend.dto.AbonnementPriceDto;
import standardNaast.backend.exception.GlobalExceptionHandler;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.service.AbonnementPriceService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AbonnementPriceControllerIntegrationTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AbonnementPriceService abonnementPriceService;

    @InjectMocks
    private AbonnementPriceController abonnementPriceController;

    private AbonnementPriceDto sampleDto;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        this.mockMvc = MockMvcBuilders.standaloneSetup(this.abonnementPriceController)
                .setMessageConverters(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(this.objectMapper))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        this.sampleDto = new AbonnementPriceDto(
                1L,
                "2024-2025",
                250L,
                1,
                "T1",
                PersonType.ADULT,
                CompetitionType.CHAMPIONSHIP
        );
    }

    @Test
    void getPricesBySeason_shouldReturnList() throws Exception {
        when(this.abonnementPriceService.getPricesBySeason("2024-2025")).thenReturn(List.of(this.sampleDto));

        this.mockMvc.perform(get("/api/abonnement-prices")
                        .param("seasonId", "2024-2025")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].bloc", is("T1")));
    }

    @Test
    void getPriceById_whenExists_shouldReturnPrice() throws Exception {
        when(this.abonnementPriceService.getPriceById(1L)).thenReturn(this.sampleDto);

        this.mockMvc.perform(get("/api/abonnement-prices/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.price", is(250)));
    }

    @Test
    void getPriceById_whenNotFound_shouldReturn404() throws Exception {
        when(this.abonnementPriceService.getPriceById(99L))
                .thenThrow(new ResourceNotFoundException("Price not found"));

        this.mockMvc.perform(get("/api/abonnement-prices/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPrice_shouldReturnCreated() throws Exception {
        AbonnementPriceCreateUpdateDto createDto = new AbonnementPriceCreateUpdateDto(
                "2024-2025",
                250L,
                1,
                "T1",
                PersonType.ADULT,
                CompetitionType.CHAMPIONSHIP
        );

        when(this.abonnementPriceService.createPrice(any(AbonnementPriceCreateUpdateDto.class))).thenReturn(this.sampleDto);

        this.mockMvc.perform(post("/api/abonnement-prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void updatePrice_shouldReturnUpdated() throws Exception {
        AbonnementPriceCreateUpdateDto updateDto = new AbonnementPriceCreateUpdateDto(
                "2024-2025",
                300L,
                1,
                "T1",
                PersonType.ADULT,
                CompetitionType.CHAMPIONSHIP
        );

        when(this.abonnementPriceService.updatePrice(eq(1L), any(AbonnementPriceCreateUpdateDto.class))).thenReturn(this.sampleDto);

        this.mockMvc.perform(put("/api/abonnement-prices/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deletePrice_shouldReturnNoContent() throws Exception {
        doNothing().when(this.abonnementPriceService).deletePrice(1L);

        this.mockMvc.perform(delete("/api/abonnement-prices/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(this.abonnementPriceService).deletePrice(1L);
    }
}
