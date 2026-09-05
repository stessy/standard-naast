package standardNaast.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import standardNaast.backend.domain.*;
import standardNaast.backend.dto.MatchTravelOverviewDto;
import standardNaast.backend.dto.PersonTravelCreateDto;
import standardNaast.backend.dto.PersonTravelDto;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.mapper.PersonTravelMapper;
import standardNaast.backend.repository.MatchRepository;
import standardNaast.backend.repository.PersonRepository;
import standardNaast.backend.repository.PersonTravelRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TravelServiceImpl implements TravelService {

    private final PersonTravelRepository personTravelRepository;
    private final PersonRepository personRepository;
    private final MatchRepository matchRepository;
    private final PersonTravelMapper personTravelMapper;

    public TravelServiceImpl(PersonTravelRepository personTravelRepository,
                             PersonRepository personRepository,
                             MatchRepository matchRepository,
                             PersonTravelMapper personTravelMapper) {
        this.personTravelRepository = personTravelRepository;
        this.personRepository = personRepository;
        this.matchRepository = matchRepository;
        this.personTravelMapper = personTravelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonTravelDto> searchTravels(Long matchId, Long personId, String seasonId, Pageable pageable) {
        return personTravelRepository.searchPersonTravels(matchId, personId, seasonId, pageable)
                .map(personTravelMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonTravelDto> getTravelsByMatch(Long matchId) {
        return personTravelMapper.toDtoList(personTravelRepository.findByMatchId(matchId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonTravelDto> getTravelsByPerson(Long personId) {
        return personTravelMapper.toDtoList(personTravelRepository.findByPersonId(personId));
    }

    @Override
    @Transactional(readOnly = true)
    public PersonTravelDto getTravelById(Long id) {
        PersonTravel entity = personTravelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription déplacement non trouvée avec l'identifiant : " + id));
        return personTravelMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public MatchTravelOverviewDto getMatchTravelOverview(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match non trouvé avec l'identifiant : " + matchId));

        List<PersonTravel> allTravels = personTravelRepository.findByMatchId(matchId);

        List<PersonTravelDto> memberTravels = new ArrayList<>();
        List<PersonTravelDto> nonMemberTravels = new ArrayList<>();
        long totalAmount = 0;

        for (PersonTravel travel : allTravels) {
            PersonTravelDto dto = personTravelMapper.toDto(travel);
            totalAmount += travel.getCarTravelAmount();
            if (travel.getPerson().isMember()) {
                memberTravels.add(dto);
            } else {
                nonMemberTravels.add(dto);
            }
        }

        String opponentName = match.getOpponent() != null ? match.getOpponent().getName() : "N/A";
        String seasonId = match.getSeason() != null ? match.getSeason().getId() : "N/A";

        return new MatchTravelOverviewDto(
                matchId,
                opponentName,
                seasonId,
                allTravels.size(),
                memberTravels.size(),
                nonMemberTravels.size(),
                totalAmount,
                memberTravels,
                nonMemberTravels
        );
    }

    @Override
    public PersonTravelDto registerPersonTravel(PersonTravelCreateDto dto) {
        if (personTravelRepository.existsByPersonIdAndMatchId(dto.personId(), dto.matchId())) {
            throw new IllegalArgumentException("La personne est déjà inscrite pour ce déplacement de match.");
        }

        Person person = personRepository.findById(dto.personId())
                .orElseThrow(() -> new ResourceNotFoundException("Personne non trouvée avec l'identifiant : " + dto.personId()));

        Match match = matchRepository.findById(dto.matchId())
                .orElseThrow(() -> new ResourceNotFoundException("Match non trouvé avec l'identifiant : " + dto.matchId()));

        PersonTravel entity = PersonTravel.builder()
                .person(person)
                .match(match)
                .carTravelAmount(dto.carTravelAmount())
                .build();

        PersonTravel saved = personTravelRepository.save(entity);
        return personTravelMapper.toDto(saved);
    }

    @Override
    public void removePersonTravel(Long id) {
        if (!personTravelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inscription déplacement non trouvée avec l'identifiant : " + id);
        }
        personTravelRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countMemberAwayTravels(String seasonId, Long personId) {
        List<CompetitionType> competitionTypes = List.of(
                CompetitionType.CHAMPIONSHIP,
                CompetitionType.PLAYOFFS,
                CompetitionType.CUP,
                CompetitionType.EUROPA_LEAGUE,
                CompetitionType.CHAMPIONS_LEAGUE
        );
        return personTravelRepository.countTravelsPerSeason(seasonId, personId, Place.AWAY, competitionTypes);
    }
}
