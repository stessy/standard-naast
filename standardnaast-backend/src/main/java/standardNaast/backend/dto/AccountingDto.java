package standardNaast.backend.dto;

import standardNaast.backend.domain.AccountingType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountingDto(
        Long id,
        LocalDate date,
        String description,
        AccountingType type,
        BigDecimal amount
) {}
