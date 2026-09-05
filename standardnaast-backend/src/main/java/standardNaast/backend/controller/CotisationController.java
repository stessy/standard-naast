package standardNaast.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import standardNaast.backend.dto.CotisationCreateUpdateDto;
import standardNaast.backend.dto.CotisationDto;
import standardNaast.backend.service.CotisationService;

import java.util.List;

@RestController
@RequestMapping("/api/cotisations")
@Tag(name = "Cotisations", description = "Cotisation fee rates management API")
public class CotisationController {

    private final CotisationService cotisationService;

    public CotisationController(CotisationService cotisationService) {
        this.cotisationService = cotisationService;
    }

    @GetMapping
    @Operation(summary = "Get all cotisation rates", description = "Retrieves all annual cotisation fee rates")
    @ApiResponse(responseCode = "200", description = "List of cotisation rates retrieved successfully")
    public ResponseEntity<List<CotisationDto>> getAllCotisations() {
        return ResponseEntity.ok(cotisationService.getAllCotisations());
    }

    @GetMapping("/{year}")
    @Operation(summary = "Get cotisation rate by year", description = "Retrieves a cotisation rate for a given year")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cotisation rate found"),
            @ApiResponse(responseCode = "404", description = "Cotisation rate not found")
    })
    public ResponseEntity<CotisationDto> getCotisationByYear(
            @Parameter(description = "Cotisation year/period", example = "2024")
            @PathVariable Long year) {
        return ResponseEntity.ok(cotisationService.getCotisationByYear(year));
    }

    @PostMapping
    @Operation(summary = "Create a new cotisation rate", description = "Creates a new annual cotisation fee rate")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cotisation rate created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or cotisation already exists")
    })
    public ResponseEntity<CotisationDto> createCotisation(
            @Valid @RequestBody CotisationCreateUpdateDto dto) {
        CotisationDto created = cotisationService.createCotisation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{year}")
    @Operation(summary = "Update a cotisation rate", description = "Updates an existing annual cotisation fee rate")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cotisation rate updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Cotisation rate not found")
    })
    public ResponseEntity<CotisationDto> updateCotisation(
            @Parameter(description = "Cotisation year/period", example = "2024")
            @PathVariable Long year,
            @Valid @RequestBody CotisationCreateUpdateDto dto) {
        return ResponseEntity.ok(cotisationService.updateCotisation(year, dto));
    }

    @DeleteMapping("/{year}")
    @Operation(summary = "Delete a cotisation rate", description = "Deletes an annual cotisation fee rate by year")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cotisation rate deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Cotisation rate not found")
    })
    public ResponseEntity<Void> deleteCotisation(
            @Parameter(description = "Cotisation year/period", example = "2024")
            @PathVariable Long year) {
        cotisationService.deleteCotisation(year);
        return ResponseEntity.noContent().build();
    }
}
