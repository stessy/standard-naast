package standardNaast.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import standardNaast.backend.dto.SeasonCreateUpdateDto;
import standardNaast.backend.dto.SeasonDto;
import standardNaast.backend.dto.TeamDto;
import standardNaast.backend.service.SeasonService;

import java.util.List;

@RestController
@RequestMapping("/api/seasons")
@RequiredArgsConstructor
@Tag(name = "Seasons", description = "API de gestion des saisons")
public class SeasonController {

    private final SeasonService seasonService;

    @GetMapping
    @Operation(summary = "Lister toutes les saisons")
    public ResponseEntity<List<SeasonDto>> getAllSeasons() {
        return ResponseEntity.ok(this.seasonService.getAllSeasons());
    }

    @GetMapping("/current")
    @Operation(summary = "Obtenir la saison en cours selon la date du jour")
    public ResponseEntity<SeasonDto> getCurrentSeason() {
        return this.seasonService.getCurrentSeason()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'une saison par son identifiant")
    public ResponseEntity<SeasonDto> getSeasonById(@PathVariable final String id) {
        return ResponseEntity.ok(this.seasonService.getSeasonById(id));
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle saison")
    public ResponseEntity<SeasonDto> createSeason(@Valid @RequestBody final SeasonCreateUpdateDto dto) {
        final SeasonDto created = this.seasonService.createSeason(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une saison existante")
    public ResponseEntity<SeasonDto> updateSeason(
            @PathVariable final String id,
            @Valid @RequestBody final SeasonCreateUpdateDto dto) {
        return ResponseEntity.ok(this.seasonService.updateSeason(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer une saison")
    public void deleteSeason(@PathVariable final String id) {
        this.seasonService.deleteSeason(id);
    }

    @GetMapping("/{id}/teams")
    @Operation(summary = "Lister les équipes associées à une saison")
    public ResponseEntity<List<TeamDto>> getTeamsForSeason(@PathVariable final String id) {
        return ResponseEntity.ok(this.seasonService.getTeamsForSeason(id));
    }

    @PostMapping("/{id}/teams/{teamId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Ajouter une équipe à une saison")
    public void addTeamToSeason(@PathVariable final String id, @PathVariable final Long teamId) {
        this.seasonService.addTeamToSeason(id, teamId);
    }

    @DeleteMapping("/{id}/teams/{teamId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Retirer une équipe d'une saison")
    public void removeTeamFromSeason(@PathVariable final String id, @PathVariable final Long teamId) {
        this.seasonService.removeTeamFromSeason(id, teamId);
    }
}
