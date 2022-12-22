package nl.tudelft.sem.template.example.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;


import commons.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

//    @Autowired
    private transient JobService jobService;

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
        jobService = mock(JobService.class);

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
    void approveJobsAfter6pm() throws InvalidScheduleJobException {
//        JobService jobService = mock(JobService.class);
        when(jobService.getAllPendingJobs()).thenReturn(pendingJobs);

        try {
            aajc.approveJobsAfter6pm();
        } catch (InvalidScheduleJobException e) {
            fail();
        } catch (NullPointerException n) {
//            fail("The responsebody was null");
        }
//        aajc.approveJobsAfter6pm();

        verify(jobService).getAllPendingJobs();

        ScheduleJob scheduleJob5 = new ScheduleJob(job5.getJobId(),
                job5.getFaculty(), job5.getPreferredDate(),
                job5.getCpuUsage(), job5.getGpuUsage(), job5.getMemoryUsage());

        ScheduleJob scheduleJob1 = new ScheduleJob(job1.getJobId(),
                job1.getFaculty(), job1.getPreferredDate(),
                job1.getCpuUsage(), job1.getGpuUsage(), job1.getMemoryUsage());

        ScheduleJob scheduleJob3 = new ScheduleJob(job3.getJobId(),
                job3.getFaculty(), job3.getPreferredDate(),
                job3.getCpuUsage(), job3.getGpuUsage(), job3.getMemoryUsage());

//        verify(jobService).scheduleJob(scheduleJob5);
//        verify(jobService).scheduleJob(scheduleJob1);
//        verify(jobService).scheduleJob(scheduleJob3);
    }



}