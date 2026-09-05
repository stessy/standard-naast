package standardNaast.backend.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record BenevolatCreateUpdateDto(
        @NotNull(message = "L'identifiant du membre est obligatoire")
        Long personId,

        @NotNull(message = "Le montant est obligatoire")
        BigDecimal amount,

        String typeBenevolat,

        @NotNull(message = "La date de bénévolat est obligatoire")
        LocalDate dateBenevolat
) {}
