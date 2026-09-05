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
import standardNaast.backend.domain.CompetitionType;
import standardNaast.backend.domain.Place;
import standardNaast.backend.dto.MatchCreateUpdateDto;
import standardNaast.backend.dto.MatchDto;
import standardNaast.backend.service.MatchService;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
@Tag(name = "Matches", description = "API de gestion des matches")
public class MatchController {

    private final MatchService matchService;

    @GetMapping
    @Operation(summary = "Lister et rechercher les matches avec pagination et filtres")
    public ResponseEntity<Page<MatchDto>> getMatches(
            @RequestParam(required = false) final String seasonId,
            @RequestParam(required = false) final CompetitionType competitionType,
            @RequestParam(required = false) final Place place,
            @PageableDefault(size = 20, sort = "dateMatch", direction = Sort.Direction.ASC) final Pageable pageable) {
        return ResponseEntity.ok(this.matchService.getMatches(seasonId, competitionType, place, pageable));
    }

    @GetMapping("/by-season/{seasonId}")
    @Operation(summary = "Lister tous les matches d'une saison")
    public ResponseEntity<List<MatchDto>> getMatchesBySeason(@PathVariable final String seasonId) {
        return ResponseEntity.ok(this.matchService.getMatchesBySeason(seasonId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'un match par son identifiant")
    public ResponseEntity<MatchDto> getMatchById(@PathVariable final Long id) {
        return ResponseEntity.ok(this.matchService.getMatchById(id));
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau match")
    public ResponseEntity<MatchDto> createMatch(@Valid @RequestBody final MatchCreateUpdateDto dto) {
        final MatchDto created = this.matchService.createMatch(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un match existant")
    public ResponseEntity<MatchDto> updateMatch(
            @PathVariable final Long id,
            @Valid @RequestBody final MatchCreateUpdateDto dto) {
        return ResponseEntity.ok(this.matchService.updateMatch(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un match")
    public void deleteMatch(@PathVariable final Long id) {
        this.matchService.deleteMatch(id);
    }
}
