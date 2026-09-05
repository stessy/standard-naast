package standardNaast.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Récapitulatif des passagers inscrits à un déplacement pour un match")
public record MatchTravelOverviewDto(
        @Schema(description = "Identifiant du match", example = "501")
        Long matchId,

        @Schema(description = "Nom de l'adversaire", example = "Anderlecht")
        String opponentName,

        @Schema(description = "Identifiant de la saison", example = "2024-2025")
        String seasonId,

        @Schema(description = "Nombre total de passagers inscrits", example = "48")
        int totalPassengers,

        @Schema(description = "Nombre de membres inscrits", example = "40")
        int memberPassengersCount,

        @Schema(description = "Nombre de non-membres inscrits", example = "8")
        int nonMemberPassengersCount,

        @Schema(description = "Total des montants perçus pour ce déplacement", example = "720")
        long totalAmountCollected,

        @Schema(description = "Liste des passagers membres")
        List<PersonTravelDto> memberPassengers,

        @Schema(description = "Liste des passagers non-membres")
        List<PersonTravelDto> nonMemberPassengers
) {
}
