package standardNaast.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import standardNaast.backend.domain.CompetitionType;
import standardNaast.backend.domain.Place;

import java.time.LocalDateTime;

@Schema(description = "Représentation d'un passager inscrit à un déplacement")
public record PersonTravelDto(
        @Schema(description = "Identifiant de l'inscription déplacement", example = "1001")
        Long id,

        @Schema(description = "Identifiant du membre / de la personne", example = "10")
        Long memberId,

        @Schema(description = "Numéro de membre", example = "42")
        Long memberNumber,

        @Schema(description = "Prénom de la personne", example = "Jean")
        String firstName,

        @Schema(description = "Nom de la personne", example = "Dupont")
        String lastName,

        @Schema(description = "Indique si la personne est membre", example = "true")
        boolean isMember,

        @Schema(description = "Identifiant du match", example = "501")
        Long matchId,

        @Schema(description = "Date et heure du match")
        LocalDateTime matchDate,

        @Schema(description = "Lieu du match", example = "AWAY")
        Place matchPlace,

        @Schema(description = "Nom de l'adversaire", example = "Anderlecht")
        String opponentName,

        @Schema(description = "Type de compétition", example = "CHAMPIONSHIP")
        CompetitionType competitionType,

        @Schema(description = "Identifiant de la saison", example = "2024-2025")
        String seasonId,

        @Schema(description = "Montant payé / facturé pour le déplacement", example = "15")
        long carTravelAmount
) {
}
