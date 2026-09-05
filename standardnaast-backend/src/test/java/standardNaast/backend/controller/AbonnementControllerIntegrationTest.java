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
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import standardNaast.backend.domain.AbonnementStatus;
import standardNaast.backend.domain.CompetitionType;
import standardNaast.backend.domain.PersonType;
import standardNaast.backend.dto.AbonnementCreateUpdateDto;
import standardNaast.backend.dto.AbonnementDto;
import standardNaast.backend.dto.AbonnementPriceDto;
import standardNaast.backend.dto.AbonnementStatusUpdateDto;
import standardNaast.backend.exception.GlobalExceptionHandler;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.service.AbonnementService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AbonnementControllerIntegrationTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AbonnementService abonnementService;

    @InjectMocks
    private AbonnementController abonnementController;

    private AbonnementDto sampleAbonnementDto;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        this.mockMvc = MockMvcBuilders.standaloneSetup(this.abonnementController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        AbonnementPriceDto priceDto = new AbonnementPriceDto(
                1L,
                "2024-2025",
                250L,
                1,
                "T1",
                PersonType.ADULT,
                CompetitionType.CHAMPIONSHIP
        );

        this.sampleAbonnementDto = new AbonnementDto(
                1L,
                priceDto,
                "A",
                "12",
                10L,
                true,
                50L,
                "2024-2025",
                100L,
                "Eden",
                "Hazard",
                10L,
                AbonnementStatus.NEW,
                "T1"
        );
    }

    @Test
    void getAbonnements_shouldReturnPagedAbonnements() throws Exception {
        when(this.abonnementService.getAbonnements(eq("2024-2025"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(this.sampleAbonnementDto)));

        this.mockMvc.perform(get("/api/abonnements")
                        .param("seasonId", "2024-2025")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].personFirstName", is("Eden")));
    }

    @Test
    void getAbonnementById_whenExists_shouldReturnAbonnement() throws Exception {
        when(this.abonnementService.getAbonnementById(1L)).thenReturn(this.sampleAbonnementDto);

        this.mockMvc.perform(get("/api/abonnements/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.place", is("12")));
    }

    @Test
    void getAbonnementById_whenNotFound_shouldReturn404() throws Exception {
        when(this.abonnementService.getAbonnementById(99L))
                .thenThrow(new ResourceNotFoundException("Abonnement not found"));

        this.mockMvc.perform(get("/api/abonnements/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createAbonnement_shouldReturnCreated() throws Exception {
        AbonnementCreateUpdateDto createDto = new AbonnementCreateUpdateDto(
                1L,
                "A",
                "12",
                10L,
                true,
                50L,
                "2024-2025",
                100L,
                AbonnementStatus.NEW,
                "T1"
        );

        when(this.abonnementService.createAbonnement(any(AbonnementCreateUpdateDto.class))).thenReturn(this.sampleAbonnementDto);

        this.mockMvc.perform(post("/api/abonnements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void updateAbonnement_shouldReturnUpdated() throws Exception {
        AbonnementCreateUpdateDto updateDto = new AbonnementCreateUpdateDto(
                1L,
                "A",
                "12",
                10L,
                true,
                50L,
                "2024-2025",
                100L,
                AbonnementStatus.NEW,
                "T1"
        );

        when(this.abonnementService.updateAbonnement(eq(1L), any(AbonnementCreateUpdateDto.class))).thenReturn(this.sampleAbonnementDto);

        this.mockMvc.perform(put("/api/abonnements/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteAbonnement_shouldReturnNoContent() throws Exception {
        doNothing().when(this.abonnementService).deleteAbonnement(1L);

        this.mockMvc.perform(delete("/api/abonnements/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(this.abonnementService).deleteAbonnement(1L);
    }

    @Test
    void updateStatus_shouldReturnOk() throws Exception {
        AbonnementStatusUpdateDto dto = new AbonnementStatusUpdateDto(
                List.of(1L, 2L),
                AbonnementStatus.PURCHASED
        );

        doNothing().when(this.abonnementService).updateStatus(List.of(1L, 2L), AbonnementStatus.PURCHASED);

        this.mockMvc.perform(patch("/api/abonnements/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(this.abonnementService).updateStatus(List.of(1L, 2L), AbonnementStatus.PURCHASED);
    }

    @Test
    void updatePaymentStatus_shouldReturnOk() throws Exception {
        doNothing().when(this.abonnementService).updatePaymentStatus(List.of(1L, 2L), true);

        this.mockMvc.perform(patch("/api/abonnements/payment")
                        .param("paid", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(List.of(1L, 2L))))
                .andExpect(status().isOk());

        verify(this.abonnementService).updatePaymentStatus(List.of(1L, 2L), true);
    }
}
