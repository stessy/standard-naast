package standardNaast.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import standardNaast.backend.domain.CompetitionType;
import standardNaast.backend.domain.MatchType;
import standardNaast.backend.domain.Place;
import standardNaast.backend.domain.PriceType;

import java.time.LocalDateTime;

public record MatchCreateUpdateDto(
        @NotBlank(message = "L'identifiant de la saison est obligatoire")
        String seasonId,

        @NotNull(message = "L'identifiant de l'équipe adverse est obligatoire")
        Long opponentId,

        @NotNull(message = "La date et l'heure du match sont obligatoires")
        LocalDateTime dateMatch,

        @NotNull(message = "Le lieu (HOME/AWAY) est obligatoire")
        Place place,

        @NotNull(message = "Le type de compétition est obligatoire")
        CompetitionType competitionType,

        MatchType matchType,

        PriceType priceType
) {
}
