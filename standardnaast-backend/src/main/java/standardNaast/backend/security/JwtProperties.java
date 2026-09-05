package standardNaast.backend.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtProperties {

    /**
     * Secret key for signing JWT tokens (HMAC-SHA256 requires >= 256 bits / 32 bytes).
     */
    private String secret = "standardNaastJwtSecretKeyForApplicationMustBeAtLeast256BitsLong!";

    /**
     * Access token expiration time in milliseconds (default: 24 hours = 86400000 ms).
     */
    private long expiration = 86400000L;

    /**
     * Refresh token expiration time in milliseconds (default: 7 days = 604800000 ms).
     */
    private long refreshExpiration = 604800000L;
}
