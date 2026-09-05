package standardNaast.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import standardNaast.backend.dto.TeamCreateUpdateDto;
import standardNaast.backend.dto.TeamDto;

import java.util.List;

public interface TeamService {

    Page<TeamDto> getTeams(String search, Pageable pageable);

    List<TeamDto> getAllTeams();

    TeamDto getTeamById(Long id);

    TeamDto createTeam(TeamCreateUpdateDto dto);

    TeamDto updateTeam(Long id, TeamCreateUpdateDto dto);

    void deleteTeam(Long id);
}
