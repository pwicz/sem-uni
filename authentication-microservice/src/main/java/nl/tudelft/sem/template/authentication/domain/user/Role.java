package nl.tudelft.sem.template.authentication.domain.user;

import commons.RoleValue;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;

/**
 * A DDD value object representing a NetID in our domain.
 */
@EqualsAndHashCode
public class Role implements GrantedAuthority {
    private transient RoleValue roleValue;
    private static final long serialVersionUID = 4L;

    public Role(RoleValue role) {
        // validate NetID
        this.roleValue = role;
    }

    /**
     * Constructor Role from String to Enum.
     *
     * @param role role
     */
    public Role(String role) {
        if (role.equals("ADMIN")) {
            this.roleValue = RoleValue.ADMIN;
            return;
        }
        if (role.equals("EMPLOYEE")) {
            this.roleValue = RoleValue.EMPLOYEE;
            return;
        }
        if (role.equals("FAC_ACC")) {
            this.roleValue = RoleValue.FAC_ACC;
        }

    }

    @Override
    public String getAuthority() {
        return roleValue.toString();
    }
}