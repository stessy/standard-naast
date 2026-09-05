package standardNaast.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import standardNaast.backend.domain.PersonTravelType;
import standardNaast.backend.domain.Place;
import standardNaast.backend.dto.TravelPriceCreateUpdateDto;
import standardNaast.backend.dto.TravelPriceDto;
import standardNaast.backend.exception.GlobalExceptionHandler;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.service.TravelPriceService;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TravelPriceControllerIntegrationTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private TravelPriceService travelPriceService;

    @InjectMocks
    private TravelPriceController travelPriceController;

    private TravelPriceDto sampleDto;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();

        this.mockMvc = MockMvcBuilders.standaloneSetup(this.travelPriceController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        this.sampleDto = new TravelPriceDto(
                1L,
                "2024-2025",
                new BigDecimal("15.00"),
                Place.AWAY,
                true,
                PersonTravelType.MAJOR
        );
    }

    @Test
    void getAllTravelPrices_shouldReturnPagedPrices() throws Exception {
        when(this.travelPriceService.getAllTravelPrices(isNull(), isNull(), isNull(), any()))
                .thenReturn(new PageImpl<>(List.of(this.sampleDto)));

        this.mockMvc.perform(get("/api/travel-prices")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].seasonId", is("2024-2025")))
                .andExpect(jsonPath("$.content[0].place", is("AWAY")))
                .andExpect(jsonPath("$.content[0].membre", is(true)));
    }

    @Test
    void getTravelPricesBySeason_shouldReturnList() throws Exception {
        when(this.travelPriceService.getTravelPricesBySeason("2024-2025"))
                .thenReturn(List.of(this.sampleDto));

        this.mockMvc.perform(get("/api/travel-prices/season/{seasonId}", "2024-2025")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].seasonId", is("2024-2025")));
    }

    @Test
    void getTravelPriceById_whenExists_shouldReturnPrice() throws Exception {
        when(this.travelPriceService.getTravelPriceById(1L)).thenReturn(this.sampleDto);

        this.mockMvc.perform(get("/api/travel-prices/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.seasonId", is("2024-2025")));
    }

    @Test
    void getTravelPriceById_whenNotFound_shouldReturn404() throws Exception {
        when(this.travelPriceService.getTravelPriceById(999L))
                .thenThrow(new ResourceNotFoundException("Tarif non trouvé"));

        this.mockMvc.perform(get("/api/travel-prices/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTravelPrice_shouldReturnCreated() throws Exception {
        final TravelPriceCreateUpdateDto createDto = new TravelPriceCreateUpdateDto(
                "2024-2025",
                new BigDecimal("15.00"),
                Place.AWAY,
                true,
                PersonTravelType.MAJOR
        );

        when(this.travelPriceService.createTravelPrice(any(TravelPriceCreateUpdateDto.class))).thenReturn(this.sampleDto);

        this.mockMvc.perform(post("/api/travel-prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.seasonId", is("2024-2025")));
    }

    @Test
    void updateTravelPrice_shouldReturnUpdated() throws Exception {
        final TravelPriceCreateUpdateDto updateDto = new TravelPriceCreateUpdateDto(
                "2024-2025",
                new BigDecimal("20.00"),
                Place.AWAY,
                true,
                PersonTravelType.MAJOR
        );

        final TravelPriceDto updatedDto = new TravelPriceDto(
                1L,
                "2024-2025",
                new BigDecimal("20.00"),
                Place.AWAY,
                true,
                PersonTravelType.MAJOR
        );

        when(this.travelPriceService.updateTravelPrice(eq(1L), any(TravelPriceCreateUpdateDto.class))).thenReturn(updatedDto);

        this.mockMvc.perform(put("/api/travel-prices/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.montant", is(20.00)));
    }

    @Test
    void deleteTravelPrice_shouldReturnNoContent() throws Exception {
        doNothing().when(this.travelPriceService).deleteTravelPrice(1L);

        this.mockMvc.perform(delete("/api/travel-prices/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(this.travelPriceService).deleteTravelPrice(1L);
    }
}
