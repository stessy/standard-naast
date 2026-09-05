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
import standardNaast.backend.domain.PersonTravelType;
import standardNaast.backend.domain.Place;
import standardNaast.backend.domain.Season;
import standardNaast.backend.domain.TravelPrice;
import standardNaast.backend.dto.TravelPriceCreateUpdateDto;
import standardNaast.backend.dto.TravelPriceDto;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.mapper.TravelPriceMapper;
import standardNaast.backend.repository.SeasonRepository;
import standardNaast.backend.repository.TravelPriceRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TravelPriceServiceTest {

    @Mock
    private TravelPriceRepository travelPriceRepository;

    @Mock
    private SeasonRepository seasonRepository;

    @Spy
    private TravelPriceMapper travelPriceMapper = Mappers.getMapper(TravelPriceMapper.class);

    @InjectMocks
    private TravelPriceServiceImpl travelPriceService;

    private Season sampleSeason;
    private TravelPrice sampleTravelPrice;
    private TravelPriceCreateUpdateDto sampleDto;

    @BeforeEach
    void setUp() {
        this.sampleSeason = Season.builder().id("2024-2025").build();

        this.sampleTravelPrice = TravelPrice.builder()
                .id(1L)
                .season(this.sampleSeason)
                .montant(new BigDecimal("15.00"))
                .place(Place.AWAY)
                .membre(true)
                .personTravelType(PersonTravelType.MAJOR)
                .build();

        this.sampleDto = new TravelPriceCreateUpdateDto(
                "2024-2025",
                new BigDecimal("15.00"),
                Place.AWAY,
                true,
                PersonTravelType.MAJOR
        );
    }

    @Test
    void getAllTravelPrices_shouldReturnPagedPrices() {
        final Pageable pageable = PageRequest.of(0, 10);
        when(this.travelPriceRepository.searchTravelPrices("2024-2025", Place.AWAY, true, pageable))
                .thenReturn(new PageImpl<>(List.of(this.sampleTravelPrice)));

        final Page<TravelPriceDto> result = this.travelPriceService.getAllTravelPrices("2024-2025", Place.AWAY, true, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst().montant()).isEqualByComparingTo("15.00");
        assertThat(result.getContent().getFirst().seasonId()).isEqualTo("2024-2025");
    }

    @Test
    void getTravelPricesBySeason_shouldReturnList() {
        when(this.travelPriceRepository.findBySeasonId("2024-2025"))
                .thenReturn(List.of(this.sampleTravelPrice));

        final List<TravelPriceDto> result = this.travelPriceService.getTravelPricesBySeason("2024-2025");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().place()).isEqualTo(Place.AWAY);
    }

    @Test
    void getTravelPriceById_whenExists_shouldReturnPrice() {
        when(this.travelPriceRepository.findById(1L)).thenReturn(Optional.of(this.sampleTravelPrice));

        final TravelPriceDto result = this.travelPriceService.getTravelPriceById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.montant()).isEqualByComparingTo("15.00");
    }

    @Test
    void getTravelPriceById_whenNotFound_shouldThrowException() {
        when(this.travelPriceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.travelPriceService.getTravelPriceById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createTravelPrice_shouldResolveSeasonAndSave() {
        when(this.seasonRepository.findById("2024-2025")).thenReturn(Optional.of(this.sampleSeason));
        when(this.travelPriceRepository.save(any(TravelPrice.class))).thenReturn(this.sampleTravelPrice);

        final TravelPriceDto created = this.travelPriceService.createTravelPrice(this.sampleDto);

        assertThat(created).isNotNull();
        assertThat(created.seasonId()).isEqualTo("2024-2025");
        verify(this.travelPriceRepository).save(any(TravelPrice.class));
    }

    @Test
    void updateTravelPrice_whenExists_shouldUpdateAndSave() {
        when(this.travelPriceRepository.findById(1L)).thenReturn(Optional.of(this.sampleTravelPrice));
        when(this.travelPriceRepository.save(any(TravelPrice.class))).thenReturn(this.sampleTravelPrice);

        final TravelPriceDto updated = this.travelPriceService.updateTravelPrice(1L, this.sampleDto);

        assertThat(updated).isNotNull();
        verify(this.travelPriceRepository).save(this.sampleTravelPrice);
    }

    @Test
    void deleteTravelPrice_whenExists_shouldDelete() {
        when(this.travelPriceRepository.existsById(1L)).thenReturn(true);
        doNothing().when(this.travelPriceRepository).deleteById(1L);

        this.travelPriceService.deleteTravelPrice(1L);

        verify(this.travelPriceRepository).deleteById(1L);
    }
}
