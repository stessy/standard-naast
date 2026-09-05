package standardNaast.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BenevolatDto(
        Long id,
        Long personId,
        Long memberNumber,
        String firstName,
        String lastName,
        BigDecimal amount,
        String typeBenevolat,
        LocalDate dateBenevolat
) {}
