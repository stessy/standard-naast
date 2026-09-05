package standardNaast.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import standardNaast.backend.domain.AbonnementPrices;
import standardNaast.backend.domain.CompetitionType;
import standardNaast.backend.domain.PersonType;
import standardNaast.backend.domain.Season;
import standardNaast.backend.dto.AbonnementPriceCreateUpdateDto;
import standardNaast.backend.dto.AbonnementPriceDto;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.mapper.AbonnementPriceMapper;
import standardNaast.backend.repository.AbonnementPricesRepository;
import standardNaast.backend.repository.SeasonRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbonnementPriceServiceTest {

    @Mock
    private AbonnementPricesRepository abonnementPricesRepository;

    @Mock
    private SeasonRepository seasonRepository;

    @Spy
    private AbonnementPriceMapper abonnementPriceMapper = Mappers.getMapper(AbonnementPriceMapper.class);

    @InjectMocks
    private AbonnementPriceServiceImpl abonnementPriceService;

    private Season sampleSeason;
    private AbonnementPrices samplePrice;
    private AbonnementPriceCreateUpdateDto sampleDto;

    @BeforeEach
    void setUp() {
        this.sampleSeason = Season.builder()
                .id("2024-2025")
                .build();

        this.samplePrice = new AbonnementPrices(
                1L,
                this.sampleSeason,
                250L,
                1,
                "T1",
                PersonType.ADULT,
                CompetitionType.CHAMPIONSHIP
        );

        this.sampleDto = new AbonnementPriceCreateUpdateDto(
                "2024-2025",
                250L,
                1,
                "T1",
                PersonType.ADULT,
                CompetitionType.CHAMPIONSHIP
        );
    }

    @Test
    void getPricesBySeason_shouldReturnPrices() {
        when(this.abonnementPricesRepository.findBySeason_Id("2024-2025")).thenReturn(List.of(this.samplePrice));

        List<AbonnementPriceDto> result = this.abonnementPriceService.getPricesBySeason("2024-2025");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().price()).isEqualTo(250L);
        assertThat(result.getFirst().bloc()).isEqualTo("T1");
    }

    @Test
    void getPricesBySeasonAndCompetition_shouldReturnPrices() {
        when(this.seasonRepository.findById("2024-2025")).thenReturn(Optional.of(this.sampleSeason));
        when(this.abonnementPricesRepository.findBySeasonAndTypeCompetition(this.sampleSeason, CompetitionType.CHAMPIONSHIP))
                .thenReturn(List.of(this.samplePrice));

        List<AbonnementPriceDto> result = this.abonnementPriceService.getPricesBySeasonAndCompetition("2024-2025", CompetitionType.CHAMPIONSHIP);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().typeCompetition()).isEqualTo(CompetitionType.CHAMPIONSHIP);
    }

    @Test
    void getPriceById_whenExists_shouldReturnPrice() {
        when(this.abonnementPricesRepository.findById(1L)).thenReturn(Optional.of(this.samplePrice));

        AbonnementPriceDto result = this.abonnementPriceService.getPriceById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void getPriceById_whenNotFound_shouldThrowException() {
        when(this.abonnementPricesRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.abonnementPriceService.getPriceById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createPrice_shouldSaveAndReturnDto() {
        when(this.seasonRepository.findById("2024-2025")).thenReturn(Optional.of(this.sampleSeason));
        when(this.abonnementPricesRepository.save(any(AbonnementPrices.class))).thenReturn(this.samplePrice);

        AbonnementPriceDto created = this.abonnementPriceService.createPrice(this.sampleDto);

        assertThat(created).isNotNull();
        assertThat(created.price()).isEqualTo(250L);
    }

    @Test
    void deletePrice_whenExists_shouldDelete() {
        when(this.abonnementPricesRepository.existsById(1L)).thenReturn(true);
        doNothing().when(this.abonnementPricesRepository).deleteById(1L);

        this.abonnementPriceService.deletePrice(1L);

        verify(this.abonnementPricesRepository).deleteById(1L);
    }
}
