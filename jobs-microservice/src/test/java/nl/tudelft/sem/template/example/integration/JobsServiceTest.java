package nl.tudelft.sem.template.example.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import commons.Job;
import commons.NetId;
import commons.Status;
import exceptions.InvalidIdException;
import java.time.LocalDate;
import java.util.List;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.example.domain.JobRepository;
import nl.tudelft.sem.template.example.domain.JobService;
import nl.tudelft.sem.template.example.models.JobRequestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class JobsServiceTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Autowired
    private transient JobService jobService;

    String facultyConstant;
    LocalDate dateConstant;

    NetId u1;
    NetId u2;
    Job j1;
    Job j2;
    @Autowired
    private JobRepository jobRepository;

    /**
     * Set variables before each test and clear database.
     */
    @BeforeEach
    public void before() throws Exception {
        jobRepository.deleteAll();
        jobRepository.flush();
        facultyConstant = "EEMCS";
        dateConstant = LocalDate.now().plusDays(1);

        u1 = new NetId("User");
        u2 = new NetId("User2");
        j1 = new Job(u1, "memory", 10, 10, 10);
        j2 = new Job(u2, "cpu", 12, 10, 10);

    }

    @Test
    public void getAllScheduledJobsTest() throws Exception {
        String url = jobService.getUrl() + "/addJob";

        Mockito.when(restTemplate.getForEntity(url, Job.class))
                .thenReturn(new ResponseEntity<>(j1, HttpStatus.OK));

        Job j3 = new Job(u2, "memory", 10, 10, 10);

        j1.setStatus(Status.ACCEPTED);
        j3.setStatus(Status.ACCEPTED);
        jobService.createJob(u1, j1, "employee");
        jobService.createJob(u2, j3, "employee");

        jobService.createJob(u2, j2, "employee");

        List<Job> fromDb = jobService.getAllScheduledJobs(u1, u1, "admin");
        assertThat(fromDb.size()).isEqualTo(2);
        assertTrue(fromDb.get(0).getStatus() == Status.ACCEPTED);
        assertTrue(fromDb.get(1).getStatus() == Status.ACCEPTED);
    }

    @Test
    public void updateJobTest_Exception() throws Exception {
        String url = jobService.getUrl() + "/updateJob";

        Mockito.when(restTemplate.getForEntity(url, Job.class))
                .thenReturn(new ResponseEntity<>(j1, HttpStatus.OK));


        assertThrows(InvalidIdException.class, () -> {
            jobService.updateJob(1, Status.ACCEPTED, dateConstant);
        });
    }

    @Test
    public void updateJobTest_Ok() throws Exception {
        String url = jobService.getUrl() + "/addJob";

        JobRequestModel model = new JobRequestModel(u1.toString(), "memory", 10, 10, 10);

        Mockito.when(restTemplate.getForEntity(url, Job.class))
                .thenReturn(new ResponseEntity<>(j1, HttpStatus.OK));


        jobService.createJob(u1, u1, "memory", 10, 10, 10, "employee");

        List<Job> fromDb = jobService.getAllJobs(u1, u1, "admin");
        assertThat(fromDb.size()).isEqualTo(1);
        assertTrue(fromDb.get(0).getStatus() == Status.PENDING);

        jobService.updateJob(fromDb.get(0).getJobId(), Status.ACCEPTED, dateConstant);
        fromDb = jobService.getAllJobs(u1, u1, "admin");
        assertThat(fromDb.size()).isEqualTo(1);
        assertTrue(fromDb.get(0).getStatus() == Status.ACCEPTED);
    }

    @Test
    public void deleteJobTest() throws Exception {
        String url = jobService.getUrl() + "/addJob";

        Mockito.when(restTemplate.getForEntity(url, Job.class))
                .thenReturn(new ResponseEntity<>(j1, HttpStatus.OK));


        jobService.createJob(u1, u1, "memory", 10, 10, 10, "employee");
        jobService.createJob(u2, u2, "cpu", 12, 10, 10, "employee");

        List<Job> fromDb = jobService.getAllJobs(u1, u1, "admin");
        assertThat(fromDb.size()).isEqualTo(2);

        jobService.deleteJob(fromDb.get(0).getJobId());

        Job lastOneStanding = fromDb.get(1);
        fromDb = jobService.getAllJobs(u1, u1, "admin");
        assertThat(fromDb.size()).isEqualTo(1);
        assertThat(fromDb.get(0).equals(lastOneStanding)).isTrue();
    }

    @Test
    public void deleteJobTest_Exception() throws Exception {
        String url = jobService.getUrl() + "/addJob";

        Mockito.when(restTemplate.getForEntity(url, Job.class))
                .thenReturn(new ResponseEntity<>(j1, HttpStatus.OK));


        jobService.createJob(u1, u1, "memory", 10, 10, 10, "employee");

        List<Job> fromDb = jobService.getAllJobs(u1, u1, "admin");
        assertThat(fromDb.size()).isEqualTo(1);

        assertThrows(InvalidIdException.class, () -> {
            jobService.deleteJob(fromDb.get(0).getJobId() + 1);
        });
    }

    @Test
    public void collectJobsByNetIdTest() throws Exception {
        String url = jobService.getUrl() + "/addJob";

        Mockito.when(restTemplate.getForEntity(url, Job.class))
                .thenReturn(new ResponseEntity<>(j1, HttpStatus.OK));


        jobService.createJob(u1, u1, "memory", 10, 10, 10, "employee");
        jobService.createJob(u2, u2, "cpu", 12, 10, 10, "employee");



        List<Job> fromDb = jobService.collectJobsByNetId(u1, u1);
        j1.setJobId(fromDb.get(0).getJobId());

        assertThat(fromDb.size()).isEqualTo(1);
        assertThat(fromDb.get(0).equals(j1)).isTrue();
    }

}