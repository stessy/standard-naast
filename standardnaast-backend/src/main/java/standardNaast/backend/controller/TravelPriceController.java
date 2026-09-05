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
import standardNaast.backend.domain.Place;
import standardNaast.backend.dto.TravelPriceCreateUpdateDto;
import standardNaast.backend.dto.TravelPriceDto;
import standardNaast.backend.service.TravelPriceService;

import java.util.List;

@RestController
@RequestMapping("/api/travel-prices")
@RequiredArgsConstructor
@Tag(name = "Travel Prices", description = "API de gestion de la grille tarifaire des déplacements")
public class TravelPriceController {

    private final TravelPriceService travelPriceService;

    @GetMapping
    @Operation(summary = "Lister et rechercher les tarifs de déplacement avec pagination et filtres")
    public ResponseEntity<Page<TravelPriceDto>> getAllTravelPrices(
            @RequestParam(required = false) final String seasonId,
            @RequestParam(required = false) final Place place,
            @RequestParam(required = false) final Boolean membre,
            @PageableDefault(size = 20) final Pageable pageable) {
        return ResponseEntity.ok(this.travelPriceService.getAllTravelPrices(seasonId, place, membre, pageable));
    }

    @GetMapping("/season/{seasonId}")
    @Operation(summary = "Lister tous les tarifs de déplacement pour une saison")
    public ResponseEntity<List<TravelPriceDto>> getTravelPricesBySeason(@PathVariable final String seasonId) {
        return ResponseEntity.ok(this.travelPriceService.getTravelPricesBySeason(seasonId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un tarif de déplacement par son identifiant")
    public ResponseEntity<TravelPriceDto> getTravelPriceById(@PathVariable final Long id) {
        return ResponseEntity.ok(this.travelPriceService.getTravelPriceById(id));
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau tarif de déplacement")
    public ResponseEntity<TravelPriceDto> createTravelPrice(@Valid @RequestBody final TravelPriceCreateUpdateDto dto) {
        final TravelPriceDto created = this.travelPriceService.createTravelPrice(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un tarif de déplacement")
    public ResponseEntity<TravelPriceDto> updateTravelPrice(
            @PathVariable final Long id,
            @Valid @RequestBody final TravelPriceCreateUpdateDto dto) {
        return ResponseEntity.ok(this.travelPriceService.updateTravelPrice(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un tarif de déplacement")
    public void deleteTravelPrice(@PathVariable final Long id) {
        this.travelPriceService.deleteTravelPrice(id);
    }
}
