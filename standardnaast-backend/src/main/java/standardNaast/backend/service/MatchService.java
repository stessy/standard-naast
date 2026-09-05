package standardNaast.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import standardNaast.backend.domain.CompetitionType;
import standardNaast.backend.domain.Place;
import standardNaast.backend.dto.MatchCreateUpdateDto;
import standardNaast.backend.dto.MatchDto;

import java.util.List;

public interface MatchService {

    Page<MatchDto> getMatches(String seasonId, CompetitionType competitionType, Place place, Pageable pageable);

    List<MatchDto> getMatchesBySeason(String seasonId);

    MatchDto getMatchById(Long id);

    MatchDto createMatch(MatchCreateUpdateDto dto);

    MatchDto updateMatch(Long id, MatchCreateUpdateDto dto);

    void deleteMatch(Long id);
}
