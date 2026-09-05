package standardNaast.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import standardNaast.backend.domain.UserRole;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private UserPrincipal samplePrincipal;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("testJwtSecretKeyForApplicationMustBeAtLeast256BitsLongAndSecure!");
        jwtProperties.setExpiration(3600000L); // 1 hour
        jwtProperties.setRefreshExpiration(86400000L); // 24 hours

        this.jwtTokenProvider = new JwtTokenProvider(jwtProperties);

        this.samplePrincipal = UserPrincipal.builder()
                .id(1L)
                .username("admin")
                .email("admin@standard-naast.be")
                .password("encoded_pass")
                .enabled(true)
                .authorities(List.of(new SimpleGrantedAuthority(UserRole.ROLE_ADMIN.name())))
                .build();
    }

    @Test
    void generateAccessToken_shouldProduceValidToken() {
        String token = jwtTokenProvider.generateAccessToken(samplePrincipal);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("admin", jwtTokenProvider.getUsernameFromToken(token));
    }

    @Test
    void generateRefreshToken_shouldProduceValidToken() {
        String refreshToken = jwtTokenProvider.generateRefreshToken(samplePrincipal);

        assertNotNull(refreshToken);
        assertTrue(jwtTokenProvider.validateToken(refreshToken));
        assertEquals("admin", jwtTokenProvider.getUsernameFromToken(refreshToken));
    }

    @Test
    void validateToken_withInvalidToken_shouldReturnFalse() {
        assertFalse(jwtTokenProvider.validateToken("invalid.jwt.token"));
    }
}
