package commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RoleTest {
    Role role;

    @BeforeEach
    void init() {
        role = new Role(RoleValue.EMPLOYEE);
    }

    @Test
    public void constructorTest() {

        role = new Role(RoleValue.EMPLOYEE);
        assertNotNull(role);
        assertThat(role.getAuthority()).isEqualTo(RoleValue.EMPLOYEE.toString());
    }

    @Test
    public void constructorTestString() {

        role = new Role("EMPLOYEE");
        assertNotNull(role);
        assertThat(role.getAuthority()).isEqualTo(RoleValue.EMPLOYEE.toString());
    }

    @Test
    public void getRoleValueTest() {
        role = new Role(RoleValue.EMPLOYEE);
        assertNotNull(role.getRoleValue());
    }

    @Test
    public void isAdminTest() {
        role = new Role(RoleValue.ADMIN);
        assertTrue(role.isAdmin());
    }

    @Test
    public void isAdminTestNegate() {
        role = new Role(RoleValue.EMPLOYEE);
        assertFalse(role.isAdmin());
    }




    @Test
    public void getAuthorityTest() {
        assertThat(role.getAuthority()).isEqualTo(RoleValue.EMPLOYEE.toString());
    }
}
