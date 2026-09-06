package standardNaast.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import standardNaast.backend.domain.Person;
import standardNaast.backend.domain.PersonCotisation;
import standardNaast.backend.domain.Season;
import standardNaast.backend.dto.CotisationsSeasonOverviewDto;
import standardNaast.backend.dto.MemberCardSentBulkUpdateDto;
import standardNaast.backend.dto.PersonCotisationCreateDto;
import standardNaast.backend.dto.PersonCotisationDto;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.mapper.PersonCotisationMapper;
import standardNaast.backend.repository.PersonCotisationRepository;
import standardNaast.backend.repository.PersonRepository;
import standardNaast.backend.repository.SeasonRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonCotisationServiceTest {

    @Mock
    private PersonCotisationRepository personCotisationRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private SeasonRepository seasonRepository;

    private PersonCotisationMapper personCotisationMapper = Mappers.getMapper(PersonCotisationMapper.class);

    private PersonCotisationService personCotisationService;

    @BeforeEach
    void setUp() {
        personCotisationService = new PersonCotisationServiceImpl(
                personCotisationRepository,
                personRepository,
                seasonRepository,
                personCotisationMapper
        );
    }

    @Test
    void shouldRegisterMemberCotisation() {
        Person member = Person.builder().id(10L).memberNumber(100L).firstname("Alice").name("Martin").build();
        Season season = Season.builder().id("2024-2025").build();

        PersonCotisationCreateDto dto = new PersonCotisationCreateDto(10L, "2024-2025", LocalDate.of(2024, 9, 1), false);

        when(personRepository.findById(10L)).thenReturn(Optional.of(member));
        when(seasonRepository.findById("2024-2025")).thenReturn(Optional.of(season));
        when(personCotisationRepository.existsByPersonIdAndSeasonId(10L, "2024-2025")).thenReturn(false);

        PersonCotisation savedEntity = PersonCotisation.builder()
                .id(1001L)
                .person(member)
                .season(season)
                .datePaiement(LocalDate.of(2024, 9, 1))
                .carteMembreEnvoyee(false)
                .build();
        when(personCotisationRepository.save(any(PersonCotisation.class))).thenReturn(savedEntity);

        PersonCotisationDto result = personCotisationService.registerMemberCotisation(dto);

        assertThat(result.id()).isEqualTo(1001L);
        assertThat(result.memberId()).isEqualTo(10L);
        assertThat(result.memberNumber()).isEqualTo(100L);
        assertThat(result.seasonId()).isEqualTo("2024-2025");
        assertThat(result.carteMembreEnvoyee()).isFalse();
    }

    @Test
    void shouldThrowWhenRegisteringDuplicateCotisation() {
        Person member = Person.builder().id(10L).memberNumber(100L).firstname("Alice").name("Martin").build();
        Season season = Season.builder().id("2024-2025").build();

        PersonCotisationCreateDto dto = new PersonCotisationCreateDto(10L, "2024-2025", LocalDate.of(2024, 9, 1), false);

        when(personRepository.findById(10L)).thenReturn(Optional.of(member));
        when(seasonRepository.findById("2024-2025")).thenReturn(Optional.of(season));
        when(personCotisationRepository.existsByPersonIdAndSeasonId(10L, "2024-2025")).thenReturn(true);

        assertThatThrownBy(() -> personCotisationService.registerMemberCotisation(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("existe déjà");
    }

    @Test
    void shouldGetSeasonOverview() {
        Person member1 = Person.builder().id(1L).memberNumber(1L).firstname("Alice").name("Martin").build();
        Person member2 = Person.builder().id(2L).memberNumber(2L).firstname("Bob").name("Dupont").build();
        Person member3 = Person.builder().id(3L).memberNumber(3L).firstname("Charlie").name("Durand").build();

        Season season = Season.builder().id("2024-2025").build();

        PersonCotisation cot1 = PersonCotisation.builder().id(101L).person(member1).season(season).carteMembreEnvoyee(true).datePaiement(LocalDate.now()).build();
        PersonCotisation cot2 = PersonCotisation.builder().id(102L).person(member2).season(season).carteMembreEnvoyee(false).datePaiement(LocalDate.now()).build();

        when(seasonRepository.existsById("2024-2025")).thenReturn(true);
        when(personRepository.findAll()).thenReturn(List.of(member1, member2, member3));
        when(personCotisationRepository.findBySeasonId("2024-2025")).thenReturn(List.of(cot1, cot2));

        CotisationsSeasonOverviewDto overview = personCotisationService.getSeasonOverview("2024-2025");

        assertThat(overview.seasonId()).isEqualTo("2024-2025");
        assertThat(overview.totalMembers()).isEqualTo(3);
        assertThat(overview.totalPaid()).isEqualTo(2);
        assertThat(overview.totalUnpaid()).isEqualTo(1);
        assertThat(overview.paidCardSent()).hasSize(1);
        assertThat(overview.paidCardNotSent()).hasSize(1);
        assertThat(overview.unpaidMembers()).hasSize(1);
    }

    @Test
    void shouldBulkUpdateMemberCardSent() {
        PersonCotisation cot1 = PersonCotisation.builder().id(101L).carteMembreEnvoyee(false).build();
        PersonCotisation cot2 = PersonCotisation.builder().id(102L).carteMembreEnvoyee(false).build();

        when(personCotisationRepository.findAllById(List.of(101L, 102L))).thenReturn(List.of(cot1, cot2));

        MemberCardSentBulkUpdateDto bulkDto = new MemberCardSentBulkUpdateDto(List.of(101L, 102L), true);
        personCotisationService.bulkUpdateMemberCardSent(bulkDto);

        assertThat(cot1.isCarteMembreEnvoyee()).isTrue();
        assertThat(cot2.isCarteMembreEnvoyee()).isTrue();
        verify(personCotisationRepository).saveAll(List.of(cot1, cot2));
    }
}
