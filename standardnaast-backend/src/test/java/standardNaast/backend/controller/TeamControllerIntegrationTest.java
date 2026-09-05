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
import standardNaast.backend.dto.TeamCreateUpdateDto;
import standardNaast.backend.dto.TeamDto;
import standardNaast.backend.exception.GlobalExceptionHandler;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.service.TeamService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TeamControllerIntegrationTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private TeamService teamService;

    @InjectMocks
    private TeamController teamController;

    private TeamDto sampleTeamDto;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();

        this.mockMvc = MockMvcBuilders.standaloneSetup(this.teamController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        this.sampleTeamDto = new TeamDto(1L, "RSC Anderlecht");
    }

    @Test
    void getTeams_shouldReturnPagedTeams() throws Exception {
        when(this.teamService.getTeams(isNull(), any())).thenReturn(new PageImpl<>(List.of(this.sampleTeamDto)));

        this.mockMvc.perform(get("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name", is("RSC Anderlecht")));
    }

    @Test
    void getAllTeams_shouldReturnAllTeamsList() throws Exception {
        when(this.teamService.getAllTeams()).thenReturn(List.of(this.sampleTeamDto));

        this.mockMvc.perform(get("/api/teams/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("RSC Anderlecht")));
    }

    @Test
    void getTeamById_whenExists_shouldReturnTeam() throws Exception {
        when(this.teamService.getTeamById(1L)).thenReturn(this.sampleTeamDto);

        this.mockMvc.perform(get("/api/teams/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("RSC Anderlecht")));
    }

    @Test
    void getTeamById_whenNotFound_shouldReturn404() throws Exception {
        when(this.teamService.getTeamById(999L))
                .thenThrow(new ResourceNotFoundException("Équipe non trouvée"));

        this.mockMvc.perform(get("/api/teams/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTeam_shouldReturnCreated() throws Exception {
        final TeamCreateUpdateDto createDto = new TeamCreateUpdateDto("RSC Anderlecht");
        when(this.teamService.createTeam(any(TeamCreateUpdateDto.class))).thenReturn(this.sampleTeamDto);

        this.mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("RSC Anderlecht")));
    }

    @Test
    void updateTeam_shouldReturnUpdated() throws Exception {
        final TeamCreateUpdateDto updateDto = new TeamCreateUpdateDto("Club Brugge");
        final TeamDto updatedDto = new TeamDto(1L, "Club Brugge");

        when(this.teamService.updateTeam(eq(1L), any(TeamCreateUpdateDto.class))).thenReturn(updatedDto);

        this.mockMvc.perform(put("/api/teams/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Club Brugge")));
    }

    @Test
    void deleteTeam_shouldReturnNoContent() throws Exception {
        doNothing().when(this.teamService).deleteTeam(1L);

        this.mockMvc.perform(delete("/api/teams/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(this.teamService).deleteTeam(1L);
    }
}
