package standardNaast.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import standardNaast.backend.dto.MatchTravelOverviewDto;
import standardNaast.backend.dto.PersonTravelCreateDto;
import standardNaast.backend.dto.PersonTravelDto;

import java.util.List;

public interface TravelService {

    Page<PersonTravelDto> searchTravels(Long matchId, Long personId, String seasonId, Pageable pageable);

    List<PersonTravelDto> getTravelsByMatch(Long matchId);

    List<PersonTravelDto> getTravelsByPerson(Long personId);

    PersonTravelDto getTravelById(Long id);

    MatchTravelOverviewDto getMatchTravelOverview(Long matchId);

    PersonTravelDto registerPersonTravel(PersonTravelCreateDto dto);

    void removePersonTravel(Long id);

    long countMemberAwayTravels(String seasonId, Long personId);
}
