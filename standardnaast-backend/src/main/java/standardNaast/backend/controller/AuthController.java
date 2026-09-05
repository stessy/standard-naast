package standardNaast.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import standardNaast.backend.domain.UserRole;
import standardNaast.backend.dto.*;
import standardNaast.backend.service.AuthService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints d'authentification, gestion des utilisateurs et des rôles")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Authentification d'un utilisateur", description = "Vérifie les identifiants et retourne les jetons JWT (access et refresh)")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    @Operation(summary = "Enregistrement d'un nouvel utilisateur", description = "Crée un nouveau compte utilisateur")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequestDto registerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(registerRequest));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rafraîchissement du token JWT", description = "Génère un nouveau couple de tokens à partir d'un refresh token valide")
    public ResponseEntity<AuthResponseDto> refreshToken(@Valid @RequestBody RefreshTokenRequestDto refreshRequest) {
        return ResponseEntity.ok(authService.refreshToken(refreshRequest));
    }

    @GetMapping("/me")
    @Operation(summary = "Profil de l'utilisateur connecté", description = "Retourne les informations du compte utilisateur actuellement authentifié",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserDto> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    @PostMapping("/change-password")
    @Operation(summary = "Changement de mot de passe", description = "Met à jour le mot de passe de l'utilisateur connecté",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequestDto request) {
        authService.changePassword(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Liste de tous les utilisateurs (ADMIN)", description = "Retourne la liste des utilisateurs de l'application",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Détail d'un utilisateur (ADMIN)", description = "Retourne un utilisateur par son identifiant",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getUserById(id));
    }

    @PutMapping("/users/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mise à jour des rôles d'un utilisateur (ADMIN)", description = "Affecte un nouvel ensemble de rôles à un utilisateur",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserDto> updateUserRoles(@PathVariable Long id, @RequestBody Set<UserRole> roles) {
        return ResponseEntity.ok(authService.updateUserRoles(id, roles));
    }
}
