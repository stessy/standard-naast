package standardNaast.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import standardNaast.backend.domain.UserRole;

import java.util.Set;

public record RegisterRequestDto(
        @NotBlank(message = "Le nom d'utilisateur est obligatoire")
        @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit comporter entre 3 et 50 caractères")
        String username,

        @NotBlank(message = "L'adresse email est obligatoire")
        @Email(message = "Format d'email invalide")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 6, max = 100, message = "Le mot de passe doit comporter au moins 6 caractères")
        String password,

        String firstName,

        String lastName,

        Set<UserRole> roles,

        Long personId
) {}
