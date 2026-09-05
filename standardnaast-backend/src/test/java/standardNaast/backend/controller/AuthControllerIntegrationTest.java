package standardNaast.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import standardNaast.backend.domain.UserRole;
import standardNaast.backend.dto.*;
import standardNaast.backend.exception.GlobalExceptionHandler;
import standardNaast.backend.service.AuthService;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerIntegrationTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private UserDto sampleUserDto;
    private AuthResponseDto sampleAuthResponse;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();

        this.mockMvc = MockMvcBuilders.standaloneSetup(this.authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        this.sampleUserDto = new UserDto(
                1L,
                "admin",
                "admin@standard-naast.be",
                "Admin",
                "User",
                true,
                Set.of(UserRole.ROLE_ADMIN),
                null,
                null,
                null
        );

        this.sampleAuthResponse = AuthResponseDto.of(
                "mocked_access_jwt_token",
                "mocked_refresh_jwt_token",
                86400000L,
                sampleUserDto
        );
    }

    @Test
    void login_shouldReturnAuthResponse() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto("admin", "password");
        when(authService.login(any(LoginRequestDto.class))).thenReturn(sampleAuthResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", is("mocked_access_jwt_token")))
                .andExpect(jsonPath("$.tokenType", is("Bearer")))
                .andExpect(jsonPath("$.user.username", is("admin")));
    }

    @Test
    void register_shouldReturnCreatedUser() throws Exception {
        RegisterRequestDto registerRequest = new RegisterRequestDto(
                "new_member",
                "member@standard-naast.be",
                "secret123",
                "New",
                "Member",
                Set.of(UserRole.ROLE_MEMBER),
                null
        );

        when(authService.register(any(RegisterRequestDto.class))).thenReturn(sampleUserDto);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("admin")));
    }

    @Test
    void getCurrentUser_shouldReturnUserDto() throws Exception {
        when(authService.getCurrentUser()).thenReturn(sampleUserDto);

        mockMvc.perform(get("/api/auth/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("admin")))
                .andExpect(jsonPath("$.email", is("admin@standard-naast.be")));
    }

    @Test
    void getAllUsers_shouldReturnUserList() throws Exception {
        when(authService.getAllUsers()).thenReturn(List.of(sampleUserDto));

        mockMvc.perform(get("/api/auth/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username", is("admin")));
    }

    @Test
    void refreshToken_shouldReturnNewAuthResponse() throws Exception {
        RefreshTokenRequestDto refreshRequest = new RefreshTokenRequestDto("valid_refresh_token");
        when(authService.refreshToken(any(RefreshTokenRequestDto.class))).thenReturn(sampleAuthResponse);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", is("mocked_access_jwt_token")));
    }

    @Test
    void getUserById_shouldReturnUserDto() throws Exception {
        when(authService.getUserById(1L)).thenReturn(sampleUserDto);

        mockMvc.perform(get("/api/auth/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("admin")));
    }

    @Test
    void changePassword_shouldReturnNoContent() throws Exception {
        ChangePasswordRequestDto request = new ChangePasswordRequestDto("oldPassword123", "newPassword123");
        doNothing().when(authService).changePassword(any(ChangePasswordRequestDto.class));

        mockMvc.perform(post("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }
}
