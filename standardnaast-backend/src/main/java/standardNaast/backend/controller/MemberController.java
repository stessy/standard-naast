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
import standardNaast.backend.dto.MemberCreateUpdateDto;
import standardNaast.backend.dto.MemberDto;
import standardNaast.backend.service.MemberService;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "API de gestion des membres")
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    @Operation(summary = "Lister et rechercher les membres avec pagination")
    public ResponseEntity<Page<MemberDto>> getMembers(
            @RequestParam(required = false) final String search,
            @PageableDefault(size = 20, sort = "memberNumber", direction = Sort.Direction.ASC) final Pageable pageable) {
        return ResponseEntity.ok(this.memberService.getMembers(search, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'un membre par son ID")
    public ResponseEntity<MemberDto> getMemberById(@PathVariable final Long id) {
        return ResponseEntity.ok(this.memberService.getMemberById(id));
    }

    @GetMapping("/by-number/{memberNumber}")
    @Operation(summary = "Obtenir un membre par son numéro de membre")
    public ResponseEntity<MemberDto> getMemberByMemberNumber(@PathVariable final Long memberNumber) {
        return this.memberService.getMemberByMemberNumber(memberNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau membre")
    public ResponseEntity<MemberDto> createMember(@Valid @RequestBody final MemberCreateUpdateDto dto) {
        final MemberDto created = this.memberService.createMember(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour les informations d'un membre")
    public ResponseEntity<MemberDto> updateMember(
            @PathVariable final Long id,
            @Valid @RequestBody final MemberCreateUpdateDto dto) {
        return ResponseEntity.ok(this.memberService.updateMember(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un membre")
    public void deleteMember(@PathVariable final Long id) {
        this.memberService.deleteMember(id);
    }

    @GetMapping("/next-number")
    @Operation(summary = "Obtenir le prochain numéro de membre disponible")
    public ResponseEntity<Long> getNextMemberNumber() {
        return ResponseEntity.ok(this.memberService.getNextMemberNumber());
    }
}
