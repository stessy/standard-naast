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
import standardNaast.backend.domain.AccountingType;
import standardNaast.backend.dto.AccountingCreateUpdateDto;
import standardNaast.backend.dto.AccountingDto;
import standardNaast.backend.dto.AccountingSummaryDto;
import standardNaast.backend.exception.GlobalExceptionHandler;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.service.AccountingService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AccountingControllerIntegrationTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AccountingService accountingService;

    @InjectMocks
    private AccountingController accountingController;

    private AccountingDto sampleDto;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        this.mockMvc = MockMvcBuilders.standaloneSetup(this.accountingController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(this.objectMapper))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        this.sampleDto = new AccountingDto(
                1L,
                LocalDate.of(2024, 8, 15),
                "Vente boissons",
                AccountingType.ENTRY,
                new BigDecimal("250.00")
        );
    }

    @Test
    void searchAccountings_shouldReturnPagedResults() throws Exception {
        when(this.accountingService.searchAccountings(isNull(), isNull(), isNull(), isNull(), any()))
                .thenReturn(new PageImpl<>(List.of(this.sampleDto), org.springframework.data.domain.PageRequest.of(0, 20), 1));

        this.mockMvc.perform(get("/api/accountings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].description", is("Vente boissons")))
                .andExpect(jsonPath("$.content[0].type", is("ENTRY")))
                .andExpect(jsonPath("$.content[0].amount", is(250.00)));
    }

    @Test
    void getAccountingsByMonthAndYear_shouldReturnList() throws Exception {
        when(this.accountingService.getAccountingsByMonthAndYear(8, 2024))
                .thenReturn(List.of(this.sampleDto));

        this.mockMvc.perform(get("/api/accountings/month")
                        .param("month", "8")
                        .param("year", "2024")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", is("Vente boissons")));
    }

    @Test
    void getAccountingsByYear_shouldReturnList() throws Exception {
        when(this.accountingService.getAccountingsByYear(2024))
                .thenReturn(List.of(this.sampleDto));

        this.mockMvc.perform(get("/api/accountings/year/{year}", 2024)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", is("Vente boissons")));
    }

    @Test
    void getAccountingsBySeason_shouldReturnList() throws Exception {
        when(this.accountingService.getAccountingsBySeason("2024-2025"))
                .thenReturn(List.of(this.sampleDto));

        this.mockMvc.perform(get("/api/accountings/season/{seasonId}", "2024-2025")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", is("Vente boissons")));
    }

    @Test
    void getAccountingSummary_shouldReturnSummary() throws Exception {
        final AccountingSummaryDto summaryDto = new AccountingSummaryDto(
                new BigDecimal("500.00"),
                new BigDecimal("200.00"),
                new BigDecimal("300.00"),
                10,
                LocalDate.of(2024, 7, 1),
                LocalDate.of(2025, 6, 30)
        );

        when(this.accountingService.getAccountingSummary(isNull(), isNull()))
                .thenReturn(summaryDto);

        this.mockMvc.perform(get("/api/accountings/summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEntries", is(500.00)))
                .andExpect(jsonPath("$.totalExits", is(200.00)))
                .andExpect(jsonPath("$.balance", is(300.00)))
                .andExpect(jsonPath("$.recordCount", is(10)));
    }

    @Test
    void getAccountingById_whenExists_shouldReturnDto() throws Exception {
        when(this.accountingService.getAccountingById(1L)).thenReturn(this.sampleDto);

        this.mockMvc.perform(get("/api/accountings/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Vente boissons")));
    }

    @Test
    void getAccountingById_whenNotFound_shouldReturn404() throws Exception {
        when(this.accountingService.getAccountingById(999L))
                .thenThrow(new ResourceNotFoundException("Écriture comptable non trouvée"));

        this.mockMvc.perform(get("/api/accountings/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createAccounting_shouldReturnCreated() throws Exception {
        final AccountingCreateUpdateDto createDto = new AccountingCreateUpdateDto(
                LocalDate.of(2024, 8, 15),
                "Vente boissons",
                AccountingType.ENTRY,
                new BigDecimal("250.00")
        );

        when(this.accountingService.createAccounting(any(AccountingCreateUpdateDto.class))).thenReturn(this.sampleDto);

        this.mockMvc.perform(post("/api/accountings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Vente boissons")));
    }

    @Test
    void updateAccounting_shouldReturnUpdated() throws Exception {
        final AccountingCreateUpdateDto updateDto = new AccountingCreateUpdateDto(
                LocalDate.of(2024, 8, 15),
                "Vente boissons - maj",
                AccountingType.ENTRY,
                new BigDecimal("280.00")
        );

        when(this.accountingService.updateAccounting(eq(1L), any(AccountingCreateUpdateDto.class))).thenReturn(this.sampleDto);

        this.mockMvc.perform(put("/api/accountings/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteAccounting_shouldReturnNoContent() throws Exception {
        doNothing().when(this.accountingService).deleteAccounting(1L);

        this.mockMvc.perform(delete("/api/accountings/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(this.accountingService).deleteAccounting(1L);
    }
}
