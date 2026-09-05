package standardNaast.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import standardNaast.backend.domain.Team;
import standardNaast.backend.dto.TeamCreateUpdateDto;
import standardNaast.backend.dto.TeamDto;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.mapper.TeamMapper;
import standardNaast.backend.repository.TeamRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    @Override
    public Page<TeamDto> getTeams(final String search, final Pageable pageable) {
        log.debug("Fetching teams with search='{}' and pageable={}", search, pageable);
        if (search == null || search.trim().isEmpty()) {
            return this.teamRepository.findAll(pageable).map(this.teamMapper::toDto);
        }
        return this.teamRepository.searchTeams(search.trim(), pageable).map(this.teamMapper::toDto);
    }

    @Override
    public List<TeamDto> getAllTeams() {
        log.debug("Fetching all teams");
        return this.teamRepository.findAllByOrderByNameAsc().stream()
                .map(this.teamMapper::toDto)
                .toList();
    }

    @Override
    public TeamDto getTeamById(final Long id) {
        log.debug("Fetching team with id={}", id);
        return this.teamRepository.findById(id)
                .map(this.teamMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Équipe non trouvée avec l'id : " + id));
    }

    @Override
    @Transactional
    public TeamDto createTeam(final TeamCreateUpdateDto dto) {
        log.info("Creating team with name='{}'", dto.name());
        final Team team = this.teamMapper.toEntity(dto);
        final Team saved = this.teamRepository.save(team);
        return this.teamMapper.toDto(saved);
    }

    @Override
    @Transactional
    public TeamDto updateTeam(final Long id, final TeamCreateUpdateDto dto) {
        log.info("Updating team with id={}", id);
        final Team team = this.teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Équipe non trouvée avec l'id : " + id));
        this.teamMapper.updateEntityFromDto(dto, team);
        final Team updated = this.teamRepository.save(team);
        return this.teamMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteTeam(final Long id) {
        log.info("Deleting team with id={}", id);
        if (!this.teamRepository.existsById(id)) {
            throw new ResourceNotFoundException("Équipe non trouvée avec l'id : " + id);
        }
        this.teamRepository.deleteById(id);
    }
}
