package standardNaast.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record MemberCreateUpdateDto(
        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
        String name,

        @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
        String firstname,

        @Size(max = 200, message = "L'adresse ne doit pas dépasser 200 caractères")
        String address,

        @Size(max = 10, message = "Le code postal ne doit pas dépasser 10 caractères")
        String postalCode,

        @Size(max = 100, message = "La ville ne doit pas dépasser 100 caractères")
        String city,

        LocalDate birthdate,

        @Email(message = "L'adresse email doit être valide")
        @Size(max = 120, message = "L'email ne doit pas dépasser 120 caractères")
        String email,

        @Size(max = 20, message = "Le numéro GSM ne doit pas dépasser 20 caractères")
        String mobilePhone,

        @Size(max = 20, message = "Le numéro de téléphone ne doit pas dépasser 20 caractères")
        String phone,

        LocalDate passportValidity,

        @Size(max = 50, message = "Le numéro de carte d'identité ne doit pas dépasser 50 caractères")
        String identityCardNumber,

        Long memberNumber,

        Boolean student,

        Boolean redCard
) {}
