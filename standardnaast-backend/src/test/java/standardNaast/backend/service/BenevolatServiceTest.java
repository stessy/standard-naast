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
import standardNaast.backend.domain.Benevolat;
import standardNaast.backend.domain.Person;
import standardNaast.backend.domain.Season;
import standardNaast.backend.dto.BenevolatCreateUpdateDto;
import standardNaast.backend.dto.BenevolatDto;
import standardNaast.backend.dto.MemberBenevolatSummaryDto;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.mapper.BenevolatMapper;
import standardNaast.backend.repository.BenevolatRepository;
import standardNaast.backend.repository.PersonRepository;
import standardNaast.backend.repository.SeasonRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BenevolatServiceTest {

    @Mock
    private BenevolatRepository benevolatRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private SeasonRepository seasonRepository;

    @Spy
    private BenevolatMapper benevolatMapper = Mappers.getMapper(BenevolatMapper.class);

    @InjectMocks
    private BenevolatServiceImpl benevolatService;

    private Person samplePerson;
    private Season sampleSeason;
    private Benevolat sampleBenevolat;
    private BenevolatCreateUpdateDto sampleCreateDto;

    @BeforeEach
    void setUp() {
        this.samplePerson = Person.builder()
                .id(1L)
                .memberNumber(101L)
                .firstname("Marc")
                .name("Dubois")
                .build();

        this.sampleSeason = Season.builder()
                .id("2024-2025")
                .dateStart(LocalDate.of(2024, 7, 1))
                .dateEnd(LocalDate.of(2025, 6, 30))
                .build();

        this.sampleBenevolat = Benevolat.builder()
                .id(10L)
                .person(this.samplePerson)
                .amount(new BigDecimal("50.00"))
                .typeBenevolat("Barman barbecue")
                .dateBenevolat(LocalDate.of(2024, 9, 15))
                .build();

        this.sampleCreateDto = new BenevolatCreateUpdateDto(
                1L,
                new BigDecimal("50.00"),
                "Barman barbecue",
                LocalDate.of(2024, 9, 15)
        );
    }

    @Test
    void searchBenevolats_shouldReturnPagedResults() {
        final Pageable pageable = PageRequest.of(0, 10);
        when(this.benevolatRepository.searchBenevolats(1L, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of(this.sampleBenevolat)));

        final Page<BenevolatDto> result = this.benevolatService.searchBenevolats(1L, null, null, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst().typeBenevolat()).isEqualTo("Barman barbecue");
        assertThat(result.getContent().getFirst().amount()).isEqualByComparingTo("50.00");
    }

    @Test
    void getBenevolatsByPerson_shouldReturnList() {
        when(this.benevolatRepository.findByPersonId(1L)).thenReturn(List.of(this.sampleBenevolat));

        final List<BenevolatDto> result = this.benevolatService.getBenevolatsByPerson(1L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().firstName()).isEqualTo("Marc");
    }

    @Test
    void getBenevolatsByPersonAndSeason_shouldReturnList() {
        when(this.seasonRepository.findById("2024-2025")).thenReturn(Optional.of(this.sampleSeason));
        when(this.benevolatRepository.findByPersonIdAndDateBenevolatBetween(1L, this.sampleSeason.getDateStart(), this.sampleSeason.getDateEnd()))
                .thenReturn(List.of(this.sampleBenevolat));

        final List<BenevolatDto> result = this.benevolatService.getBenevolatsByPersonAndSeason(1L, "2024-2025");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(10L);
    }

    @Test
    void getMemberBenevolatSummary_withSeason_shouldCalculateTotals() {
        when(this.personRepository.findById(1L)).thenReturn(Optional.of(this.samplePerson));
        when(this.seasonRepository.findById("2024-2025")).thenReturn(Optional.of(this.sampleSeason));
        when(this.benevolatRepository.findByPersonIdAndDateBenevolatBetween(1L, this.sampleSeason.getDateStart(), this.sampleSeason.getDateEnd()))
                .thenReturn(List.of(this.sampleBenevolat));
        when(this.benevolatRepository.sumAmountByPersonIdAndPeriod(1L, this.sampleSeason.getDateStart(), this.sampleSeason.getDateEnd()))
                .thenReturn(new BigDecimal("50.00"));

        final MemberBenevolatSummaryDto summary = this.benevolatService.getMemberBenevolatSummary(1L, "2024-2025");

        assertThat(summary.personId()).isEqualTo(1L);
        assertThat(summary.totalAmount()).isEqualByComparingTo("50.00");
        assertThat(summary.count()).isEqualTo(1);
    }

    @Test
    void getBenevolatById_whenExists_shouldReturnDto() {
        when(this.benevolatRepository.findById(10L)).thenReturn(Optional.of(this.sampleBenevolat));

        final BenevolatDto result = this.benevolatService.getBenevolatById(10L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
    }

    @Test
    void getBenevolatById_whenNotFound_shouldThrowException() {
        when(this.benevolatRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.benevolatService.getBenevolatById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createBenevolat_shouldSaveAndReturnDto() {
        when(this.personRepository.findById(1L)).thenReturn(Optional.of(this.samplePerson));
        when(this.benevolatRepository.save(any(Benevolat.class))).thenReturn(this.sampleBenevolat);

        final BenevolatDto created = this.benevolatService.createBenevolat(this.sampleCreateDto);

        assertThat(created).isNotNull();
        assertThat(created.id()).isEqualTo(10L);
        verify(this.benevolatRepository).save(any(Benevolat.class));
    }

    @Test
    void updateBenevolat_whenExists_shouldUpdate() {
        when(this.benevolatRepository.findById(10L)).thenReturn(Optional.of(this.sampleBenevolat));
        when(this.benevolatRepository.save(any(Benevolat.class))).thenReturn(this.sampleBenevolat);

        final BenevolatDto updated = this.benevolatService.updateBenevolat(10L, this.sampleCreateDto);

        assertThat(updated).isNotNull();
        verify(this.benevolatRepository).save(this.sampleBenevolat);
    }

    @Test
    void deleteBenevolat_whenExists_shouldDelete() {
        when(this.benevolatRepository.existsById(10L)).thenReturn(true);
        doNothing().when(this.benevolatRepository).deleteById(10L);

        this.benevolatService.deleteBenevolat(10L);

        verify(this.benevolatRepository).deleteById(10L);
    }
}
