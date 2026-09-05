package standardNaast.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import standardNaast.backend.domain.CompetitionType;
import standardNaast.backend.domain.Match;
import standardNaast.backend.domain.Place;
import standardNaast.backend.domain.Season;
import standardNaast.backend.domain.Team;
import standardNaast.backend.dto.MatchCreateUpdateDto;
import standardNaast.backend.dto.MatchDto;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.mapper.MatchMapper;
import standardNaast.backend.repository.MatchRepository;
import standardNaast.backend.repository.SeasonRepository;
import standardNaast.backend.repository.SeasonTeamRepository;
import standardNaast.backend.repository.TeamRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final SeasonRepository seasonRepository;
    private final TeamRepository teamRepository;
    private final SeasonTeamRepository seasonTeamRepository;
    private final MatchMapper matchMapper;

    @Override
    public Page<MatchDto> getMatches(final String seasonId,
                                     final CompetitionType competitionType,
                                     final Place place,
                                     final Pageable pageable) {
        log.debug("Fetching matches with seasonId={}, competitionType={}, place={}, pageable={}",
                seasonId, competitionType, place, pageable);
        return this.matchRepository.searchMatches(seasonId, competitionType, place, pageable)
                .map(this.matchMapper::toDto);
    }

    @Override
    public List<MatchDto> getMatchesBySeason(final String seasonId) {
        log.debug("Fetching matches for season={}", seasonId);
        return this.matchRepository.findBySeasonIdOrderByDateMatchAsc(seasonId).stream()
                .map(this.matchMapper::toDto)
                .toList();
    }

    @Override
    public MatchDto getMatchById(final Long id) {
        log.debug("Fetching match with id={}", id);
        return this.matchRepository.findById(id)
                .map(this.matchMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Match non trouvé avec l'id : " + id));
    }

    @Override
    @Transactional
    public MatchDto createMatch(final MatchCreateUpdateDto dto) {
        log.info("Creating match for season={} and opponent={}", dto.seasonId(), dto.opponentId());
        final Season season = this.seasonRepository.findById(dto.seasonId())
                .orElseThrow(() -> new ResourceNotFoundException("Saison non trouvée avec l'id : " + dto.seasonId()));
        final Team opponent = this.teamRepository.findById(dto.opponentId())
                .orElseThrow(() -> new ResourceNotFoundException("Équipe non trouvée avec l'id : " + dto.opponentId()));

        final Match match = this.matchMapper.toEntity(dto);
        match.setSeason(season);
        match.setOpponent(opponent);

        this.seasonTeamRepository.findBySeasonIdAndOpponentId(dto.seasonId(), dto.opponentId())
                .ifPresent(match::setSeasonTeam);

        final Match saved = this.matchRepository.save(match);
        return this.matchMapper.toDto(saved);
    }

    @Override
    @Transactional
    public MatchDto updateMatch(final Long id, final MatchCreateUpdateDto dto) {
        log.info("Updating match with id={}", id);
        final Match match = this.matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match non trouvé avec l'id : " + id));

        final Season season = this.seasonRepository.findById(dto.seasonId())
                .orElseThrow(() -> new ResourceNotFoundException("Saison non trouvée avec l'id : " + dto.seasonId()));
        final Team opponent = this.teamRepository.findById(dto.opponentId())
                .orElseThrow(() -> new ResourceNotFoundException("Équipe non trouvée avec l'id : " + dto.opponentId()));

        this.matchMapper.updateEntityFromDto(dto, match);
        match.setSeason(season);
        match.setOpponent(opponent);

        this.seasonTeamRepository.findBySeasonIdAndOpponentId(dto.seasonId(), dto.opponentId())
                .ifPresentOrElse(match::setSeasonTeam, () -> match.setSeasonTeam(null));

        final Match updated = this.matchRepository.save(match);
        return this.matchMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteMatch(final Long id) {
        log.info("Deleting match with id={}", id);
        if (!this.matchRepository.existsById(id)) {
            throw new ResourceNotFoundException("Match non trouvé avec l'id : " + id);
        }
        this.matchRepository.deleteById(id);
    }
}
