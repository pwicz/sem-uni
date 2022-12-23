package nl.tudelft.sem.template.example.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JobNotificationResponseModelTest {
    JobNotificationResponseModel model;

    @BeforeEach
    public void init() {
        model = new JobNotificationResponseModel(1L, "status", LocalDate.now());
    }

    @Test
    public void constructorTest() {
        model = new JobNotificationResponseModel(1L, "status", LocalDate.now());

        assertNotNull(model);
        assertThat(model.getJobId()).isEqualTo(1L);
        assertThat(model.getStatus()).isEqualTo("status");
        assertThat(model.getScheduleDate()).isEqualTo(LocalDate.now());
    }

    @Test
    public void getJobIdTest() {
        assertThat(model.getJobId()).isEqualTo(1L);
    }

    @Test
    public void getStatusTest() {
        assertThat(model.getStatus()).isEqualTo("status");
    }

    @Test
    public void getScheduleDateTest() {
        assertThat(model.getScheduleDate()).isEqualTo(LocalDate.now());
    }
}
