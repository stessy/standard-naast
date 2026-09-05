package standardNaast.backend.service;

import standardNaast.backend.domain.UserRole;
import standardNaast.backend.dto.*;

import java.util.List;
import java.util.Set;

public interface AuthService {

    AuthResponseDto login(LoginRequestDto loginRequest);

    UserDto register(RegisterRequestDto registerRequest);

    AuthResponseDto refreshToken(RefreshTokenRequestDto refreshRequest);

    UserDto getCurrentUser();

    void changePassword(ChangePasswordRequestDto request);

    UserDto getUserById(Long id);

    List<UserDto> getAllUsers();

    UserDto updateUserRoles(Long userId, Set<UserRole> roles);
}
