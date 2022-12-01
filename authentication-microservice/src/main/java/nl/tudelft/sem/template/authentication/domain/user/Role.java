package nl.tudelft.sem.template.authentication.domain.user;

import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing a NetID in our domain.
 */
@EqualsAndHashCode
public class Role {
    private final transient String roleValue;

    public Role(String role) {
        // validate NetID
        this.roleValue = role;
    }

    @Override
    public String toString() {
        return roleValue;
    }
}