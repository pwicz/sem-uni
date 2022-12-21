package nl.tudelft.sem.template.example.models;



import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JobRequestModelTest {
    JobRequestModel jm;

    @BeforeEach
    void init() {
        jm = new JobRequestModel("filip", 10, 4, 3);
    }

    @Test
    void test() {
        assertThat(jm.getNetId()).isEqualTo("filip");
    }

    @Test
    void testCpu() {
        assertThat(jm.getCpuUsage()).isEqualTo(10);
    }

    @Test
    void testGpu() {
        assertThat(jm.getGpuUsage()).isEqualTo(4);
    }

    @Test
    void testMem() {
        assertThat(jm.getMemoryUsage()).isEqualTo(3);
    }
}
