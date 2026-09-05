package standardNaast.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import standardNaast.backend.dto.TeamCreateUpdateDto;
import standardNaast.backend.dto.TeamDto;
import standardNaast.backend.service.TeamService;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Tag(name = "Teams", description = "API de gestion des équipes")
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    @Operation(summary = "Lister et rechercher les équipes avec pagination")
    public ResponseEntity<Page<TeamDto>> getTeams(
            @RequestParam(required = false) final String search,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) final Pageable pageable) {
        return ResponseEntity.ok(this.teamService.getTeams(search, pageable));
    }

    @GetMapping("/all")
    @Operation(summary = "Lister toutes les équipes sans pagination")
    public ResponseEntity<List<TeamDto>> getAllTeams() {
        return ResponseEntity.ok(this.teamService.getAllTeams());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'une équipe par son identifiant")
    public ResponseEntity<TeamDto> getTeamById(@PathVariable final Long id) {
        return ResponseEntity.ok(this.teamService.getTeamById(id));
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle équipe")
    public ResponseEntity<TeamDto> createTeam(@Valid @RequestBody final TeamCreateUpdateDto dto) {
        final TeamDto created = this.teamService.createTeam(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une équipe existante")
    public ResponseEntity<TeamDto> updateTeam(
            @PathVariable final Long id,
            @Valid @RequestBody final TeamCreateUpdateDto dto) {
        return ResponseEntity.ok(this.teamService.updateTeam(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer une équipe")
    public void deleteTeam(@PathVariable final Long id) {
        this.teamService.deleteTeam(id);
    }
}
