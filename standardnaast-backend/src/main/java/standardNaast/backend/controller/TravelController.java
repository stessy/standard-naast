package standardNaast.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import standardNaast.backend.dto.MatchTravelOverviewDto;
import standardNaast.backend.dto.PersonTravelCreateDto;
import standardNaast.backend.dto.PersonTravelDto;
import standardNaast.backend.service.TravelService;

import java.util.List;

@RestController
@RequestMapping("/api/travels")
@RequiredArgsConstructor
@Tag(name = "Travels", description = "API de gestion des déplacements en car")
public class TravelController {

    private final TravelService travelService;

    @GetMapping
    @Operation(summary = "Rechercher et lister les inscriptions de déplacement avec pagination et filtres")
    public ResponseEntity<Page<PersonTravelDto>> searchTravels(
            @RequestParam(required = false) final Long matchId,
            @RequestParam(required = false) final Long personId,
            @RequestParam(required = false) final String seasonId,
            @PageableDefault(size = 20) final Pageable pageable) {
        return ResponseEntity.ok(this.travelService.searchTravels(matchId, personId, seasonId, pageable));
    }

    @GetMapping("/match/{matchId}")
    @Operation(summary = "Lister les inscriptions de déplacement pour un match donné")
    public ResponseEntity<List<PersonTravelDto>> getTravelsByMatch(@PathVariable final Long matchId) {
        return ResponseEntity.ok(this.travelService.getTravelsByMatch(matchId));
    }

    @GetMapping("/person/{personId}")
    @Operation(summary = "Lister les inscriptions de déplacement pour une personne donnée")
    public ResponseEntity<List<PersonTravelDto>> getTravelsByPerson(@PathVariable final Long personId) {
        return ResponseEntity.ok(this.travelService.getTravelsByPerson(personId));
    }

    @GetMapping("/match/{matchId}/overview")
    @Operation(summary = "Obtenir le récapitulatif complet des passagers pour un déplacement de match")
    public ResponseEntity<MatchTravelOverviewDto> getMatchTravelOverview(@PathVariable final Long matchId) {
        return ResponseEntity.ok(this.travelService.getMatchTravelOverview(matchId));
    }

    @GetMapping("/count")
    @Operation(summary = "Compter le nombre de déplacements à l'extérieur pour un membre et une saison")
    public ResponseEntity<Long> countMemberAwayTravels(
            @RequestParam final String seasonId,
            @RequestParam final Long personId) {
        return ResponseEntity.ok(this.travelService.countMemberAwayTravels(seasonId, personId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une inscription de déplacement par son identifiant")
    public ResponseEntity<PersonTravelDto> getTravelById(@PathVariable final Long id) {
        return ResponseEntity.ok(this.travelService.getTravelById(id));
    }

    @PostMapping
    @Operation(summary = "Inscrire un passager à un déplacement")
    public ResponseEntity<PersonTravelDto> registerPersonTravel(@Valid @RequestBody final PersonTravelCreateDto dto) {
        final PersonTravelDto created = this.travelService.registerPersonTravel(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer une inscription de déplacement")
    public void removePersonTravel(@PathVariable final Long id) {
        this.travelService.removePersonTravel(id);
    }
}
