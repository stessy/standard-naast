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
import standardNaast.backend.dto.MemberCreateUpdateDto;
import standardNaast.backend.dto.MemberDto;
import standardNaast.backend.exception.GlobalExceptionHandler;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.service.MemberService;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MemberControllerIntegrationTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    private MemberDto sampleMemberDto;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        this.mockMvc = MockMvcBuilders.standaloneSetup(this.memberController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(this.objectMapper))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        this.sampleMemberDto = new MemberDto(
                1L,
                "Witsel",
                "Axel",
                "Rue de Naast, 4",
                "7191",
                "Naast",
                LocalDate.of(1989, 1, 12),
                "axel.witsel@example.com",
                "0470111111",
                null,
                null,
                null,
                28L,
                false,
                false
        );
    }

    @Test
    void getMembers_shouldReturnPagedMembers() throws Exception {
        when(this.memberService.getMembers(isNull(), any())).thenReturn(new PageImpl<>(List.of(this.sampleMemberDto), org.springframework.data.domain.PageRequest.of(0, 20), 1));

        this.mockMvc.perform(get("/api/members")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name", is("Witsel")))
                .andExpect(jsonPath("$.content[0].firstname", is("Axel")));
    }

    @Test
    void getMemberById_whenExists_shouldReturnMember() throws Exception {
        when(this.memberService.getMemberById(1L)).thenReturn(this.sampleMemberDto);

        this.mockMvc.perform(get("/api/members/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Witsel")))
                .andExpect(jsonPath("$.firstname", is("Axel")))
                .andExpect(jsonPath("$.memberNumber", is(28)));
    }

    @Test
    void getMemberById_whenNotFound_shouldReturn404() throws Exception {
        when(this.memberService.getMemberById(999999L))
                .thenThrow(new ResourceNotFoundException("Membre non trouvé"));

        this.mockMvc.perform(get("/api/members/{id}", 999999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createMember_shouldPersistAndReturnCreated() throws Exception {
        final MemberCreateUpdateDto newMember = new MemberCreateUpdateDto(
                "Fellaini",
                "Marouane",
                "Rue de Naast, 10",
                "7191",
                "Naast",
                LocalDate.of(1987, 11, 22),
                "marouane@example.com",
                "0470000000",
                null,
                null,
                null,
                27L,
                false,
                false
        );

        final MemberDto createdDto = new MemberDto(
                2L,
                "Fellaini",
                "Marouane",
                "Rue de Naast, 10",
                "7191",
                "Naast",
                LocalDate.of(1987, 11, 22),
                "marouane@example.com",
                "0470000000",
                null,
                null,
                null,
                27L,
                false,
                false
        );

        when(this.memberService.createMember(any(MemberCreateUpdateDto.class))).thenReturn(createdDto);

        this.mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(newMember)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("Fellaini")))
                .andExpect(jsonPath("$.firstname", is("Marouane")))
                .andExpect(jsonPath("$.memberNumber", is(27)));
    }

    @Test
    void updateMember_shouldUpdateAndReturnMember() throws Exception {
        final MemberCreateUpdateDto updateDto = new MemberCreateUpdateDto(
                "Witsel",
                "Axel-Laurent",
                "Rue de Naast, 4",
                "7191",
                "Naast",
                LocalDate.of(1989, 1, 12),
                "axel.updated@example.com",
                "0470111111",
                null,
                null,
                null,
                28L,
                false,
                false
        );

        final MemberDto updatedMember = new MemberDto(
                1L,
                "Witsel",
                "Axel-Laurent",
                "Rue de Naast, 4",
                "7191",
                "Naast",
                LocalDate.of(1989, 1, 12),
                "axel.updated@example.com",
                "0470111111",
                null,
                null,
                null,
                28L,
                false,
                false
        );

        when(this.memberService.updateMember(eq(1L), any(MemberCreateUpdateDto.class))).thenReturn(updatedMember);

        this.mockMvc.perform(put("/api/members/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname", is("Axel-Laurent")))
                .andExpect(jsonPath("$.email", is("axel.updated@example.com")));
    }

    @Test
    void deleteMember_shouldRemoveAndReturnNoContent() throws Exception {
        doNothing().when(this.memberService).deleteMember(1L);

        this.mockMvc.perform(delete("/api/members/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(this.memberService).deleteMember(1L);
    }

    @Test
    void getNextMemberNumber_shouldReturnNextNumber() throws Exception {
        when(this.memberService.getNextMemberNumber()).thenReturn(105L);

        this.mockMvc.perform(get("/api/members/next-number")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("105"));
    }
}
