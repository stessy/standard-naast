package standardNaast.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import standardNaast.backend.domain.AbonnementStatus;

public record AbonnementCreateUpdateDto(
        Long abonnementPriceId,
        String rang,
        String place,
        long reduction,
        boolean paye,
        Long acompte,
        @NotBlank(message = "Season ID is mandatory")
        String seasonId,
        @NotNull(message = "Person ID is mandatory")
        Long personId,
        AbonnementStatus abonnementStatus,
        String bloc
) {
}
