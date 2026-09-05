package standardNaast.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import standardNaast.backend.domain.AbonnementStatus;

import java.util.List;

public record AbonnementStatusUpdateDto(
        @NotEmpty(message = "Abonnement IDs list cannot be empty")
        List<Long> abonnementIds,

        @NotNull(message = "Status is mandatory")
        AbonnementStatus status
) {
}
