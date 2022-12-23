package nl.tudelft.sem.template.example.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import commons.Job;
import commons.NetId;
import commons.RoleValue;
import commons.ScheduleJob;
import commons.Status;
import exceptions.ResourceBiggerThanCpuException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class JobServiceTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private transient JobRepository jobRepository;

    @Autowired
    private transient JobService jobService;

    @BeforeEach
    void setUp() {
        Job job1 = new Job(new NetId("mlica"), "d", 10, 10, 10, LocalDate.now());
        jobRepository.save(job1);
        Job job3 = new Job(new NetId("mlica"), "d", 20, 10, 1, LocalDate.now());
        jobRepository.save(job3);
        Job job2 = new Job(new NetId("ppolitowicz"), "d", 1, 2, 3, LocalDate.now());
        jobRepository.save(job2);
    }

    @Test
    void scheduleJobSuccess() throws InvalidScheduleJobException {
        //Job job1 = new Job(new NetId("ageist"), 10, 10, 10);
        ScheduleJob job = new ScheduleJob(1L, "EEMCS", LocalDate.now(), 10, 10, 10);
        Mockito.when(restTemplate.postForEntity("http://localhost:8084/schedule", job, String.class))
                .thenReturn(new ResponseEntity<String>("processing", HttpStatus.OK));
        String responseText = jobService.scheduleJob(job);
        assertThat(responseText).isEqualTo("processing");
    }

    @Test
    void scheduleJobProblem() throws InvalidScheduleJobException {
        ScheduleJob job = new ScheduleJob(1L, "EEMCS", LocalDate.now(), 10, 10, 10);
        Mockito.when(restTemplate.postForEntity("http://localhost:8084/schedule", job, String.class))
                .thenReturn(new ResponseEntity<String>((String) null, HttpStatus.OK));
        String responseText = jobService.scheduleJob(job);
        assertThat(responseText).isEqualTo("Problem: ResponseEntity was null!");
    }

    @Test
    void scheduleJobException() {
        Assertions.assertThrows(InvalidScheduleJobException.class, () -> {
            jobService.scheduleJob(null);
        });
    }

    @Test
    void createJob() {
        NetId netId = new NetId("test");
        int cpuUsage = 3;
        int gpuUsage = 2;
        int memoryUsage = 3;
        try {
            Job created = jobService.createJob(netId, netId, "d", cpuUsage,
                    gpuUsage, memoryUsage, RoleValue.EMPLOYEE, LocalDate.now());
            jobRepository.save(created);
            Optional<Job> jobOptional = jobRepository.findById(created.getJobId());
            assertFalse(jobOptional.isEmpty());
            assertEquals(jobOptional.get(), created);
        } catch (Exception e) {
            fail();
        }
    }


    @Test
    void createJob_Exception() {
        NetId netId = new NetId("test");
        int cpuUsage = 1;
        int gpuUsage = 2;
        int memoryUsage = 3;
        assertThrows(ResourceBiggerThanCpuException.class, () -> {
            jobService.createJob(netId, netId, "d", cpuUsage, gpuUsage, memoryUsage, RoleValue.EMPLOYEE, LocalDate.now());
        });
    }

    @Test
    void deleteJob() {
        NetId netId = new NetId("mlica");
        Optional<List<Job>> query = jobRepository.findAllByNetId(netId);
        Job j = null;
        if (query.isPresent()) {
            j = query.get().get(0);
        }
        try {
            assert j != null;
            jobService.deleteJob(netId.toString(), RoleValue.ADMIN, j.getJobId());
            assertFalse(jobRepository.existsById(j.getJobId()));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void collectJobsByNetId() {
        NetId netId = new NetId("mlica");
        Job expected1 = new Job(new NetId("mlica"), "d", 10, 10, 10, LocalDate.now());
        Job expected2 = new Job(new NetId("mlica"), "d", 20, 10, 1, LocalDate.now());
        try {
            List<Job> jobs = jobService.collectJobsByNetId(netId, netId);
            expected1.setJobId(jobs.get(0).getJobId());
            expected2.setJobId(jobs.get(1).getJobId());
            assertEquals(jobs.size(), 2);
            assertEquals(jobs.get(0), expected1);
            assertEquals(jobs.get(1), expected2);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void getJobStatus() {
        NetId netId = new NetId("mlica");
        Optional<List<Job>> query = jobRepository.findAllByNetId(netId);
        Job j = null;
        if (query.isPresent()) {
            j = query.get().get(0);
        }
        try {
            assert j != null;
            Status status = jobService.getJobStatus(netId, netId, j.getJobId());
            assertEquals(status, Status.PENDING);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void getAllJobs() {
        NetId netId = new NetId("administrator");
        try {
            List<Job> jobs = jobService.getAllJobs(netId, netId, RoleValue.ADMIN);
            assertEquals(jobs.size(), 3);
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    void updateJob() {
        NetId netId = new NetId("mlica");
        Optional<List<Job>> query = jobRepository.findAllByNetId(netId);
        Job j = null;
        if (query.isPresent()) {
            j = query.get().get(0);
        }
        try {
            assert j != null;
            Status status = Status.FINISHED;
            jobService.updateJob(j.getJobId(), status, LocalDate.now());
            Optional<Job> updatedJob = jobRepository.findById(j.getJobId());
            assertFalse(updatedJob.isEmpty());
            assertEquals(updatedJob.get().getStatus(), status);
        } catch (Exception e) {
            fail();
        }
    }
}