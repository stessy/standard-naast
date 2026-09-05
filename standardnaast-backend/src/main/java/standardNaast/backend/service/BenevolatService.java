package standardNaast.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import standardNaast.backend.dto.BenevolatCreateUpdateDto;
import standardNaast.backend.dto.BenevolatDto;
import standardNaast.backend.dto.MemberBenevolatSummaryDto;

import java.time.LocalDate;
import java.util.List;

public interface BenevolatService {

    Page<BenevolatDto> searchBenevolats(Long personId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    List<BenevolatDto> getBenevolatsByPerson(Long personId);

    List<BenevolatDto> getBenevolatsByPersonAndSeason(Long personId, String seasonId);

    MemberBenevolatSummaryDto getMemberBenevolatSummary(Long personId, String seasonId);

    BenevolatDto getBenevolatById(Long id);

    BenevolatDto createBenevolat(BenevolatCreateUpdateDto dto);

    BenevolatDto updateBenevolat(Long id, BenevolatCreateUpdateDto dto);

    void deleteBenevolat(Long id);
}
