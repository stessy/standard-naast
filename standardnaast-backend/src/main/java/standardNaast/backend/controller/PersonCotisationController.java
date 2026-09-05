package standardNaast.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import standardNaast.backend.dto.CotisationsSeasonOverviewDto;
import standardNaast.backend.dto.MemberCardSentBulkUpdateDto;
import standardNaast.backend.dto.PersonCotisationCreateDto;
import standardNaast.backend.dto.PersonCotisationDto;
import standardNaast.backend.service.PersonCotisationService;

import java.util.List;

@RestController
@RequestMapping("/api/member-cotisations")
@Tag(name = "Member Cotisations", description = "Member cotisations lifecycle and payments management API")
public class PersonCotisationController {

    private final PersonCotisationService personCotisationService;

    public PersonCotisationController(PersonCotisationService personCotisationService) {
        this.personCotisationService = personCotisationService;
    }

    @PostMapping
    @Operation(summary = "Register member cotisation", description = "Registers a new cotisation payment for a member")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Member cotisation registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or cotisation already exists for this season"),
            @ApiResponse(responseCode = "404", description = "Member or Season not found")
    })
    public ResponseEntity<PersonCotisationDto> registerCotisation(
            @Valid @RequestBody PersonCotisationCreateDto dto) {
        PersonCotisationDto created = personCotisationService.registerMemberCotisation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get cotisation by ID", description = "Retrieves a single member cotisation record by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cotisation found"),
            @ApiResponse(responseCode = "404", description = "Cotisation not found")
    })
    public ResponseEntity<PersonCotisationDto> getCotisationById(
            @Parameter(description = "Cotisation ID", example = "1001")
            @PathVariable Long id) {
        return ResponseEntity.ok(personCotisationService.getCotisationById(id));
    }

    @GetMapping
    @Operation(summary = "Search member cotisations", description = "Searches member cotisations with optional filters and pagination")
    @ApiResponse(responseCode = "200", description = "Page of cotisations retrieved successfully")
    public ResponseEntity<Page<PersonCotisationDto>> searchCotisations(
            @Parameter(description = "Season ID filter", example = "2024-2025")
            @RequestParam(required = false) String seasonId,
            @Parameter(description = "Member ID filter", example = "123")
            @RequestParam(required = false) Long memberId,
            @Parameter(description = "Member card sent status filter")
            @RequestParam(required = false) Boolean cardSent,
            @ParameterObject @PageableDefault(size = 20, sort = "datePaiement", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(personCotisationService.searchCotisations(seasonId, memberId, cardSent, pageable));
    }

    @GetMapping("/member/{memberId}")
    @Operation(summary = "Get cotisations by member", description = "Retrieves all cotisation records for a given member")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of cotisations retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    public ResponseEntity<List<PersonCotisationDto>> getCotisationsByMember(
            @Parameter(description = "Member ID", example = "123")
            @PathVariable Long memberId) {
        return ResponseEntity.ok(personCotisationService.getCotisationsByMember(memberId));
    }

    @GetMapping("/season/{seasonId}")
    @Operation(summary = "Get cotisations by season", description = "Retrieves all cotisation records for a given season")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of cotisations retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Season not found")
    })
    public ResponseEntity<List<PersonCotisationDto>> getCotisationsBySeason(
            @Parameter(description = "Season ID", example = "2024-2025")
            @PathVariable String seasonId) {
        return ResponseEntity.ok(personCotisationService.getCotisationsBySeason(seasonId));
    }

    @GetMapping("/overview/{seasonId}")
    @Operation(summary = "Get season cotisations overview", description = "Retrieves full categorized breakdown of cotisations for a season (paid with card sent, paid without card sent, unpaid)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Season overview retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Season not found")
    })
    public ResponseEntity<CotisationsSeasonOverviewDto> getSeasonOverview(
            @Parameter(description = "Season ID", example = "2024-2025")
            @PathVariable String seasonId) {
        return ResponseEntity.ok(personCotisationService.getSeasonOverview(seasonId));
    }

    @PostMapping("/card-sent/bulk")
    @Operation(summary = "Bulk update member card sent status", description = "Updates the membership card sent status for multiple cotisations")
    @ApiResponse(responseCode = "204", description = "Member card statuses updated successfully")
    public ResponseEntity<Void> bulkUpdateMemberCardSent(
            @Valid @RequestBody MemberCardSentBulkUpdateDto dto) {
        personCotisationService.bulkUpdateMemberCardSent(dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete member cotisation", description = "Deletes a member cotisation record by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cotisation deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Cotisation not found")
    })
    public ResponseEntity<Void> deleteCotisation(
            @Parameter(description = "Cotisation ID", example = "1001")
            @PathVariable Long id) {
        personCotisationService.deleteMemberCotisation(id);
        return ResponseEntity.noContent().build();
    }
}
