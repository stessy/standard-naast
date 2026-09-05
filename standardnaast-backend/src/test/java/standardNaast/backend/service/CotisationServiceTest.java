package standardNaast.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import standardNaast.backend.domain.Cotisation;
import standardNaast.backend.dto.CotisationCreateUpdateDto;
import standardNaast.backend.dto.CotisationDto;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.mapper.CotisationMapper;
import standardNaast.backend.repository.CotisationRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CotisationServiceTest {

    @Mock
    private CotisationRepository cotisationRepository;

    private CotisationMapper cotisationMapper = Mappers.getMapper(CotisationMapper.class);

    private CotisationService cotisationService;

    @BeforeEach
    void setUp() {
        cotisationService = new CotisationServiceImpl(cotisationRepository, cotisationMapper);
    }

    @Test
    void shouldGetAllCotisations() {
        Cotisation cot1 = Cotisation.builder().anneeCotisation(2023L).montantCotisation(BigDecimal.valueOf(20.0)).build();
        Cotisation cot2 = Cotisation.builder().anneeCotisation(2024L).montantCotisation(BigDecimal.valueOf(25.0)).build();
        when(cotisationRepository.findAll()).thenReturn(List.of(cot1, cot2));

        List<CotisationDto> result = cotisationService.getAllCotisations();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).anneeCotisation()).isEqualTo(2023L);
        assertThat(result.get(1).anneeCotisation()).isEqualTo(2024L);
    }

    @Test
    void shouldGetCotisationByYear() {
        Cotisation cot = Cotisation.builder().anneeCotisation(2024L).montantCotisation(BigDecimal.valueOf(25.0)).build();
        when(cotisationRepository.findById(2024L)).thenReturn(Optional.of(cot));

        CotisationDto result = cotisationService.getCotisationByYear(2024L);

        assertThat(result.anneeCotisation()).isEqualTo(2024L);
        assertThat(result.montantCotisation()).isEqualByComparingTo(BigDecimal.valueOf(25.0));
    }

    @Test
    void shouldThrowWhenCotisationNotFound() {
        when(cotisationRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cotisationService.getCotisationByYear(9999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldCreateCotisation() {
        CotisationCreateUpdateDto dto = new CotisationCreateUpdateDto(2025L, BigDecimal.valueOf(30.0));
        when(cotisationRepository.existsById(2025L)).thenReturn(false);
        when(cotisationRepository.save(any(Cotisation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CotisationDto result = cotisationService.createCotisation(dto);

        assertThat(result.anneeCotisation()).isEqualTo(2025L);
        assertThat(result.montantCotisation()).isEqualByComparingTo(BigDecimal.valueOf(30.0));
    }

    @Test
    void shouldThrowWhenCreatingDuplicateCotisation() {
        CotisationCreateUpdateDto dto = new CotisationCreateUpdateDto(2025L, BigDecimal.valueOf(30.0));
        when(cotisationRepository.existsById(2025L)).thenReturn(true);

        assertThatThrownBy(() -> cotisationService.createCotisation(dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldDeleteCotisation() {
        when(cotisationRepository.existsById(2024L)).thenReturn(true);

        cotisationService.deleteCotisation(2024L);

        verify(cotisationRepository).deleteById(2024L);
    }
}
