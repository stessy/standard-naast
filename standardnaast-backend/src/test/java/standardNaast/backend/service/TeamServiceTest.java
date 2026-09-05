package standardNaast.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import standardNaast.backend.domain.Team;
import standardNaast.backend.dto.TeamCreateUpdateDto;
import standardNaast.backend.dto.TeamDto;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.mapper.TeamMapper;
import standardNaast.backend.repository.TeamRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Spy
    private TeamMapper teamMapper = Mappers.getMapper(TeamMapper.class);

    @InjectMocks
    private TeamServiceImpl teamService;

    private Team sampleTeam;
    private TeamCreateUpdateDto sampleDto;

    @BeforeEach
    void setUp() {
        this.sampleTeam = Team.builder()
                .id(1L)
                .name("RSC Anderlecht")
                .build();

        this.sampleDto = new TeamCreateUpdateDto("RSC Anderlecht");
    }

    @Test
    void getTeams_shouldReturnPagedTeams() {
        final Pageable pageable = PageRequest.of(0, 10);
        when(this.teamRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(this.sampleTeam)));

        final Page<TeamDto> result = this.teamService.getTeams(null, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst().name()).isEqualTo("RSC Anderlecht");
    }

    @Test
    void getTeamById_whenExists_shouldReturnTeam() {
        when(this.teamRepository.findById(1L)).thenReturn(Optional.of(this.sampleTeam));

        final TeamDto result = this.teamService.getTeamById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("RSC Anderlecht");
    }

    @Test
    void getTeamById_whenNotFound_shouldThrowException() {
        when(this.teamRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.teamService.getTeamById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void createTeam_shouldSaveAndReturnDto() {
        when(this.teamRepository.save(any(Team.class))).thenReturn(this.sampleTeam);

        final TeamDto created = this.teamService.createTeam(this.sampleDto);

        assertThat(created).isNotNull();
        assertThat(created.name()).isEqualTo("RSC Anderlecht");
    }

    @Test
    void deleteTeam_whenExists_shouldDelete() {
        when(this.teamRepository.existsById(1L)).thenReturn(true);
        doNothing().when(this.teamRepository).deleteById(1L);

        this.teamService.deleteTeam(1L);

        verify(this.teamRepository).deleteById(1L);
    }
}
