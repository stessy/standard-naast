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
import standardNaast.backend.domain.AccountingType;
import standardNaast.backend.dto.AccountingCreateUpdateDto;
import standardNaast.backend.dto.AccountingDto;
import standardNaast.backend.dto.AccountingSummaryDto;
import standardNaast.backend.service.AccountingService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/accountings")
@RequiredArgsConstructor
@Tag(name = "Accounting", description = "API de gestion du journal comptable et des finances")
public class AccountingController {

    private final AccountingService accountingService;

    @GetMapping
    @Operation(summary = "Rechercher et lister les écritures comptables avec pagination et filtres")
    public ResponseEntity<Page<AccountingDto>> searchAccountings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate endDate,
            @RequestParam(required = false) final AccountingType type,
            @RequestParam(required = false) final String description,
            @PageableDefault(size = 20) final Pageable pageable) {
        return ResponseEntity.ok(this.accountingService.searchAccountings(startDate, endDate, type, description, pageable));
    }

    @GetMapping("/month")
    @Operation(summary = "Lister les écritures comptables pour un mois et une année donnés")
    public ResponseEntity<List<AccountingDto>> getAccountingsByMonthAndYear(
            @RequestParam final int month,
            @RequestParam final int year) {
        return ResponseEntity.ok(this.accountingService.getAccountingsByMonthAndYear(month, year));
    }

    @GetMapping("/year/{year}")
    @Operation(summary = "Lister les écritures comptables pour une année complète")
    public ResponseEntity<List<AccountingDto>> getAccountingsByYear(@PathVariable final int year) {
        return ResponseEntity.ok(this.accountingService.getAccountingsByYear(year));
    }

    @GetMapping("/season/{seasonId}")
    @Operation(summary = "Lister les écritures comptables pour une saison")
    public ResponseEntity<List<AccountingDto>> getAccountingsBySeason(@PathVariable final String seasonId) {
        return ResponseEntity.ok(this.accountingService.getAccountingsBySeason(seasonId));
    }

    @GetMapping("/summary")
    @Operation(summary = "Obtenir le récapitulatif comptable (total entrées, total sorties, balance) sur une période")
    public ResponseEntity<AccountingSummaryDto> getAccountingSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate endDate) {
        return ResponseEntity.ok(this.accountingService.getAccountingSummary(startDate, endDate));
    }

    @GetMapping("/summary/season/{seasonId}")
    @Operation(summary = "Obtenir le récapitulatif comptable pour une saison")
    public ResponseEntity<AccountingSummaryDto> getAccountingSummaryBySeason(@PathVariable final String seasonId) {
        return ResponseEntity.ok(this.accountingService.getAccountingSummaryBySeason(seasonId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une écriture comptable par son identifiant")
    public ResponseEntity<AccountingDto> getAccountingById(@PathVariable final Long id) {
        return ResponseEntity.ok(this.accountingService.getAccountingById(id));
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle écriture comptable")
    public ResponseEntity<AccountingDto> createAccounting(@Valid @RequestBody final AccountingCreateUpdateDto dto) {
        final AccountingDto created = this.accountingService.createAccounting(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une écriture comptable")
    public ResponseEntity<AccountingDto> updateAccounting(
            @PathVariable final Long id,
            @Valid @RequestBody final AccountingCreateUpdateDto dto) {
        return ResponseEntity.ok(this.accountingService.updateAccounting(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer une écriture comptable")
    public void deleteAccounting(@PathVariable final Long id) {
        this.accountingService.deleteAccounting(id);
    }
}
