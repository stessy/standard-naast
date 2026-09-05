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
import standardNaast.backend.domain.Abonnement;
import standardNaast.backend.domain.AbonnementPrices;
import standardNaast.backend.domain.AbonnementStatus;
import standardNaast.backend.domain.CompetitionType;
import standardNaast.backend.domain.Person;
import standardNaast.backend.domain.PersonType;
import standardNaast.backend.domain.Season;
import standardNaast.backend.dto.AbonnementCreateUpdateDto;
import standardNaast.backend.dto.AbonnementDto;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.mapper.AbonnementMapper;
import standardNaast.backend.mapper.AbonnementPriceMapper;
import standardNaast.backend.repository.AbonnementPricesRepository;
import standardNaast.backend.repository.AbonnementRepository;
import standardNaast.backend.repository.PersonRepository;
import standardNaast.backend.repository.SeasonRepository;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbonnementServiceTest {

    @Mock
    private AbonnementRepository abonnementRepository;

    @Mock
    private SeasonRepository seasonRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private AbonnementPricesRepository abonnementPricesRepository;

    @Spy
    private AbonnementMapper abonnementMapper = Mappers.getMapper(AbonnementMapper.class);

    @InjectMocks
    private AbonnementServiceImpl abonnementService;

    private Season sampleSeason;
    private Person samplePerson;
    private AbonnementPrices samplePrice;
    private Abonnement sampleAbonnement;
    private AbonnementCreateUpdateDto sampleDto;

    @BeforeEach
    void setUp() throws Exception {
        AbonnementPriceMapper priceMapper = Mappers.getMapper(AbonnementPriceMapper.class);
        Field field = abonnementMapper.getClass().getDeclaredField("abonnementPriceMapper");
        field.setAccessible(true);
        field.set(abonnementMapper, priceMapper);

        this.sampleSeason = Season.builder().id("2024-2025").build();
        this.samplePerson = Person.builder().id(100L).memberNumber(10L).firstname("Eden").name("Hazard").build();
        this.samplePrice = new AbonnementPrices(1L, this.sampleSeason, 300L, 1, "T1", PersonType.ADULT, CompetitionType.CHAMPIONSHIP);

        this.sampleAbonnement = new Abonnement(
                1L,
                this.samplePrice,
                "A",
                "12",
                10L,
                true,
                50L,
                this.sampleSeason,
                this.samplePerson,
                AbonnementStatus.NEW,
                "T1"
        );

        this.sampleDto = new AbonnementCreateUpdateDto(
                1L,
                "A",
                "12",
                10L,
                true,
                50L,
                "2024-2025",
                100L,
                AbonnementStatus.NEW,
                "T1"
        );
    }

    @Test
    void getAbonnements_withSeason_shouldReturnPagedAbonnements() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Abonnement> page = new PageImpl<>(List.of(this.sampleAbonnement));
        when(this.abonnementRepository.findBySeason_Id("2024-2025", pageable)).thenReturn(page);

        Page<AbonnementDto> result = this.abonnementService.getAbonnements("2024-2025", pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().personFirstName()).isEqualTo("Eden");
    }

    @Test
    void getAbonnementsBySeason_shouldReturnList() {
        when(this.abonnementRepository.findBySeason_Id("2024-2025")).thenReturn(List.of(this.sampleAbonnement));

        List<AbonnementDto> result = this.abonnementService.getAbonnementsBySeason("2024-2025");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().place()).isEqualTo("12");
    }

    @Test
    void getAbonnementsByMember_shouldReturnList() {
        when(this.abonnementRepository.findByPersonne_Id(100L)).thenReturn(List.of(this.sampleAbonnement));

        List<AbonnementDto> result = this.abonnementService.getAbonnementsByMember(100L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().personName()).isEqualTo("Hazard");
    }

    @Test
    void getAbonnementById_whenExists_shouldReturnDto() {
        when(this.abonnementRepository.findById(1L)).thenReturn(Optional.of(this.sampleAbonnement));

        AbonnementDto result = this.abonnementService.getAbonnementById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void getAbonnementById_whenNotFound_shouldThrowException() {
        when(this.abonnementRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.abonnementService.getAbonnementById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createAbonnement_shouldSaveAndReturnDto() {
        when(this.seasonRepository.findById("2024-2025")).thenReturn(Optional.of(this.sampleSeason));
        when(this.personRepository.findById(100L)).thenReturn(Optional.of(this.samplePerson));
        when(this.abonnementPricesRepository.findById(1L)).thenReturn(Optional.of(this.samplePrice));
        when(this.abonnementRepository.save(any(Abonnement.class))).thenReturn(this.sampleAbonnement);

        AbonnementDto created = this.abonnementService.createAbonnement(this.sampleDto);

        assertThat(created).isNotNull();
        assertThat(created.seasonId()).isEqualTo("2024-2025");
        assertThat(created.personId()).isEqualTo(100L);
    }

    @Test
    void deleteAbonnement_whenExists_shouldDelete() {
        when(this.abonnementRepository.existsById(1L)).thenReturn(true);
        doNothing().when(this.abonnementRepository).deleteById(1L);

        this.abonnementService.deleteAbonnement(1L);

        verify(this.abonnementRepository).deleteById(1L);
    }

    @Test
    void updateStatus_shouldUpdateAll() {
        when(this.abonnementRepository.findAllById(List.of(1L))).thenReturn(List.of(this.sampleAbonnement));
        when(this.abonnementRepository.saveAll(anyList())).thenReturn(List.of(this.sampleAbonnement));

        this.abonnementService.updateStatus(List.of(1L), AbonnementStatus.PURCHASED);

        assertThat(this.sampleAbonnement.getAbonnementStatus()).isEqualTo(AbonnementStatus.PURCHASED);
        verify(this.abonnementRepository).saveAll(anyList());
    }

    @Test
    void updatePaymentStatus_shouldUpdateAll() {
        when(this.abonnementRepository.findAllById(List.of(1L))).thenReturn(List.of(this.sampleAbonnement));
        when(this.abonnementRepository.saveAll(anyList())).thenReturn(List.of(this.sampleAbonnement));

        this.abonnementService.updatePaymentStatus(List.of(1L), true);

        assertThat(this.sampleAbonnement.isPaye()).isTrue();
        verify(this.abonnementRepository).saveAll(anyList());
    }
}
