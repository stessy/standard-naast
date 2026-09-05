package standardNaast.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SeasonCreateUpdateDto(
        @NotBlank(message = "L'identifiant de saison est obligatoire (ex: 2024-2025)")
        @Size(max = 10, message = "L'identifiant ne doit pas dépasser 10 caractères")
        String id,

        @NotNull(message = "La date de début est obligatoire")
        LocalDate dateStart,

        @NotNull(message = "La date de fin est obligatoire")
        LocalDate dateEnd,

        LocalDate dateFirstMatchChampionship,

        Boolean european,

        @PositiveOrZero(message = "Le montant de la cotisation doit être positif ou nul")
        BigDecimal montantCotisation
) {
}
