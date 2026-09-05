package standardNaast.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import standardNaast.backend.domain.CompetitionType;
import standardNaast.backend.domain.PersonType;

public record AbonnementPriceCreateUpdateDto(
        @NotBlank(message = "Season ID is mandatory")
        String seasonId,

        @NotNull(message = "Price is mandatory")
        @PositiveOrZero(message = "Price must be positive or zero")
        Long price,

        Integer tribune,

        @NotBlank(message = "Bloc is mandatory")
        String bloc,

        @NotNull(message = "Person type is mandatory")
        PersonType typePersonne,

        @NotNull(message = "Competition type is mandatory")
        CompetitionType typeCompetition
) {
}
