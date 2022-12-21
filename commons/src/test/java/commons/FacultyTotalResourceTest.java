package commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class FacultyTotalResourceTest {

    FacultyTotalResource fr;

    @BeforeEach
    void setUp() {
        LocalDate today = LocalDate.now();
        fr = new FacultyTotalResource("EEMCS", today, 3, 2, 1, 10, 10 ,10);
    }

    @Test
    void constructorTest() {
        LocalDate tmrw = LocalDate.now().plusDays(1);
        fr = new FacultyTotalResource("EE", tmrw, 10, 2, 1, 10, 10 ,10);

        assertNotNull(fr);

        assertThat(fr.getFaculty()).isEqualTo("EE");
        assertThat(fr.getDate()).isEqualTo(tmrw);
        assertThat(fr.getCpuUsage()).isEqualTo(10);
        assertThat(fr.getGpuUsage()).isEqualTo(2);
        assertThat(fr.getMemoryUsage()).isEqualTo(1);
        assertThat(fr.getMemoryUsageTotal()).isEqualTo(10);
        assertThat(fr.getMemoryUsageTotal()).isEqualTo(10);
        assertThat(fr.getMemoryUsageTotal()).isEqualTo(10);
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

    @Test
    void getCpuUsageT() {
        assertThat(fr.getCpuUsageTotal()).isEqualTo(10);
    }

    @Test
    void getGpuUsageT() {
        assertThat(fr.getGpuUsageTotal()).isEqualTo(10);
    }

    @Test
    void getMemoryUsageT() {
        assertThat(fr.getMemoryUsageTotal()).isEqualTo(10);
    }
}