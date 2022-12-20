package nl.tudelft.sem.template.authentication.domain.user;

import commons.RoleType;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;

/**
 * A DDD value object representing a NetID in our domain.
 */
@EqualsAndHashCode
public class Role implements GrantedAuthority {
    private final transient RoleType roleValue;
    private static final long serialVersionUID = 4L;

    public Role(RoleType role) {
        // validate NetID
        this.roleValue = role;
    }

    public Role(String role) {
        if (role.equals("Admin")) {
            this.roleValue = RoleType.Admin;
            return;
        }
        if (role.equals("Employee")) {
            this.roleValue = RoleType.Employee;
            return;
        }
        if (role.equals("Faculty")) {
            this.roleValue = RoleType.Faculty;
            return;
        }
        this.roleValue = null;
    }

    @Override
    public String getAuthority() {
        return roleValue.toString();
    }
}