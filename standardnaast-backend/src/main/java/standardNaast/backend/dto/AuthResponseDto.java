package standardNaast.backend.dto;

public record AuthResponseDto(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UserDto user
) {
    public static AuthResponseDto of(String accessToken, String refreshToken, long expiresIn, UserDto user) {
        return new AuthResponseDto(accessToken, refreshToken, "Bearer", expiresIn, user);
    }
}
