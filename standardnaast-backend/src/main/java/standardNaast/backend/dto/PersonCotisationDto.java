package standardNaast.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Member cotisation details")
public record PersonCotisationDto(
        @Schema(description = "Cotisation ID", example = "1001")
        Long id,

        @Schema(description = "Member ID", example = "123")
        Long memberId,

        @Schema(description = "Member number", example = "42")
        Long memberNumber,

        @Schema(description = "Member first name", example = "Jean")
        String firstName,

        @Schema(description = "Member last name", example = "Dupont")
        String name,

        @Schema(description = "Season ID", example = "2024-2025")
        String seasonId,

        @Schema(description = "Payment date", example = "2024-09-01")
        LocalDate datePaiement,

        @Schema(description = "Whether the membership card was sent", example = "true")
        boolean carteMembreEnvoyee
) {
}
