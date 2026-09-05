package standardNaast.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import standardNaast.backend.domain.PersonTravelType;
import standardNaast.backend.domain.Place;

import java.math.BigDecimal;

@Schema(description = "Représentation d'un tarif de déplacement en car")
public record TravelPriceDto(
        @Schema(description = "Identifiant unique du tarif", example = "100033")
        Long id,

        @Schema(description = "Identifiant de la saison", example = "2024-2025")
        String seasonId,

        @Schema(description = "Montant du déplacement", example = "15.00")
        BigDecimal montant,

        @Schema(description = "Lieu du match", example = "AWAY")
        Place place,

        @Schema(description = "Indique si le tarif s'applique aux membres", example = "true")
        boolean membre,

        @Schema(description = "Tranche d'âge / type de personne", example = "MAJOR")
        PersonTravelType personTravelType
) {
}
