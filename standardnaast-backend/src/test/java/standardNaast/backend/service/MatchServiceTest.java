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
import standardNaast.backend.dto.MatchCreateUpdateDto;
import standardNaast.backend.dto.MatchDto;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.mapper.MatchMapper;
import standardNaast.backend.repository.MatchRepository;
import standardNaast.backend.repository.SeasonRepository;
import standardNaast.backend.repository.SeasonTeamRepository;
import standardNaast.backend.repository.TeamRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private SeasonRepository seasonRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private SeasonTeamRepository seasonTeamRepository;

    @Spy
    private MatchMapper matchMapper = Mappers.getMapper(MatchMapper.class);

    @InjectMocks
    private MatchServiceImpl matchService;

    private Season sampleSeason;
    private Team sampleTeam;
    private Match sampleMatch;
    private MatchCreateUpdateDto sampleDto;

    @BeforeEach
    void setUp() {
        this.sampleSeason = Season.builder().id("2024-2025").build();
        this.sampleTeam = Team.builder().id(10L).name("Anderlecht").build();

        this.sampleMatch = Match.builder()
                .id(1L)
                .season(this.sampleSeason)
                .opponent(this.sampleTeam)
                .dateMatch(LocalDateTime.of(2024, 10, 20, 18, 30))
                .place(Place.HOME)
                .competitionType(CompetitionType.CHAMPIONSHIP)
                .matchType(MatchType.GROUP_MATCH)
                .priceType(PriceType.TOP)
                .build();

        this.sampleDto = new MatchCreateUpdateDto(
                "2024-2025",
                10L,
                LocalDateTime.of(2024, 10, 20, 18, 30),
                Place.HOME,
                CompetitionType.CHAMPIONSHIP,
                MatchType.GROUP_MATCH,
                PriceType.TOP
        );
    }

    @Test
    void getMatches_shouldReturnPagedMatches() {
        final Pageable pageable = PageRequest.of(0, 10);
        when(this.matchRepository.searchMatches("2024-2025", null, null, pageable))
                .thenReturn(new PageImpl<>(List.of(this.sampleMatch)));

        final Page<MatchDto> result = this.matchService.getMatches("2024-2025", null, null, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst().opponentName()).isEqualTo("Anderlecht");
    }

    @Test
    void getMatchById_whenExists_shouldReturnMatch() {
        when(this.matchRepository.findById(1L)).thenReturn(Optional.of(this.sampleMatch));

        final MatchDto result = this.matchService.getMatchById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.place()).isEqualTo(Place.HOME);
    }

    @Test
    void getMatchById_whenNotFound_shouldThrowException() {
        when(this.matchRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.matchService.getMatchById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createMatch_shouldResolveDependenciesAndSave() {
        when(this.seasonRepository.findById("2024-2025")).thenReturn(Optional.of(this.sampleSeason));
        when(this.teamRepository.findById(10L)).thenReturn(Optional.of(this.sampleTeam));
        when(this.seasonTeamRepository.findBySeasonIdAndOpponentId("2024-2025", 10L)).thenReturn(Optional.empty());
        when(this.matchRepository.save(any(Match.class))).thenReturn(this.sampleMatch);

        final MatchDto created = this.matchService.createMatch(this.sampleDto);

        assertThat(created).isNotNull();
        assertThat(created.opponentName()).isEqualTo("Anderlecht");
        verify(this.matchRepository).save(any(Match.class));
    }

    @Test
    void deleteMatch_whenExists_shouldDelete() {
        when(this.matchRepository.existsById(1L)).thenReturn(true);
        doNothing().when(this.matchRepository).deleteById(1L);

        this.matchService.deleteMatch(1L);

        verify(this.matchRepository).deleteById(1L);
    }
}
