package standardNaast.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import standardNaast.backend.domain.Season;
import standardNaast.backend.domain.SeasonTeam;
import standardNaast.backend.domain.Team;
import standardNaast.backend.dto.SeasonCreateUpdateDto;
import standardNaast.backend.dto.SeasonDto;
import standardNaast.backend.dto.TeamDto;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.mapper.SeasonMapper;
import standardNaast.backend.mapper.TeamMapper;
import standardNaast.backend.repository.SeasonRepository;
import standardNaast.backend.repository.SeasonTeamRepository;
import standardNaast.backend.repository.TeamRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeasonServiceTest {

    @Mock
    private SeasonRepository seasonRepository;

    @Mock
    private SeasonTeamRepository seasonTeamRepository;

    @Mock
    private TeamRepository teamRepository;

    @Spy
    private SeasonMapper seasonMapper = Mappers.getMapper(SeasonMapper.class);

    @Spy
    private TeamMapper teamMapper = Mappers.getMapper(TeamMapper.class);

    @InjectMocks
    private SeasonServiceImpl seasonService;

    private Season sampleSeason;
    private SeasonCreateUpdateDto sampleDto;

    @BeforeEach
    void setUp() {
        this.sampleSeason = Season.builder()
                .id("2024-2025")
                .dateStart(LocalDate.of(2024, 7, 1))
                .dateEnd(LocalDate.of(2025, 6, 30))
                .dateFirstMatchChampionship(LocalDate.of(2024, 7, 28))
                .european(true)
                .montantCotisation(BigDecimal.valueOf(15.00))
                .build();

        this.sampleDto = new SeasonCreateUpdateDto(
                "2024-2025",
                LocalDate.of(2024, 7, 1),
                LocalDate.of(2025, 6, 30),
                LocalDate.of(2024, 7, 28),
                true,
                BigDecimal.valueOf(15.00)
        );
    }

    @Test
    void getAllSeasons_shouldReturnAllSeasons() {
        when(this.seasonRepository.findAllByOrderByDateStartDesc()).thenReturn(List.of(this.sampleSeason));

        final List<SeasonDto> result = this.seasonService.getAllSeasons();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo("2024-2025");
        verify(this.seasonRepository).findAllByOrderByDateStartDesc();
    }

    @Test
    void getSeasonById_whenExists_shouldReturnSeason() {
        when(this.seasonRepository.findById("2024-2025")).thenReturn(Optional.of(this.sampleSeason));

        final SeasonDto result = this.seasonService.getSeasonById("2024-2025");

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("2024-2025");
        assertThat(result.european()).isTrue();
    }

    @Test
    void getSeasonById_whenNotFound_shouldThrowException() {
        when(this.seasonRepository.findById("9999-9999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.seasonService.getSeasonById("9999-9999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("9999-9999");
    }

    @Test
    void createSeason_shouldSaveAndReturnDto() {
        when(this.seasonRepository.save(any(Season.class))).thenReturn(this.sampleSeason);

        final SeasonDto created = this.seasonService.createSeason(this.sampleDto);

        assertThat(created).isNotNull();
        assertThat(created.id()).isEqualTo("2024-2025");
        verify(this.seasonRepository).save(any(Season.class));
    }

    @Test
    void deleteSeason_whenExists_shouldDelete() {
        when(this.seasonRepository.existsById("2024-2025")).thenReturn(true);
        doNothing().when(this.seasonRepository).deleteById("2024-2025");

        this.seasonService.deleteSeason("2024-2025");

        verify(this.seasonRepository).deleteById("2024-2025");
    }

    @Test
    void getTeamsForSeason_shouldReturnTeams() {
        final Team team = Team.builder().id(10L).name("Anderlecht").build();
        final SeasonTeam seasonTeam = SeasonTeam.builder().id(1L).season(this.sampleSeason).opponent(team).build();

        when(this.seasonRepository.existsById("2024-2025")).thenReturn(true);
        when(this.seasonTeamRepository.findBySeasonId("2024-2025")).thenReturn(List.of(seasonTeam));

        final List<TeamDto> result = this.seasonService.getTeamsForSeason("2024-2025");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("Anderlecht");
    }
}
