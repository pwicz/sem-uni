package nl.tudelft.sem.template.example.domain.processing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import commons.FacultyResource;
import commons.ScheduleJob;
import commons.UpdateJob;
import commons.exceptions.ResourceBiggerThanCpuException;
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
    public void scheduleJob_forNextDay_worksCorrectly() throws ResourceBiggerThanCpuException {
        String facultyConstant = "EEMCS";
        LocalDate dateConstant = LocalDate.now().plusDays(1);

        ScheduleJob scheduleJob = new ScheduleJob(1, facultyConstant, dateConstant.plusDays(1),
                5, 2, 2);

        FacultyResource[] s = {new FacultyResource(facultyConstant, dateConstant, 10, 10, 10)};

        String url = processingJobsService.getResourcesUrl() + "/facultyResources?faculty="
                + facultyConstant + "&day=" + dateConstant;

        Mockito.when(restTemplate.getForEntity(url, FacultyResource[].class))
                .thenReturn(new ResponseEntity<>(s, HttpStatus.OK));


        processingJobsService.scheduleJob(scheduleJob);

        Mockito.verify(restTemplate).postForEntity(processingJobsService.getJobsUrl() + "/updateStatus",
                new UpdateJob(1, "scheduled", dateConstant), Void.class);

        List<ScheduledInstance> fromDb = scheduledInstanceRepository.findAllByJobId(1L);

        ScheduledInstance expected = new ScheduledInstance(1L, facultyConstant, 5, 2, 2, dateConstant);
        assertThat(fromDb.size()).isEqualTo(1);
        assertThat(compareScheduledInstances(fromDb.get(0), expected)).isEqualTo(true);
    }

    @Test
    public void scheduleJob_forOtherDay_worksCorrectly() throws ResourceBiggerThanCpuException {
        String facultyConstant = "EEMCS";
        LocalDate dateConstant = LocalDate.now().plusDays(1);

        ScheduledInstance toDb = new ScheduledInstance(4L, "EEMCS", 6, 5, 5, dateConstant);
        scheduledInstanceRepository.save(toDb);

        ScheduleJob scheduleJob = new ScheduleJob(1, facultyConstant, dateConstant.plusDays(5),
                5, 2, 2);

        FacultyResource[] dayOne = {new FacultyResource(facultyConstant, dateConstant, 10, 7, 7)};
        FacultyResource[] dayTwo = {new FacultyResource(facultyConstant, dateConstant.plusDays(1), 5, 2, 2)};

        String url = processingJobsService.getResourcesUrl() + "/facultyResources?faculty="
                + facultyConstant + "&day=";

        Mockito.when(restTemplate.getForEntity(url + dateConstant, FacultyResource[].class))
                .thenReturn(new ResponseEntity<>(dayOne, HttpStatus.OK));
        Mockito.when(restTemplate.getForEntity(url + dateConstant.plusDays(1), FacultyResource[].class))
                .thenReturn(new ResponseEntity<>(dayTwo, HttpStatus.OK));

        processingJobsService.scheduleJob(scheduleJob);

        Mockito.verify(restTemplate).postForEntity(processingJobsService.getJobsUrl() + "/updateStatus",
                new UpdateJob(1, "scheduled", dateConstant.plusDays(1)), Void.class);

        List<ScheduledInstance> fromDb = scheduledInstanceRepository.findAllByJobId(1L);

        ScheduledInstance expected = new ScheduledInstance(1L, facultyConstant, 5, 2, 2, dateConstant.plusDays(1));
        assertThat(fromDb.size()).isEqualTo(1);
        assertThat(compareScheduledInstances(fromDb.get(0), expected)).isEqualTo(true);
    }

    @Test
    public void scheduleJob_splitBetweenFaculties_worksCorrectly() throws ResourceBiggerThanCpuException {
        String facultyConstant = "EEMCS";
        String facultyConstant2 = "3ME";
        LocalDate dateConstant = LocalDate.now().plusDays(1);

        ScheduleJob scheduleJob = new ScheduleJob(1, facultyConstant, dateConstant.plusDays(5),
                5, 2, 2);

        FacultyResource[] dayOne = {new FacultyResource(facultyConstant, dateConstant, 2, 1, 0),
            new FacultyResource(facultyConstant2, dateConstant, 10, 10, 10)};

        String url = processingJobsService.getResourcesUrl() + "/facultyResources?faculty="
                + facultyConstant + "&day=";

        Mockito.when(restTemplate.getForEntity(url + dateConstant, FacultyResource[].class))
                .thenReturn(new ResponseEntity<>(dayOne, HttpStatus.OK));

        processingJobsService.scheduleJob(scheduleJob);

        Mockito.verify(restTemplate).postForEntity(processingJobsService.getJobsUrl() + "/updateStatus",
                new UpdateJob(1, "scheduled", dateConstant), Void.class);

        List<ScheduledInstance> fromDb = scheduledInstanceRepository.findAllByJobId(1L);

        ScheduledInstance expectedEemcs = new ScheduledInstance(1L, facultyConstant, 2, 1, 0, dateConstant);
        ScheduledInstance expected3me = new ScheduledInstance(1L, facultyConstant2, 3, 1, 2, dateConstant);
        assertThat(fromDb.size()).isEqualTo(2);
        for (var si : fromDb) {
            if (si.getFaculty().equals(facultyConstant)) {
                assertThat(compareScheduledInstances(si, expectedEemcs)).isEqualTo(true);
            } else if (si.getFaculty().equals(facultyConstant2)) {
                assertThat(compareScheduledInstances(si, expected3me)).isEqualTo(true);
            }
        }

    }

    @Test
    public void scheduleJob_notAbleToSchedule_worksCorrectly() throws ResourceBiggerThanCpuException {
        String facultyConstant = "EEMCS";
        String facultyConstant2 = "3ME";
        LocalDate dateConstant = LocalDate.now().plusDays(1);

        ScheduleJob scheduleJob = new ScheduleJob(1, facultyConstant, dateConstant.plusDays(2),
                50, 10, 2);

        //CHECKSTYLE.OFF: Indentation
        FacultyResource[] dayOne = {
                new FacultyResource(facultyConstant, dateConstant, 2, 1, 0),
                new FacultyResource(facultyConstant2, dateConstant, 10, 10, 10)};
        //CHECKSTYLE.ON: Indentation
        FacultyResource[] dayTwo = {new FacultyResource(facultyConstant, dateConstant.plusDays(1), 5, 2, 2)};

        String url = processingJobsService.getResourcesUrl() + "/facultyResources?faculty="
                + facultyConstant + "&day=";

        Mockito.when(restTemplate.getForEntity(url + dateConstant, FacultyResource[].class))
                .thenReturn(new ResponseEntity<>(dayOne, HttpStatus.OK));
        Mockito.when(restTemplate.getForEntity(url + dateConstant.plusDays(1), FacultyResource[].class))
                .thenReturn(new ResponseEntity<>(dayTwo, HttpStatus.OK));

        processingJobsService.scheduleJob(scheduleJob);

        Mockito.verify(restTemplate).postForEntity(processingJobsService.getJobsUrl() + "/updateStatus",
                new UpdateJob(1, "unscheduled", null), Void.class);

        List<ScheduledInstance> fromDb = scheduledInstanceRepository.findAllByJobId(1L);
        assertThat(fromDb.size()).isEqualTo(0);
    }

    @Test
    public void scheduleJob_notAbleToSchedule_fullDay_worksCorrectly() throws ResourceBiggerThanCpuException {
        String facultyConstant = "EEMCS";
        String facultyConstant2 = "3ME";
        LocalDate dateConstant = LocalDate.now().plusDays(1);

        ScheduledInstance toDb = new ScheduledInstance(4L, "3ME", 7, 1, 1, dateConstant);
        scheduledInstanceRepository.save(toDb);

        //CHECKSTYLE.OFF: Indentation
        FacultyResource[] dayOne = {
                new FacultyResource(facultyConstant, dateConstant, 2, 1, 0),
                new FacultyResource(facultyConstant2, dateConstant, 10, 10, 10)};
        //CHECKSTYLE.ON: Indentation
        FacultyResource[] dayTwo = {new FacultyResource(facultyConstant, dateConstant.plusDays(1), 5, 2, 2)};

        String url = processingJobsService.getResourcesUrl() + "/facultyResources?faculty="
                + facultyConstant + "&day=";

        Mockito.when(restTemplate.getForEntity(url + dateConstant, FacultyResource[].class))
                .thenReturn(new ResponseEntity<>(dayOne, HttpStatus.OK));
        Mockito.when(restTemplate.getForEntity(url + dateConstant.plusDays(1), FacultyResource[].class))
                .thenReturn(new ResponseEntity<>(dayTwo, HttpStatus.OK));

        ScheduleJob scheduleJob = new ScheduleJob(1, facultyConstant, dateConstant.plusDays(2),
                6, 2, 2);

        processingJobsService.scheduleJob(scheduleJob);

        Mockito.verify(restTemplate).postForEntity(processingJobsService.getJobsUrl() + "/updateStatus",
                new UpdateJob(1, "unscheduled", null), Void.class);

        List<ScheduledInstance> fromDb = scheduledInstanceRepository.findAllByJobId(1L);
        assertThat(fromDb.size()).isEqualTo(0);
    }

    @Test
    public void scheduleJob_withGpuGreaterThanCpu_throwsException() {
        ScheduleJob scheduleJob = new ScheduleJob(1, "EEMCS", LocalDate.now().plusDays(1),
                1, 2, 1);
        Exception e = assertThrows(ResourceBiggerThanCpuException.class,
                () -> processingJobsService.scheduleJob(scheduleJob));
        assertThat(e.getMessage()).isEqualTo("GPU usage cannot be greater than the CPU usage.");
    }

    @Test
    public void scheduleJob_withMemoryGreaterThanCpu_throwsException() {
        ScheduleJob scheduleJob = new ScheduleJob(1, "EEMCS", LocalDate.now().plusDays(1),
                1, 1, 2);
        Exception e = assertThrows(ResourceBiggerThanCpuException.class,
                () -> processingJobsService.scheduleJob(scheduleJob));
        assertThat(e.getMessage()).isEqualTo("Memory usage cannot be greater than the CPU usage.");
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
