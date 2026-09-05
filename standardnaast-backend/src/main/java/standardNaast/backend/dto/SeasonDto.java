package standardNaast.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SeasonDto(
        String id,
        LocalDate dateStart,
        LocalDate dateEnd,
        LocalDate dateFirstMatchChampionship,
        Boolean european,
        BigDecimal montantCotisation
) {
}
