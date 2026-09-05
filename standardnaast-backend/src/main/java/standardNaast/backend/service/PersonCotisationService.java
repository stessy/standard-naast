package standardNaast.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import standardNaast.backend.dto.CotisationsSeasonOverviewDto;
import standardNaast.backend.dto.MemberCardSentBulkUpdateDto;
import standardNaast.backend.dto.PersonCotisationCreateDto;
import standardNaast.backend.dto.PersonCotisationDto;

import java.util.List;

public interface PersonCotisationService {

    PersonCotisationDto registerMemberCotisation(PersonCotisationCreateDto dto);

    List<PersonCotisationDto> getCotisationsByMember(Long memberId);

    List<PersonCotisationDto> getCotisationsBySeason(String seasonId);

    PersonCotisationDto getCotisationById(Long id);

    CotisationsSeasonOverviewDto getSeasonOverview(String seasonId);

    void bulkUpdateMemberCardSent(MemberCardSentBulkUpdateDto dto);

    Page<PersonCotisationDto> searchCotisations(String seasonId, Long personId, Boolean carteEnvoyee, Pageable pageable);

    void deleteMemberCotisation(Long id);
}
