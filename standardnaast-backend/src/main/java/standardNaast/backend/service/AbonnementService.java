package standardNaast.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import standardNaast.backend.domain.AbonnementStatus;
import standardNaast.backend.dto.AbonnementCreateUpdateDto;
import standardNaast.backend.dto.AbonnementDto;

import java.util.List;

public interface AbonnementService {

    Page<AbonnementDto> getAbonnements(String seasonId, Pageable pageable);

    List<AbonnementDto> getAbonnementsBySeason(String seasonId);

    List<AbonnementDto> getAbonnementsByMember(Long memberId);

    List<AbonnementDto> getPurchasableAbonnements(String seasonId);

    AbonnementDto getAbonnementById(Long id);

    AbonnementDto createAbonnement(AbonnementCreateUpdateDto dto);

    AbonnementDto updateAbonnement(Long id, AbonnementCreateUpdateDto dto);

    void deleteAbonnement(Long id);

    void updateStatus(List<Long> abonnementIds, AbonnementStatus status);

    void updatePaymentStatus(List<Long> abonnementIds, boolean paid);
}
