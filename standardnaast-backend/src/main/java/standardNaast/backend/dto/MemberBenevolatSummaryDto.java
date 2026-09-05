package standardNaast.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public record MemberBenevolatSummaryDto(
        Long personId,
        Long memberNumber,
        String firstName,
        String lastName,
        BigDecimal totalAmount,
        long count,
        List<BenevolatDto> benevolats
) {}
