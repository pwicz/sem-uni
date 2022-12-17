package commons;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class FacultyResourceTest {

    FacultyResource fr;

    @BeforeEach
    void setUp() {
        LocalDate today = LocalDate.now();
        fr = new FacultyResource("EEMCS", today, 3, 2, 1);
    }

    @Test
    void getFaculty() {
        assertThat(fr.getFaculty()).isEqualTo("EEMCS");
    }

    @Test
    void getDate() {
        assertThat(fr.getDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void getCpuUsage() {
        assertThat(fr.getCpuUsage()).isEqualTo(3);
    }

    @Test
    void getGpuUsage() {
        assertThat(fr.getGpuUsage()).isEqualTo(2);
    }

    @Test
    void getMemoryUsage() {
        assertThat(fr.getMemoryUsage()).isEqualTo(1);
    }
}