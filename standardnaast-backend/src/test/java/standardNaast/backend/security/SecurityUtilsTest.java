package standardNaast.backend.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import standardNaast.backend.domain.UserRole;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilsTest {

    @BeforeEach
    @AfterEach
    void cleanSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUsername_whenAuthenticated_shouldReturnUsername() {
        UserPrincipal principal = UserPrincipal.builder()
                .id(10L)
                .username("john_smith")
                .email("john@example.com")
                .password("pwd")
                .enabled(true)
                .authorities(List.of(new SimpleGrantedAuthority(UserRole.ROLE_MEMBER.name())))
                .build();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Optional<String> username = SecurityUtils.getCurrentUsername();
        assertTrue(username.isPresent());
        assertEquals("john_smith", username.get());
    }

    @Test
    void getCurrentUserId_whenAuthenticated_shouldReturnUserId() {
        UserPrincipal principal = UserPrincipal.builder()
                .id(42L)
                .username("john_smith")
                .email("john@example.com")
                .password("pwd")
                .enabled(true)
                .authorities(List.of(new SimpleGrantedAuthority(UserRole.ROLE_MEMBER.name())))
                .build();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Optional<Long> userId = SecurityUtils.getCurrentUserId();
        assertTrue(userId.isPresent());
        assertEquals(42L, userId.get());
    }

    @Test
    void getCurrentUsername_whenNotAuthenticated_shouldReturnEmpty() {
        Optional<String> username = SecurityUtils.getCurrentUsername();
        assertTrue(username.isEmpty());
    }

    @Test
    void getCurrentUserId_whenNotAuthenticated_shouldReturnEmpty() {
        Optional<Long> userId = SecurityUtils.getCurrentUserId();
        assertTrue(userId.isEmpty());
    }
}
