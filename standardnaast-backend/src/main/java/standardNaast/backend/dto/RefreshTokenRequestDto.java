package standardNaast.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDto(
        @NotBlank(message = "Le refresh token est obligatoire")
        String refreshToken
) {}
