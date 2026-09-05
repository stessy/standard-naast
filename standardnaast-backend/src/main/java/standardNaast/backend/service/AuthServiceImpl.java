package standardNaast.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import standardNaast.backend.domain.AppUser;
import standardNaast.backend.domain.Person;
import standardNaast.backend.domain.UserRole;
import standardNaast.backend.dto.*;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.mapper.AppUserMapper;
import standardNaast.backend.repository.AppUserRepository;
import standardNaast.backend.repository.PersonRepository;
import standardNaast.backend.security.CustomUserDetailsService;
import standardNaast.backend.security.JwtTokenProvider;
import standardNaast.backend.security.SecurityUtils;
import standardNaast.backend.security.UserPrincipal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;
    private final PersonRepository personRepository;
    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public AuthResponseDto login(LoginRequestDto loginRequest) {
        log.info("Tentative de connexion pour l'utilisateur: {}", loginRequest.username());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                )
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String accessToken = jwtTokenProvider.generateAccessToken(userPrincipal);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userPrincipal);

        AppUser user = appUserRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé: " + userPrincipal.getId()));

        return AuthResponseDto.of(accessToken, refreshToken, jwtTokenProvider.getExpiration(), appUserMapper.toDto(user));
    }

    @Override
    public UserDto register(RegisterRequestDto registerRequest) {
        log.info("Enregistrement d'un nouvel utilisateur: {}", registerRequest.username());

        if (appUserRepository.existsByUsername(registerRequest.username())) {
            throw new IllegalArgumentException("Le nom d'utilisateur '" + registerRequest.username() + "' est déjà utilisé");
        }

        if (registerRequest.email() != null && appUserRepository.existsByEmail(registerRequest.email())) {
            throw new IllegalArgumentException("L'adresse email '" + registerRequest.email() + "' est déjà utilisée");
        }

        AppUser user = appUserMapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.password()));

        Set<UserRole> roles = registerRequest.roles();
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            roles.add(UserRole.ROLE_MEMBER);
        }
        user.setRoles(roles);

        if (registerRequest.personId() != null) {
            Person person = personRepository.findById(registerRequest.personId())
                    .orElseThrow(() -> new ResourceNotFoundException("Personne non trouvée avec l'id: " + registerRequest.personId()));
            user.setPerson(person);
        }

        AppUser savedUser = appUserRepository.save(user);
        return appUserMapper.toDto(savedUser);
    }

    @Override
    public AuthResponseDto refreshToken(RefreshTokenRequestDto refreshRequest) {
        String refreshToken = refreshRequest.refreshToken();
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("Refresh token invalide ou expiré");
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        UserPrincipal userPrincipal = (UserPrincipal) userDetails;

        String newAccessToken = jwtTokenProvider.generateAccessToken(userPrincipal);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userPrincipal);

        AppUser user = appUserRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé: " + userPrincipal.getId()));

        return AuthResponseDto.of(newAccessToken, newRefreshToken, jwtTokenProvider.getExpiration(), appUserMapper.toDto(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getCurrentUser() {
        String username = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new BadCredentialsException("Aucun utilisateur authentifié dans la session courante"));

        AppUser user = appUserRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé: " + username));

        return appUserMapper.toDto(user);
    }

    @Override
    public void changePassword(ChangePasswordRequestDto request) {
        String username = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new BadCredentialsException("Aucun utilisateur authentifié dans la session courante"));

        AppUser user = appUserRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé: " + username));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadCredentialsException("L'ancien mot de passe est incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        appUserRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        return appUserRepository.findById(id)
                .map(appUserMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return appUserMapper.toDtoList(appUserRepository.findAll());
    }

    @Override
    public UserDto updateUserRoles(Long userId, Set<UserRole> roles) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + userId));

        user.setRoles(roles);
        AppUser saved = appUserRepository.save(user);
        return appUserMapper.toDto(saved);
    }
}
