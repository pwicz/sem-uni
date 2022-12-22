package nl.tudelft.sem.template.example.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import commons.Faculty;
import commons.Job;
import commons.NetId;
import commons.ScheduleJob;
import commons.Status;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AutomaticApproveJobComponentTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private transient JobRepository jobRepository;
    @MockBean
    private transient JobService mockJobService;

    @Autowired
    private transient AutomaticApproveJobsComponent aajc;

    List<Job> pendingJobs;
    Job job1;
    Job job2;
    Job job3;
    Job job4;
    Job job5;

    @BeforeEach
    void setUp() {
        job1 = new Job(new NetId("mlica"), new Faculty("EEMCS"), 10, 10, 10, LocalDate.now().plusDays(1));
        job1.setStatus(Status.PENDING);
        job2 = new Job(new NetId("ppolitowicz"), new Faculty("EEMCS"), 1, 2, 3, LocalDate.now());
        job2.setStatus(Status.PENDING);
        job3 = new Job(new NetId("mlica"), new Faculty("EEMCS"), 20, 10, 1, LocalDate.now().plusDays(1));
        job3.setStatus(Status.PENDING);
        job4 = new Job(new NetId("mlica"), new Faculty("EEMCS"), 20, 10, 1, LocalDate.now().plusDays(2));
        job4.setStatus(Status.PENDING);
        job5 = new Job(new NetId("mlica"), new Faculty("EEMCS"), 20, 10, 1, LocalDate.now().plusDays(1));
        job5.setStatus(Status.PENDING);

        pendingJobs = new ArrayList<>();
        pendingJobs.add(job5); // due tomorrow
        pendingJobs.add(job2);
        pendingJobs.add(job1); // due tomorrow
        pendingJobs.add(job3); // due tomorrow
        pendingJobs.add(job4);

        jobRepository.save(job5);
        jobRepository.save(job2);
        jobRepository.save(job1);
        jobRepository.save(job3);
        jobRepository.save(job4);
    }

    @Test
    void filterAndSortPendingJobs() {
        List<Job> filterSortedPendingJobs = aajc.filterAndSortPendingJobs(pendingJobs);
        assertThat(filterSortedPendingJobs.size()).isEqualTo(3);
        assertThat(filterSortedPendingJobs.get(0)).isEqualTo(job5);
        assertThat(filterSortedPendingJobs.get(1)).isEqualTo(job1);
        assertThat(filterSortedPendingJobs.get(2)).isEqualTo(job3);
    }

    @Test
    public void approveJobsAfter6pmTest() throws InvalidScheduleJobException {
        //JobService mockJobService = Mockito.mock(JobService.class);

        List<Job> pendingJobs = new ArrayList<>();

        Job job1 = new Job(new NetId("mlica"), new Faculty("EEMCS"), 10, 10, 10, LocalDate.now().plusDays(1));
        job1.setStatus(Status.PENDING);
        pendingJobs.add(job1);
        Job job2 = new Job(new NetId("ppolitowicz"), new Faculty("EEMCS"), 1, 2, 3, LocalDate.now());
        job2.setStatus(Status.PENDING);
        pendingJobs.add(job2);
        Job job3 = new Job(new NetId("mlica"), new Faculty("EEMCS"), 20, 10, 1, LocalDate.now().plusDays(1));
        job3.setStatus(Status.PENDING);
        pendingJobs.add(job3);
        Job job4 = new Job(new NetId("mlica"), new Faculty("EEMCS"), 20, 10, 1, LocalDate.now().plusDays(2));
        job4.setStatus(Status.PENDING);
        pendingJobs.add(job4);
        Job job5 = new Job(new NetId("mlica"), new Faculty("EEMCS"), 20, 10, 1, LocalDate.now().plusDays(1));
        job5.setStatus(Status.PENDING);
        pendingJobs.add(job5);

        // Configure the mock jobService to return a predefined list of pending jobs
        //List<Job> pendingJobs = Arrays.asList(job1, job2, job3, job4, job5);

        jobRepository.save(job1);
        jobRepository.save(job2);
        jobRepository.save(job3);
        jobRepository.save(job4);
        jobRepository.save(job5);

        Mockito.when(mockJobService.getAllPendingJobs()).thenReturn(pendingJobs);

        // Inject the mock jobService into the approveJobsAfter6pm method
        aajc.setJobService(mockJobService);

        // Invoke the approveJobsAfter6pm method
        aajc.approveJobsAfter6pm();

        // Verify that the mock jobService's scheduleJob method was called three times
        Mockito.verify(mockJobService, Mockito.times(3)).scheduleJob(Mockito.any());
    }
}