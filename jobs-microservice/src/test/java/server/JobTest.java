package server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JobTest {

    Job job;

    @BeforeEach
    void setUp() {
        job = new Job("ageist", "CPU", 2, 0, 1);
    }


    @Test
    void getId() {
    }

    @Test
    void getJobId() {
    }

    @Test
    void getNetId() {
    }

    @Test
    void setNetId() {
    }

    @Test
    void getResourceType() {
    }

    @Test
    void setResourceType() {
    }

    @Test
    void getCPUusage() {
    }

    @Test
    void setCPUusage() {
    }

    @Test
    void getGPUusage() {
    }

    @Test
    void setGPUusage() {
    }

    @Test
    void getMemoryUsage() {
    }

    @Test
    void setMemoryUsage() {
    }
}