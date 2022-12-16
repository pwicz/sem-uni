package nl.tudelft.sem.template.example.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import commons.Job;
import commons.NetId;
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
    void setUp() throws Exception{
        Job job1 = new Job(new NetId("mlica"), "CPU", 10, 10, 10);
        jobRepository.save(job1);
        Job job2 = new Job(new NetId("ppolitowicz"), "GPU", 1, 2, 3);
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
            Job created = jobService.createJob(netId, netId, resourceType, cpuUsage, gpuUsage, memoryUsage, "employee");
            jobRepository.save(created);
            Optional<Job> jobOptional = jobRepository.findById(created.getJobId());
            assertFalse(jobOptional.isEmpty());
            assertEquals(jobOptional.get(), created);
        } catch (Exception e) {
            System.out.println("exception");
        }

    }

    @Test
    void deleteJob() {
    }

    @Test
    void collectJobsByNetId() {
    }

    @Test
    void getJobStatus() {
    }

    @Test
    void getAllJobs() {
    }

    @Test
    void updateJob() {
    }
}