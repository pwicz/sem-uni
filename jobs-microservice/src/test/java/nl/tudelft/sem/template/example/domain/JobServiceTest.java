package nl.tudelft.sem.template.example.domain;

import commons.Job;
import commons.NetId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

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