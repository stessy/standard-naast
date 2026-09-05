package standardNaast.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountingSummaryDto(
        BigDecimal totalEntries,
        BigDecimal totalExits,
        BigDecimal balance,
        long recordCount,
        LocalDate minDate,
        LocalDate maxDate
) {}
