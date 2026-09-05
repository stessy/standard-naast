package standardNaast.backend.service;

import standardNaast.backend.dto.SeasonCreateUpdateDto;
import standardNaast.backend.dto.SeasonDto;
import standardNaast.backend.dto.TeamDto;

import java.util.List;
import java.util.Optional;

public interface SeasonService {

    List<SeasonDto> getAllSeasons();

    SeasonDto getSeasonById(String id);

    Optional<SeasonDto> getCurrentSeason();

    SeasonDto createSeason(SeasonCreateUpdateDto dto);

    SeasonDto updateSeason(String id, SeasonCreateUpdateDto dto);

    void deleteSeason(String id);

    List<TeamDto> getTeamsForSeason(String seasonId);

    void addTeamToSeason(String seasonId, Long teamId);

    void removeTeamFromSeason(String seasonId, Long teamId);
}
