package commons;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class JobTest {

    NetId netId1;
    Job job1;

    @BeforeEach
    void setUp() {
        netId1 = new NetId("ageist");
        job1 = new Job(netId1, 3, 2, 1);
    }

    @Test
    void getJobId() {
        assertThat(job1.getJobId()).isEqualTo(0L);
    }

    @Test
    void setJobId() {
        job1.setJobId(2L);
        assertThat(job1.getJobId()).isEqualTo(2L);
    }

    @Test
    void getNetId() {
        assertThat(job1.getNetId()).isEqualTo(netId1);
    }

    @Test
    void setNetId() {
        NetId newNetId = new NetId("piotr");
        job1.setNetId(newNetId);
        assertThat(job1.getNetId()).isEqualTo(newNetId);
    }

    @Test
    void getCpuUsage() {
        assertThat(job1.getCpuUsage()).isEqualTo(3);
    }

    @Test
    void setCpuUsage() {
        job1.setCpuUsage(4);
        assertThat(job1.getCpuUsage()).isEqualTo(4);
    }

    @Test
    void getGpuUsage() {
        assertThat(job1.getGpuUsage()).isEqualTo(2);
    }

    @Test
    void setGpuUsage() {
        job1.setGpuUsage(1);
        assertThat(job1.getGpuUsage()).isEqualTo(1);
    }

    @Test
    void getMemoryUsage() {
        assertThat(job1.getMemoryUsage()).isEqualTo(1);
    }

    @Test
    void setMemoryUsage() {
        job1.setMemoryUsage(2);
        assertThat(job1.getMemoryUsage()).isEqualTo(2);
    }

    @Test
    void getScheduleDate() {
        assertThat(job1.getScheduleDate()).isNull();
    }

    @Test
    void setScheduleDate() {
        job1.setScheduleDate(LocalDate.now().plusDays(3));
        assertThat(job1.getScheduleDate()).isEqualTo(LocalDate.now().plusDays(3));
    }

}