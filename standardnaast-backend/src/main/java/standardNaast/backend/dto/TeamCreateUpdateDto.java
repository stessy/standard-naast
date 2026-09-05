package standardNaast.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TeamCreateUpdateDto(
        @NotBlank(message = "Le nom de l'équipe est obligatoire")
        @Size(max = 150, message = "Le nom de l'équipe ne doit pas dépasser 150 caractères")
        String name
) {
}
