package standardNaast.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import standardNaast.backend.dto.BenevolatCreateUpdateDto;
import standardNaast.backend.dto.BenevolatDto;
import standardNaast.backend.dto.MemberBenevolatSummaryDto;
import standardNaast.backend.service.BenevolatService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/benevolats")
@RequiredArgsConstructor
@Tag(name = "Benevolats", description = "API de gestion des prestations de bénévolat")
public class BenevolatController {

    private final BenevolatService benevolatService;

    @GetMapping
    @Operation(summary = "Rechercher et lister les prestations de bénévolat avec pagination et filtres")
    public ResponseEntity<Page<BenevolatDto>> searchBenevolats(
            @RequestParam(required = false) final Long personId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate endDate,
            @PageableDefault(size = 20) final Pageable pageable) {
        return ResponseEntity.ok(this.benevolatService.searchBenevolats(personId, startDate, endDate, pageable));
    }

    @GetMapping("/person/{personId}")
    @Operation(summary = "Lister les prestations de bénévolat pour une personne")
    public ResponseEntity<List<BenevolatDto>> getBenevolatsByPerson(@PathVariable final Long personId) {
        return ResponseEntity.ok(this.benevolatService.getBenevolatsByPerson(personId));
    }

    @GetMapping("/person/{personId}/season/{seasonId}")
    @Operation(summary = "Lister les prestations de bénévolat d'une personne pour une saison")
    public ResponseEntity<List<BenevolatDto>> getBenevolatsByPersonAndSeason(
            @PathVariable final Long personId,
            @PathVariable final String seasonId) {
        return ResponseEntity.ok(this.benevolatService.getBenevolatsByPersonAndSeason(personId, seasonId));
    }

    @GetMapping("/person/{personId}/summary")
    @Operation(summary = "Obtenir le récapitulatif du bénévolat d'un membre (global ou par saison)")
    public ResponseEntity<MemberBenevolatSummaryDto> getMemberBenevolatSummary(
            @PathVariable final Long personId,
            @RequestParam(required = false) final String seasonId) {
        return ResponseEntity.ok(this.benevolatService.getMemberBenevolatSummary(personId, seasonId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une prestation de bénévolat par son identifiant")
    public ResponseEntity<BenevolatDto> getBenevolatById(@PathVariable final Long id) {
        return ResponseEntity.ok(this.benevolatService.getBenevolatById(id));
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle prestation de bénévolat")
    public ResponseEntity<BenevolatDto> createBenevolat(@Valid @RequestBody final BenevolatCreateUpdateDto dto) {
        final BenevolatDto created = this.benevolatService.createBenevolat(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une prestation de bénévolat")
    public ResponseEntity<BenevolatDto> updateBenevolat(
            @PathVariable final Long id,
            @Valid @RequestBody final BenevolatCreateUpdateDto dto) {
        return ResponseEntity.ok(this.benevolatService.updateBenevolat(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer une prestation de bénévolat")
    public void deleteBenevolat(@PathVariable final Long id) {
        this.benevolatService.deleteBenevolat(id);
    }
}
