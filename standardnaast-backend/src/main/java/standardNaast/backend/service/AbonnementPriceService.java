package standardNaast.backend.service;

import standardNaast.backend.domain.CompetitionType;
import standardNaast.backend.dto.AbonnementPriceCreateUpdateDto;
import standardNaast.backend.dto.AbonnementPriceDto;

import java.util.List;

public interface AbonnementPriceService {

    List<AbonnementPriceDto> getPricesBySeason(String seasonId);

    List<AbonnementPriceDto> getPricesBySeasonAndCompetition(String seasonId, CompetitionType competitionType);

    List<String> getDistinctBlocs(String seasonId, CompetitionType competitionType);

    List<CompetitionType> getDistinctCompetitionTypes(String seasonId);

    AbonnementPriceDto getPriceById(Long id);

    AbonnementPriceDto createPrice(AbonnementPriceCreateUpdateDto dto);

    AbonnementPriceDto updatePrice(Long id, AbonnementPriceCreateUpdateDto dto);

    void deletePrice(Long id);
}
