package standardNaast.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import standardNaast.backend.dto.AbonnementCreateUpdateDto;
import standardNaast.backend.dto.AbonnementDto;
import standardNaast.backend.dto.AbonnementStatusUpdateDto;
import standardNaast.backend.service.AbonnementService;

import java.util.List;

@RestController
@RequestMapping("/api/abonnements")
@Tag(name = "Abonnements", description = "Endpoints for managing member season tickets")
public class AbonnementController {

    private final AbonnementService abonnementService;

    public AbonnementController(AbonnementService abonnementService) {
        this.abonnementService = abonnementService;
    }

    @GetMapping
    @Operation(summary = "Get abonnements with pagination and optional season or member filter")
    public ResponseEntity<Page<AbonnementDto>> getAbonnements(
            @RequestParam(required = false) String seasonId,
            @RequestParam(required = false) Long memberId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(abonnementService.getAbonnements(seasonId, memberId, pageable));
    }

    @GetMapping("/season/{seasonId}")
    @Operation(summary = "Get all abonnements for a specific season")
    public ResponseEntity<List<AbonnementDto>> getAbonnementsBySeason(@PathVariable String seasonId) {
        return ResponseEntity.ok(abonnementService.getAbonnementsBySeason(seasonId));
    }

    @GetMapping("/member/{memberId}")
    @Operation(summary = "Get all abonnements for a specific member")
    public ResponseEntity<List<AbonnementDto>> getAbonnementsByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(abonnementService.getAbonnementsByMember(memberId));
    }

    @GetMapping("/purchasable")
    @Operation(summary = "Get purchasable abonnements for a season")
    public ResponseEntity<List<AbonnementDto>> getPurchasableAbonnements(@RequestParam String seasonId) {
        return ResponseEntity.ok(abonnementService.getPurchasableAbonnements(seasonId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get abonnement by ID")
    public ResponseEntity<AbonnementDto> getAbonnementById(@PathVariable Long id) {
        return ResponseEntity.ok(abonnementService.getAbonnementById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new abonnement")
    public ResponseEntity<AbonnementDto> createAbonnement(@Valid @RequestBody AbonnementCreateUpdateDto dto) {
        AbonnementDto created = abonnementService.createAbonnement(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing abonnement")
    public ResponseEntity<AbonnementDto> updateAbonnement(
            @PathVariable Long id,
            @Valid @RequestBody AbonnementCreateUpdateDto dto
    ) {
        return ResponseEntity.ok(abonnementService.updateAbonnement(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an abonnement")
    public ResponseEntity<Void> deleteAbonnement(@PathVariable Long id) {
        abonnementService.deleteAbonnement(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/status")
    @Operation(summary = "Bulk update status for a list of abonnements")
    public ResponseEntity<Void> updateStatus(@Valid @RequestBody AbonnementStatusUpdateDto dto) {
        abonnementService.updateStatus(dto.abonnementIds(), dto.status());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/payment")
    @Operation(summary = "Bulk update payment status for a list of abonnements")
    public ResponseEntity<Void> updatePaymentStatus(
            @RequestParam boolean paid,
            @RequestBody List<Long> abonnementIds
    ) {
        abonnementService.updatePaymentStatus(abonnementIds, paid);
        return ResponseEntity.ok().build();
    }
}
