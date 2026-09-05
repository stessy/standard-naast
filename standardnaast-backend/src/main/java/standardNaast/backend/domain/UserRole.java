package standardNaast.backend.domain;

public enum UserRole {
    ROLE_ADMIN,
    ROLE_TRESORIER,
    ROLE_MEMBRE_BUREAU,
    ROLE_MEMBER;

    public String getAuthority() {
        return name();
    }
}
