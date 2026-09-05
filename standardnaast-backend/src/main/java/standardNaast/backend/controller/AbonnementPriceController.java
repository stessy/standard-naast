package standardNaast.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import standardNaast.backend.domain.CompetitionType;
import standardNaast.backend.dto.AbonnementPriceCreateUpdateDto;
import standardNaast.backend.dto.AbonnementPriceDto;
import standardNaast.backend.service.AbonnementPriceService;

import java.util.List;

@RestController
@RequestMapping("/api/abonnement-prices")
@Tag(name = "Abonnement Prices", description = "Endpoints for managing season ticket price matrix")
public class AbonnementPriceController {

    private final AbonnementPriceService abonnementPriceService;

    public AbonnementPriceController(AbonnementPriceService abonnementPriceService) {
        this.abonnementPriceService = abonnementPriceService;
    }

    @GetMapping
    @Operation(summary = "Get all prices for a season")
    public ResponseEntity<List<AbonnementPriceDto>> getPricesBySeason(@RequestParam String seasonId) {
        return ResponseEntity.ok(abonnementPriceService.getPricesBySeason(seasonId));
    }

    @GetMapping("/competition")
    @Operation(summary = "Get prices for a season and competition type")
    public ResponseEntity<List<AbonnementPriceDto>> getPricesBySeasonAndCompetition(
            @RequestParam String seasonId,
            @RequestParam CompetitionType competitionType
    ) {
        return ResponseEntity.ok(abonnementPriceService.getPricesBySeasonAndCompetition(seasonId, competitionType));
    }

    @GetMapping("/blocs")
    @Operation(summary = "Get distinct blocs for a season and competition type")
    public ResponseEntity<List<String>> getDistinctBlocs(
            @RequestParam String seasonId,
            @RequestParam CompetitionType competitionType
    ) {
        return ResponseEntity.ok(abonnementPriceService.getDistinctBlocs(seasonId, competitionType));
    }

    @GetMapping("/competition-types")
    @Operation(summary = "Get distinct competition types for a season")
    public ResponseEntity<List<CompetitionType>> getDistinctCompetitionTypes(@RequestParam String seasonId) {
        return ResponseEntity.ok(abonnementPriceService.getDistinctCompetitionTypes(seasonId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get price by ID")
    public ResponseEntity<AbonnementPriceDto> getPriceById(@PathVariable Long id) {
        return ResponseEntity.ok(abonnementPriceService.getPriceById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new abonnement price entry")
    public ResponseEntity<AbonnementPriceDto> createPrice(@Valid @RequestBody AbonnementPriceCreateUpdateDto dto) {
        AbonnementPriceDto created = abonnementPriceService.createPrice(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing abonnement price entry")
    public ResponseEntity<AbonnementPriceDto> updatePrice(
            @PathVariable Long id,
            @Valid @RequestBody AbonnementPriceCreateUpdateDto dto
    ) {
        return ResponseEntity.ok(abonnementPriceService.updatePrice(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an abonnement price entry")
    public ResponseEntity<Void> deletePrice(@PathVariable Long id) {
        abonnementPriceService.deletePrice(id);
        return ResponseEntity.noContent().build();
    }
}
