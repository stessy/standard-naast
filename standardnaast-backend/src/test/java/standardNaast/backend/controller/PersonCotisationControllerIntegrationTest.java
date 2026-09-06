package standardNaast.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import standardNaast.backend.dto.CotisationsSeasonOverviewDto;
import standardNaast.backend.dto.MemberCardSentBulkUpdateDto;
import standardNaast.backend.dto.PersonCotisationCreateDto;
import standardNaast.backend.dto.PersonCotisationDto;
import standardNaast.backend.exception.GlobalExceptionHandler;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.service.PersonCotisationService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PersonCotisationControllerIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private PersonCotisationService personCotisationService;

    @InjectMocks
    private PersonCotisationController personCotisationController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(personCotisationController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(this.objectMapper))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldRegisterMemberCotisation() throws Exception {
        PersonCotisationCreateDto input = new PersonCotisationCreateDto(10L, "2024-2025", LocalDate.of(2024, 9, 1), false);
        PersonCotisationDto output = new PersonCotisationDto(
                1001L, 10L, 42L, "Alice", "Martin", "2024-2025", LocalDate.of(2024, 9, 1), false
        );
        when(personCotisationService.registerMemberCotisation(any(PersonCotisationCreateDto.class))).thenReturn(output);

        mockMvc.perform(post("/api/member-cotisations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1001))
                .andExpect(jsonPath("$.memberId").value(10))
                .andExpect(jsonPath("$.memberNumber").value(42))
                .andExpect(jsonPath("$.seasonId").value("2024-2025"));
    }

    @Test
    void shouldGetCotisationById() throws Exception {
        PersonCotisationDto output = new PersonCotisationDto(
                1001L, 10L, 42L, "Alice", "Martin", "2024-2025", LocalDate.of(2024, 9, 1), false
        );
        when(personCotisationService.getCotisationById(1001L)).thenReturn(output);

        mockMvc.perform(get("/api/member-cotisations/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1001))
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    void shouldReturn404WhenCotisationNotFound() throws Exception {
        when(personCotisationService.getCotisationById(9999L))
                .thenThrow(new ResourceNotFoundException("Cotisation non trouvée avec l'identifiant : 9999"));

        mockMvc.perform(get("/api/member-cotisations/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetCotisationsByMember() throws Exception {
        PersonCotisationDto output = new PersonCotisationDto(
                1001L, 10L, 42L, "Alice", "Martin", "2024-2025", LocalDate.of(2024, 9, 1), false
        );
        when(personCotisationService.getCotisationsByMember(10L)).thenReturn(List.of(output));

        mockMvc.perform(get("/api/member-cotisations/member/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].memberId").value(10));
    }

    @Test
    void shouldGetCotisationsBySeason() throws Exception {
        PersonCotisationDto output = new PersonCotisationDto(
                1001L, 10L, 42L, "Alice", "Martin", "2024-2025", LocalDate.of(2024, 9, 1), false
        );
        when(personCotisationService.getCotisationsBySeason("2024-2025")).thenReturn(List.of(output));

        mockMvc.perform(get("/api/member-cotisations/season/2024-2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].seasonId").value("2024-2025"));
    }

    @Test
    void shouldGetSeasonOverview() throws Exception {
        CotisationsSeasonOverviewDto overview = new CotisationsSeasonOverviewDto(
                "2024-2025", 10, 8, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList()
        );
        when(personCotisationService.getSeasonOverview("2024-2025")).thenReturn(overview);

        mockMvc.perform(get("/api/member-cotisations/overview/2024-2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seasonId").value("2024-2025"))
                .andExpect(jsonPath("$.totalMembers").value(10))
                .andExpect(jsonPath("$.totalPaid").value(8))
                .andExpect(jsonPath("$.totalUnpaid").value(2));
    }

    @Test
    void shouldBulkUpdateMemberCardSent() throws Exception {
        MemberCardSentBulkUpdateDto input = new MemberCardSentBulkUpdateDto(List.of(1001L, 1002L), true);
        doNothing().when(personCotisationService).bulkUpdateMemberCardSent(any(MemberCardSentBulkUpdateDto.class));

        mockMvc.perform(post("/api/member-cotisations/card-sent/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNoContent());

        verify(personCotisationService).bulkUpdateMemberCardSent(any(MemberCardSentBulkUpdateDto.class));
    }

    @Test
    void shouldDeleteCotisation() throws Exception {
        doNothing().when(personCotisationService).deleteMemberCotisation(1001L);

        mockMvc.perform(delete("/api/member-cotisations/1001"))
                .andExpect(status().isNoContent());

        verify(personCotisationService).deleteMemberCotisation(1001L);
    }
}
