package standardNaast.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank(message = "Le nom d'utilisateur ou email est obligatoire")
        String username,

        @NotBlank(message = "Le mot de passe est obligatoire")
        String password
) {}
