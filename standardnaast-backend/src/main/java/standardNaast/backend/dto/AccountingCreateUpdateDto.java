package standardNaast.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import standardNaast.backend.domain.AccountingType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountingCreateUpdateDto(
        @NotNull(message = "La date comptable est obligatoire")
        LocalDate date,

        @NotBlank(message = "La description est obligatoire")
        String description,

        @NotNull(message = "Le type comptable (ENTRY/EXIT) est obligatoire")
        AccountingType type,

        @NotNull(message = "Le montant est obligatoire")
        BigDecimal amount
) {}
