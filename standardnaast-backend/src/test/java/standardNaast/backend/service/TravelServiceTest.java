package standardNaast.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import standardNaast.backend.domain.*;
import standardNaast.backend.dto.MatchTravelOverviewDto;
import standardNaast.backend.dto.PersonTravelCreateDto;
import standardNaast.backend.dto.PersonTravelDto;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.mapper.PersonTravelMapper;
import standardNaast.backend.repository.MatchRepository;
import standardNaast.backend.repository.PersonRepository;
import standardNaast.backend.repository.PersonTravelRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TravelServiceTest {

    @Mock
    private PersonTravelRepository personTravelRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private MatchRepository matchRepository;

    @Spy
    private PersonTravelMapper personTravelMapper = Mappers.getMapper(PersonTravelMapper.class);

    @InjectMocks
    private TravelServiceImpl travelService;

    private Person memberPerson;
    private Person nonMemberPerson;
    private Match sampleMatch;
    private PersonTravel memberTravel;
    private PersonTravel nonMemberTravel;

    @BeforeEach
    void setUp() {
        Season season = Season.builder().id("2024-2025").build();
        Team opponent = Team.builder().id(10L).name("Anderlecht").build();

        this.sampleMatch = Match.builder()
                .id(100L)
                .season(season)
                .opponent(opponent)
                .dateMatch(LocalDateTime.of(2024, 10, 20, 18, 30))
                .place(Place.AWAY)
                .competitionType(CompetitionType.CHAMPIONSHIP)
                .build();

        this.memberPerson = Person.builder()
                .id(1L)
                .memberNumber(42L)
                .firstname("Jean")
                .name("Dupont")
                .build();

        this.nonMemberPerson = Person.builder()
                .id(2L)
                .memberNumber(10050L)
                .firstname("Pierre")
                .name("Durand")
                .build();

        this.memberTravel = PersonTravel.builder()
                .id(10L)
                .person(this.memberPerson)
                .match(this.sampleMatch)
                .carTravelAmount(15L)
                .build();

        this.nonMemberTravel = PersonTravel.builder()
                .id(11L)
                .person(this.nonMemberPerson)
                .match(this.sampleMatch)
                .carTravelAmount(20L)
                .build();
    }

    @Test
    void searchTravels_shouldReturnPagedTravels() {
        final Pageable pageable = PageRequest.of(0, 10);
        when(this.personTravelRepository.searchPersonTravels(100L, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of(this.memberTravel)));

        final Page<PersonTravelDto> result = this.travelService.searchTravels(100L, null, null, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst().firstName()).isEqualTo("Jean");
        assertThat(result.getContent().getFirst().isMember()).isTrue();
    }

    @Test
    void getTravelsByMatch_shouldReturnList() {
        when(this.personTravelRepository.findByMatchId(100L))
                .thenReturn(List.of(this.memberTravel, this.nonMemberTravel));

        final List<PersonTravelDto> result = this.travelService.getTravelsByMatch(100L);

        assertThat(result).hasSize(2);
    }

    @Test
    void getTravelsByPerson_shouldReturnList() {
        when(this.personTravelRepository.findByPersonId(1L))
                .thenReturn(List.of(this.memberTravel));

        final List<PersonTravelDto> result = this.travelService.getTravelsByPerson(1L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().memberId()).isEqualTo(1L);
    }

    @Test
    void getTravelById_whenExists_shouldReturnTravel() {
        when(this.personTravelRepository.findById(10L)).thenReturn(Optional.of(this.memberTravel));

        final PersonTravelDto result = this.travelService.getTravelById(10L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.carTravelAmount()).isEqualTo(15L);
    }

    @Test
    void getTravelById_whenNotFound_shouldThrowException() {
        when(this.personTravelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.travelService.getTravelById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getMatchTravelOverview_shouldReturnOverview() {
        when(this.matchRepository.findById(100L)).thenReturn(Optional.of(this.sampleMatch));
        when(this.personTravelRepository.findByMatchId(100L)).thenReturn(List.of(this.memberTravel, this.nonMemberTravel));

        final MatchTravelOverviewDto overview = this.travelService.getMatchTravelOverview(100L);

        assertThat(overview).isNotNull();
        assertThat(overview.matchId()).isEqualTo(100L);
        assertThat(overview.opponentName()).isEqualTo("Anderlecht");
        assertThat(overview.totalPassengers()).isEqualTo(2);
        assertThat(overview.memberPassengersCount()).isEqualTo(1);
        assertThat(overview.nonMemberPassengersCount()).isEqualTo(1);
        assertThat(overview.totalAmountCollected()).isEqualTo(35L);
        assertThat(overview.memberPassengers()).hasSize(1);
        assertThat(overview.nonMemberPassengers()).hasSize(1);
    }

    @Test
    void registerPersonTravel_shouldSaveAndReturnDto() {
        final PersonTravelCreateDto dto = new PersonTravelCreateDto(1L, 100L, 15L);

        when(this.personTravelRepository.existsByPersonIdAndMatchId(1L, 100L)).thenReturn(false);
        when(this.personRepository.findById(1L)).thenReturn(Optional.of(this.memberPerson));
        when(this.matchRepository.findById(100L)).thenReturn(Optional.of(this.sampleMatch));
        when(this.personTravelRepository.save(any(PersonTravel.class))).thenReturn(this.memberTravel);

        final PersonTravelDto result = this.travelService.registerPersonTravel(dto);

        assertThat(result).isNotNull();
        assertThat(result.memberId()).isEqualTo(1L);
        assertThat(result.carTravelAmount()).isEqualTo(15L);
        verify(this.personTravelRepository).save(any(PersonTravel.class));
    }

    @Test
    void registerPersonTravel_whenAlreadyRegistered_shouldThrowException() {
        final PersonTravelCreateDto dto = new PersonTravelCreateDto(1L, 100L, 15L);

        when(this.personTravelRepository.existsByPersonIdAndMatchId(1L, 100L)).thenReturn(true);

        assertThatThrownBy(() -> this.travelService.registerPersonTravel(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("déjà inscrite");
    }

    @Test
    void removePersonTravel_whenExists_shouldDelete() {
        when(this.personTravelRepository.existsById(10L)).thenReturn(true);
        doNothing().when(this.personTravelRepository).deleteById(10L);

        this.travelService.removePersonTravel(10L);

        verify(this.personTravelRepository).deleteById(10L);
    }

    @Test
    void countMemberAwayTravels_shouldReturnCount() {
        when(this.personTravelRepository.countTravelsPerSeason(eq("2024-2025"), eq(1L), eq(Place.AWAY), anyList()))
                .thenReturn(5L);

        final long count = this.travelService.countMemberAwayTravels("2024-2025", 1L);

        assertThat(count).isEqualTo(5L);
    }
}
