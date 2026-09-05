package standardNaast.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Cotisation price per year")
public record CotisationDto(
        @Schema(description = "Cotisation year/period", example = "2024")
        Long anneeCotisation,

        @Schema(description = "Fee amount", example = "20.00")
        BigDecimal montantCotisation
) {
}
