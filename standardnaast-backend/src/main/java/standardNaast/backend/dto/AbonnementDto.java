package standardNaast.backend.dto;

import standardNaast.backend.domain.AbonnementStatus;

public record AbonnementDto(
        Long id,
        AbonnementPriceDto abonnementPrice,
        String rang,
        String place,
        long reduction,
        boolean paye,
        Long acompte,
        String seasonId,
        Long personId,
        String personFirstName,
        String personName,
        Long memberNumber,
        AbonnementStatus abonnementStatus,
        String bloc
) {
}
