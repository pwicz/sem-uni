package commons;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RoleTest {
    Role role;

    @BeforeEach
    void init() {
        role = new Role(RoleValue.EMPLOYEE);
    }

    @Test
    public void getAuthorityTest() {
        assertThat(role.getAuthority()).isEqualTo(RoleValue.EMPLOYEE.toString());
    }
}
