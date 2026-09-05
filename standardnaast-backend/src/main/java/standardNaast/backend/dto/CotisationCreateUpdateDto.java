package standardNaast.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "DTO for creating or updating a cotisation fee rate")
public record CotisationCreateUpdateDto(
        @Schema(description = "Cotisation year/period", example = "2024", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "L'année de cotisation est obligatoire")
        Long anneeCotisation,

        @Schema(description = "Fee amount", example = "20.00", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Le montant de cotisation est obligatoire")
        @DecimalMin(value = "0.0", inclusive = false, message = "Le montant de cotisation doit être supérieur à zéro")
        BigDecimal montantCotisation
) {
}
