package standardNaast.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Bulk update request for membership card sent status")
public record MemberCardSentBulkUpdateDto(
        @Schema(description = "List of member cotisation IDs", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "La liste des cotisations ne peut pas être vide")
        List<Long> cotisationIds,

        @Schema(description = "Sent status", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
        boolean carteMembreEnvoyee
) {
}
