package nl.tudelft.sem.template.example.models;

import static org.assertj.core.api.Assertions.assertThat;

import commons.Status;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class JobNotificationResponseModelTest {

    JobNotificationResponseModel jnrm;

    @BeforeEach
    void setUp() {
        jnrm = new JobNotificationResponseModel(1L, Status.PENDING, LocalDate.now().plusDays(2));
    }

    @Test
    void getJobId() {
        assertThat(jnrm.getJobId()).isEqualTo(1L);
    }

    @Test
    void getStatus() {
        assertThat(jnrm.getStatus()).isEqualTo(Status.PENDING);
    }

    @Test
    void getScheduleDate() {
        assertThat(jnrm.getScheduleDate()).isEqualTo(LocalDate.now().plusDays(2));
    }

    @Test
    void setJobId() {
        jnrm.setJobId(2L);
        assertThat(jnrm.getJobId()).isEqualTo(2L);
    }

    @Test
    void setStatus() {
        jnrm.setStatus(Status.ACCEPTED);
        assertThat(jnrm.getStatus()).isEqualTo(Status.ACCEPTED);
    }

    @Test
    void setScheduleDate() {
        jnrm.setScheduleDate(LocalDate.now().plusDays(1));
        assertThat(jnrm.getScheduleDate()).isEqualTo(LocalDate.now().plusDays(1));
    }

    @Test
    void toStringAccepted() {
        JobNotificationResponseModel jnrm1 = new JobNotificationResponseModel(
                1L, Status.ACCEPTED, LocalDate.now().plusDays(2));
        String text = "Job with job id 1 is ACCEPTED. "
                + "The Job will be executed on the "
                + LocalDate.now().plusDays(2) + ".";
        assertThat(jnrm1.toString()).isEqualTo(text);
    }

    @Test
    void toStringFinished() {
        JobNotificationResponseModel jnrm2 = new JobNotificationResponseModel(
                1L, Status.FINISHED, LocalDate.now().plusDays(2));
        String text = "Job with job id 1 is FINISHED. "
                + "The Job was executed on the "
                + LocalDate.now().plusDays(2) + ".";
        assertThat(jnrm2.toString()).isEqualTo(text);
    }

    @Test
    void toStringPending() {
        JobNotificationResponseModel jnrm3 = new JobNotificationResponseModel(
                1L, Status.PENDING, LocalDate.now().plusDays(2));
        String text = "Job with job id 1 is PENDING. "
                + "Please check at another time, if the Job has been scheduled!";
        assertThat(jnrm3.toString()).isEqualTo(text);
    }

    @Test
    void toStringRunning() {
        JobNotificationResponseModel jnrm4 = new JobNotificationResponseModel(
                1L, Status.RUNNING, LocalDate.now().plusDays(2));
        String text = "Job with job id 1 is RUNNING. "
                + "The Job is executed today.";
        assertThat(jnrm4.toString()).isEqualTo(text);
    }

    @Test
    void toStringRejected() {
        JobNotificationResponseModel jnrm5 = new JobNotificationResponseModel(
                1L, Status.REJECTED, LocalDate.now().plusDays(2));
        String text = "Job with job id 1 is REJECTED. "
                + "The Job is rejected, since no resources were available.";
        assertThat(jnrm5.toString()).isEqualTo(text);
    }
}