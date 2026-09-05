package standardNaast.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import standardNaast.backend.domain.PersonTravelType;
import standardNaast.backend.domain.Place;

import java.math.BigDecimal;

@Schema(description = "DTO de création/modification d'un tarif de déplacement en car")
public record TravelPriceCreateUpdateDto(
        @Schema(description = "Identifiant de la saison", example = "2024-2025")
        @NotBlank(message = "L'identifiant de la saison est obligatoire")
        String seasonId,

        @Schema(description = "Montant du déplacement", example = "15.00")
        @NotNull(message = "Le montant est obligatoire")
        @DecimalMin(value = "0.00", message = "Le montant ne peut pas être négatif")
        BigDecimal montant,

        @Schema(description = "Lieu du match", example = "AWAY")
        @NotNull(message = "Le lieu du match est obligatoire")
        Place place,

        @Schema(description = "Indique si le tarif s'applique aux membres", example = "true")
        boolean membre,

        @Schema(description = "Tranche d'âge / type de personne", example = "MAJOR")
        PersonTravelType personTravelType
) {
}
