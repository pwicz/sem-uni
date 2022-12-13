package nl.tudelft.sem.template.example.domain.processing;

import static org.assertj.core.api.Assertions.assertThat;

import commons.FacultyResource;
import commons.ScheduleJob;
import java.time.LocalDate;
import java.util.List;
import nl.tudelft.sem.template.example.domain.db.ScheduledInstance;
import nl.tudelft.sem.template.example.domain.db.ScheduledInstanceRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProcessingJobsServiceTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private transient ProcessingJobsService processingJobsService;

    @Autowired
    private transient ScheduledInstanceRepository scheduledInstanceRepository;

    @Test
    public void scheduleJob_forNextDay_worksCorrectly() {
        String facultyConstant = "EEMCS";
        LocalDate dateConstant = LocalDate.now().plusDays(1);

        ScheduleJob scheduleJob = new ScheduleJob(1, facultyConstant, dateConstant.plusDays(1),
                5, 2, 2);

        FacultyResource[] s = {new FacultyResource(facultyConstant, dateConstant, 10, 10, 10)};

        String url = processingJobsService.getResourcesUrl() + "/facultyResources?faculty="
                + facultyConstant + "&day=" + dateConstant;

        Mockito.when(restTemplate.getForEntity(url, FacultyResource[].class))
                .thenReturn(new ResponseEntity<>(s, HttpStatus.OK));

        ScheduledInstance expected = new ScheduledInstance(1L, facultyConstant, 5, 2, 2, dateConstant);

        processingJobsService.scheduleJob(scheduleJob);
        List<ScheduledInstance> fromDb = scheduledInstanceRepository.findAllByJobId(1L);

        assertThat(fromDb.size()).isEqualTo(1);
        assertThat(compareScheduledInstances(fromDb.get(0), expected)).isEqualTo(true);
    }

    private boolean compareScheduledInstances(ScheduledInstance a, ScheduledInstance b) {
        return a.getJobId().equals(b.getJobId())
                && a.getFaculty().equals(b.getFaculty())
                && a.getCpuUsage() == b.getCpuUsage()
                && a.getGpuUsage() == b.getGpuUsage()
                && a.getMemoryUsage() == b.getMemoryUsage()
                && a.getDate().equals(b.getDate());
    }

}
