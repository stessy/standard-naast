package standardNaast.backend.dto;

import standardNaast.backend.domain.CompetitionType;
import standardNaast.backend.domain.PersonType;

public record AbonnementPriceDto(
        Long id,
        String seasonId,
        Long price,
        Integer tribune,
        String bloc,
        PersonType typePersonne,
        CompetitionType typeCompetition
) {
}
