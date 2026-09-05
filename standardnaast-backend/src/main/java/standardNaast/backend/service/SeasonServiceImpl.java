package standardNaast.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SeasonServiceImpl implements SeasonService {

    private final SeasonRepository seasonRepository;
    private final SeasonTeamRepository seasonTeamRepository;
    private final TeamRepository teamRepository;
    private final SeasonMapper seasonMapper;
    private final TeamMapper teamMapper;

    @Override
    public List<SeasonDto> getAllSeasons() {
        log.debug("Fetching all seasons");
        return this.seasonRepository.findAllByOrderByDateStartDesc().stream()
                .map(this.seasonMapper::toDto)
                .toList();
    }

    @Override
    public SeasonDto getSeasonById(final String id) {
        log.debug("Fetching season with id={}", id);
        return this.seasonRepository.findById(id)
                .map(this.seasonMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Saison non trouvée avec l'id : " + id));
    }

    @Override
    public Optional<SeasonDto> getCurrentSeason() {
        log.debug("Fetching current season for today");
        return this.seasonRepository.findSeasonForDate(LocalDate.now())
                .map(this.seasonMapper::toDto);
    }

    @Override
    @Transactional
    public SeasonDto createSeason(final SeasonCreateUpdateDto dto) {
        log.info("Creating season with id={}", dto.id());
        final Season season = this.seasonMapper.toEntity(dto);
        final Season saved = this.seasonRepository.save(season);
        return this.seasonMapper.toDto(saved);
    }

    @Override
    @Transactional
    public SeasonDto updateSeason(final String id, final SeasonCreateUpdateDto dto) {
        log.info("Updating season with id={}", id);
        final Season season = this.seasonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Saison non trouvée avec l'id : " + id));
        this.seasonMapper.updateEntityFromDto(dto, season);
        final Season updated = this.seasonRepository.save(season);
        return this.seasonMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteSeason(final String id) {
        log.info("Deleting season with id={}", id);
        if (!this.seasonRepository.existsById(id)) {
            throw new ResourceNotFoundException("Saison non trouvée avec l'id : " + id);
        }
        this.seasonRepository.deleteById(id);
    }

    @Override
    public List<TeamDto> getTeamsForSeason(final String seasonId) {
        log.debug("Fetching teams for season={}", seasonId);
        if (!this.seasonRepository.existsById(seasonId)) {
            throw new ResourceNotFoundException("Saison non trouvée avec l'id : " + seasonId);
        }
        return this.seasonTeamRepository.findBySeasonId(seasonId).stream()
                .map(SeasonTeam::getOpponent)
                .map(this.teamMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void addTeamToSeason(final String seasonId, final Long teamId) {
        log.info("Adding team={} to season={}", teamId, seasonId);
        final Season season = this.seasonRepository.findById(seasonId)
                .orElseThrow(() -> new ResourceNotFoundException("Saison non trouvée avec l'id : " + seasonId));
        final Team team = this.teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Équipe non trouvée avec l'id : " + teamId));

        if (!this.seasonTeamRepository.existsBySeasonIdAndOpponentId(seasonId, teamId)) {
            final SeasonTeam seasonTeam = SeasonTeam.builder()
                    .season(season)
                    .opponent(team)
                    .build();
            this.seasonTeamRepository.save(seasonTeam);
        }
    }

    @Override
    @Transactional
    public void removeTeamFromSeason(final String seasonId, final Long teamId) {
        log.info("Removing team={} from season={}", teamId, seasonId);
        this.seasonTeamRepository.findBySeasonIdAndOpponentId(seasonId, teamId)
                .ifPresent(this.seasonTeamRepository::delete);
    }
}
