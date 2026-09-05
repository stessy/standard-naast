package standardNaast.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "DTO for registering a member cotisation payment")
public record PersonCotisationCreateDto(
        @Schema(description = "Member ID", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "L'identifiant du membre est obligatoire")
        Long memberId,

        @Schema(description = "Season ID", example = "2024-2025", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "La saison est obligatoire")
        String seasonId,

        @Schema(description = "Payment date", example = "2024-09-01")
        LocalDate datePaiement,

        @Schema(description = "Whether the membership card was sent", example = "false")
        Boolean carteMembreEnvoyee
) {
}
