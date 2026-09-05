package standardNaast.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO d'inscription d'un passager à un déplacement")
public record PersonTravelCreateDto(
        @Schema(description = "Identifiant de la personne (membre ou non-membre)", example = "10")
        @NotNull(message = "L'identifiant de la personne est obligatoire")
        Long personId,

        @Schema(description = "Identifiant du match", example = "501")
        @NotNull(message = "L'identifiant du match est obligatoire")
        Long matchId,

        @Schema(description = "Montant payé pour le déplacement", example = "15")
        @Min(value = 0, message = "Le montant ne peut pas être négatif")
        long carTravelAmount
) {
}
