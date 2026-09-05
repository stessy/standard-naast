package standardNaast.backend.dto;

import standardNaast.backend.domain.UserRole;

import java.time.LocalDateTime;
import java.util.Set;

public record UserDto(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        boolean enabled,
        Set<UserRole> roles,
        Long personId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
