package nl.tudelft.sem.template.example.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import commons.Job;
import commons.NetId;
import commons.Status;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class JobServiceTest {

    @Autowired
    private transient JobRepository jobRepository;

    @Autowired
    private transient JobService jobService;

    @BeforeEach
    void setUp() {
        Job job1 = new Job(new NetId("mlica"), 10, 10, 10);
        jobRepository.save(job1);
        Job job3 = new Job(new NetId("mlica"), 20, 10, 1);
        jobRepository.save(job3);
        Job job2 = new Job(new NetId("ppolitowicz"), 1, 2, 3);
        jobRepository.save(job2);
    }

    @AfterEach
    void after() {
        jobRepository.deleteAll();
    }

    @Test
    void createJob() {
        NetId netId = new NetId("test");
        String resourceType = "CPU";
        int cpuUsage = 1;
        int gpuUsage = 2;
        int memoryUsage = 3;
        try {
            Job created = jobService.createJob(netId, netId, cpuUsage, gpuUsage, memoryUsage, "employee");
            jobRepository.save(created);
            Optional<Job> jobOptional = jobRepository.findById(created.getJobId());
            assertFalse(jobOptional.isEmpty());
            assertEquals(jobOptional.get(), created);
        } catch (Exception e) {
            fail();
        }

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
            jobService.deleteJob(j.getJobId());
            assertFalse(jobRepository.existsById(j.getJobId()));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void collectJobsByNetId() {
        NetId netId = new NetId("mlica");
        Job expected1 = new Job(new NetId("mlica"), 10, 10, 10);
        Job expected2 = new Job(new NetId("mlica"), 20, 10, 1);
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
            assertEquals(status.toString(), "pending");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void getAllJobs() {
        NetId netId = new NetId("administrator");
        try {
            List<Job> jobs = jobService.getAllJobs(netId, netId, "admin");
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