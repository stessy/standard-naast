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
import standardNaast.backend.dto.BenevolatCreateUpdateDto;
import standardNaast.backend.dto.BenevolatDto;
import standardNaast.backend.dto.MemberBenevolatSummaryDto;
import standardNaast.backend.exception.GlobalExceptionHandler;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.service.BenevolatService;

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
class BenevolatControllerIntegrationTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private BenevolatService benevolatService;

    @InjectMocks
    private BenevolatController benevolatController;

    private BenevolatDto sampleDto;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        this.mockMvc = MockMvcBuilders.standaloneSetup(this.benevolatController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(this.objectMapper))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        this.sampleDto = new BenevolatDto(
                10L,
                1L,
                101L,
                "Marc",
                "Dubois",
                new BigDecimal("50.00"),
                "Barman barbecue",
                LocalDate.of(2024, 9, 15)
        );
    }

    @Test
    void searchBenevolats_shouldReturnPagedResults() throws Exception {
        when(this.benevolatService.searchBenevolats(isNull(), isNull(), isNull(), any()))
                .thenReturn(new PageImpl<>(List.of(this.sampleDto), org.springframework.data.domain.PageRequest.of(0, 20), 1));

        this.mockMvc.perform(get("/api/benevolats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName", is("Marc")))
                .andExpect(jsonPath("$.content[0].typeBenevolat", is("Barman barbecue")))
                .andExpect(jsonPath("$.content[0].amount", is(50.00)));
    }

    @Test
    void getBenevolatsByPerson_shouldReturnList() throws Exception {
        when(this.benevolatService.getBenevolatsByPerson(1L))
                .thenReturn(List.of(this.sampleDto));

        this.mockMvc.perform(get("/api/benevolats/person/{personId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName", is("Marc")));
    }

    @Test
    void getBenevolatsByPersonAndSeason_shouldReturnList() throws Exception {
        when(this.benevolatService.getBenevolatsByPersonAndSeason(1L, "2024-2025"))
                .thenReturn(List.of(this.sampleDto));

        this.mockMvc.perform(get("/api/benevolats/person/{personId}/season/{seasonId}", 1L, "2024-2025")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].typeBenevolat", is("Barman barbecue")));
    }

    @Test
    void getMemberBenevolatSummary_shouldReturnSummary() throws Exception {
        final MemberBenevolatSummaryDto summaryDto = new MemberBenevolatSummaryDto(
                1L,
                101L,
                "Marc",
                "Dubois",
                new BigDecimal("50.00"),
                1,
                List.of(this.sampleDto)
        );

        when(this.benevolatService.getMemberBenevolatSummary(1L, "2024-2025"))
                .thenReturn(summaryDto);

        this.mockMvc.perform(get("/api/benevolats/person/{personId}/summary", 1L)
                        .param("seasonId", "2024-2025")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.personId", is(1)))
                .andExpect(jsonPath("$.totalAmount", is(50.00)))
                .andExpect(jsonPath("$.count", is(1)));
    }

    @Test
    void getBenevolatById_whenExists_shouldReturnDto() throws Exception {
        when(this.benevolatService.getBenevolatById(10L)).thenReturn(this.sampleDto);

        this.mockMvc.perform(get("/api/benevolats/{id}", 10L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.firstName", is("Marc")));
    }

    @Test
    void getBenevolatById_whenNotFound_shouldReturn404() throws Exception {
        when(this.benevolatService.getBenevolatById(999L))
                .thenThrow(new ResourceNotFoundException("Prestation de bénévolat non trouvée"));

        this.mockMvc.perform(get("/api/benevolats/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBenevolat_shouldReturnCreated() throws Exception {
        final BenevolatCreateUpdateDto createDto = new BenevolatCreateUpdateDto(
                1L,
                new BigDecimal("50.00"),
                "Barman barbecue",
                LocalDate.of(2024, 9, 15)
        );

        when(this.benevolatService.createBenevolat(any(BenevolatCreateUpdateDto.class))).thenReturn(this.sampleDto);

        this.mockMvc.perform(post("/api/benevolats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.firstName", is("Marc")));
    }

    @Test
    void updateBenevolat_shouldReturnUpdated() throws Exception {
        final BenevolatCreateUpdateDto updateDto = new BenevolatCreateUpdateDto(
                1L,
                new BigDecimal("60.00"),
                "Barman barbecue + aide cuisine",
                LocalDate.of(2024, 9, 15)
        );

        when(this.benevolatService.updateBenevolat(eq(10L), any(BenevolatCreateUpdateDto.class))).thenReturn(this.sampleDto);

        this.mockMvc.perform(put("/api/benevolats/{id}", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)));
    }

    @Test
    void deleteBenevolat_shouldReturnNoContent() throws Exception {
        doNothing().when(this.benevolatService).deleteBenevolat(10L);

        this.mockMvc.perform(delete("/api/benevolats/{id}", 10L))
                .andExpect(status().isNoContent());

        verify(this.benevolatService).deleteBenevolat(10L);
    }
}
