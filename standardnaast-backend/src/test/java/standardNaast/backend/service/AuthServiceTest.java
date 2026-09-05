package standardNaast.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import standardNaast.backend.domain.AppUser;
import standardNaast.backend.domain.UserRole;
import standardNaast.backend.dto.*;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.mapper.AppUserMapper;
import standardNaast.backend.repository.AppUserRepository;
import standardNaast.backend.repository.PersonRepository;
import standardNaast.backend.security.CustomUserDetailsService;
import standardNaast.backend.security.JwtTokenProvider;
import standardNaast.backend.security.UserPrincipal;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private AppUserMapper appUserMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private AuthServiceImpl authService;

    private AppUser sampleUser;
    private UserDto sampleUserDto;
    private UserPrincipal samplePrincipal;

    @BeforeEach
    void setUp() {
        this.sampleUser = AppUser.builder()
                .id(1L)
                .username("john_doe")
                .email("john@example.com")
                .password("encoded_secret")
                .roles(Set.of(UserRole.ROLE_MEMBER))
                .enabled(true)
                .build();

        this.sampleUserDto = new UserDto(
                1L,
                "john_doe",
                "john@example.com",
                "John",
                "Doe",
                true,
                Set.of(UserRole.ROLE_MEMBER),
                null,
                null,
                null
        );

        this.samplePrincipal = UserPrincipal.builder()
                .id(1L)
                .username("john_doe")
                .email("john@example.com")
                .password("encoded_secret")
                .enabled(true)
                .authorities(List.of(new SimpleGrantedAuthority(UserRole.ROLE_MEMBER.name())))
                .build();
    }

    @Test
    void login_whenValidCredentials_shouldReturnAuthResponse() {
        LoginRequestDto loginRequest = new LoginRequestDto("john_doe", "password123");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(samplePrincipal);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenProvider.generateAccessToken(samplePrincipal)).thenReturn("mocked_access_token");
        when(jwtTokenProvider.generateRefreshToken(samplePrincipal)).thenReturn("mocked_refresh_token");
        when(jwtTokenProvider.getExpiration()).thenReturn(86400000L);
        when(appUserRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(appUserMapper.toDto(sampleUser)).thenReturn(sampleUserDto);

        AuthResponseDto response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mocked_access_token", response.accessToken());
        assertEquals("mocked_refresh_token", response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals("john_doe", response.user().username());
    }

    @Test
    void register_whenUsernameNotTaken_shouldSaveAndReturnUserDto() {
        RegisterRequestDto registerRequest = new RegisterRequestDto(
                "new_user",
                "new@example.com",
                "secret123",
                "New",
                "User",
                Set.of(UserRole.ROLE_MEMBER),
                null
        );

        when(appUserRepository.existsByUsername("new_user")).thenReturn(false);
        when(appUserRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(appUserMapper.toEntity(registerRequest)).thenReturn(sampleUser);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded_secret");
        when(appUserRepository.save(any(AppUser.class))).thenReturn(sampleUser);
        when(appUserMapper.toDto(sampleUser)).thenReturn(sampleUserDto);

        UserDto result = authService.register(registerRequest);

        assertNotNull(result);
        assertEquals("john_doe", result.username());
        verify(appUserRepository).save(any(AppUser.class));
    }

    @Test
    void register_whenUsernameAlreadyExists_shouldThrowIllegalArgumentException() {
        RegisterRequestDto registerRequest = new RegisterRequestDto(
                "existing_user",
                "test@example.com",
                "secret123",
                null,
                null,
                null,
                null
        );

        when(appUserRepository.existsByUsername("existing_user")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest));
    }

    @Test
    void getUserById_whenUserExists_shouldReturnUserDto() {
        when(appUserRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(appUserMapper.toDto(sampleUser)).thenReturn(sampleUserDto);

        UserDto result = authService.getUserById(1L);

        assertNotNull(result);
        assertEquals("john_doe", result.username());
    }

    @Test
    void getUserById_whenUserNotFound_shouldThrowResourceNotFoundException() {
        when(appUserRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.getUserById(999L));
    }

    @Test
    void register_whenEmailAlreadyExists_shouldThrowIllegalArgumentException() {
        RegisterRequestDto registerRequest = new RegisterRequestDto(
                "new_user",
                "existing@example.com",
                "secret123",
                null,
                null,
                null,
                null
        );

        when(appUserRepository.existsByUsername("new_user")).thenReturn(false);
        when(appUserRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest));
    }

    @Test
    void refreshToken_whenValidToken_shouldReturnNewAuthResponse() {
        RefreshTokenRequestDto refreshRequest = new RefreshTokenRequestDto("valid_refresh_token");

        when(jwtTokenProvider.validateToken("valid_refresh_token")).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken("valid_refresh_token")).thenReturn("john_doe");
        when(customUserDetailsService.loadUserByUsername("john_doe")).thenReturn(samplePrincipal);
        when(jwtTokenProvider.generateAccessToken(samplePrincipal)).thenReturn("new_access_token");
        when(jwtTokenProvider.generateRefreshToken(samplePrincipal)).thenReturn("new_refresh_token");
        when(jwtTokenProvider.getExpiration()).thenReturn(86400000L);
        when(appUserRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(appUserMapper.toDto(sampleUser)).thenReturn(sampleUserDto);

        AuthResponseDto response = authService.refreshToken(refreshRequest);

        assertNotNull(response);
        assertEquals("new_access_token", response.accessToken());
        assertEquals("new_refresh_token", response.refreshToken());
    }

    @Test
    void refreshToken_whenInvalidToken_shouldThrowBadCredentialsException() {
        RefreshTokenRequestDto refreshRequest = new RefreshTokenRequestDto("invalid_refresh_token");
        when(jwtTokenProvider.validateToken("invalid_refresh_token")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.refreshToken(refreshRequest));
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        when(appUserRepository.findAll()).thenReturn(List.of(sampleUser));
        when(appUserMapper.toDtoList(List.of(sampleUser))).thenReturn(List.of(sampleUserDto));

        List<UserDto> users = authService.getAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("john_doe", users.get(0).username());
    }

    @Test
    void updateUserRoles_shouldUpdateAndReturnUserDto() {
        Set<UserRole> newRoles = Set.of(UserRole.ROLE_ADMIN, UserRole.ROLE_MEMBER);
        when(appUserRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(appUserRepository.save(sampleUser)).thenReturn(sampleUser);
        when(appUserMapper.toDto(sampleUser)).thenReturn(sampleUserDto);

        UserDto result = authService.updateUserRoles(1L, newRoles);

        assertNotNull(result);
        verify(appUserRepository).save(sampleUser);
    }
}
