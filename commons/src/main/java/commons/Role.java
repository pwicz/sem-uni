package commons;

import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;

/**
 * A DDD value object representing a NetID in our domain.
 */
@EqualsAndHashCode
public class Role implements GrantedAuthority {
    private static final long serialVersionUID = 4L;      //Default serial version uid

    private final transient RoleType role;

    public Role(RoleType role) {
        // validate NetID
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return role.toString();
    }

    public RoleType getRole() {
        return role;
    }
}