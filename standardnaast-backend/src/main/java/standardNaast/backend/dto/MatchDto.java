package standardNaast.backend.dto;

import standardNaast.backend.domain.CompetitionType;
import standardNaast.backend.domain.MatchType;
import standardNaast.backend.domain.Place;
import standardNaast.backend.domain.PriceType;

import java.time.LocalDateTime;

public record MatchDto(
        Long id,
        String seasonId,
        Long opponentId,
        String opponentName,
        LocalDateTime dateMatch,
        Place place,
        CompetitionType competitionType,
        MatchType matchType,
        PriceType priceType
) {
}
