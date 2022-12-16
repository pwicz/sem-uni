package nl.tudelft.sem.template.example.domain.processing;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

import commons.FacultyResource;
import commons.UpdateJob;
import java.time.LocalDate;
import java.util.List;
import nl.tudelft.sem.template.example.domain.db.ScheduledInstance;
import nl.tudelft.sem.template.example.domain.db.ScheduledInstanceRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UpdatingJobsServiceTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private transient ScheduledInstanceRepository scheduledInstanceRepository;

    @Autowired
    private transient UpdatingJobsService updatingJobsService;

    @Test
    void updateSchedule_nothingToChange_worksCorrectly() {
        List<ScheduledInstance> instances = List.of(
              new ScheduledInstance(1L, "EEMCS", 10, 5, 3, LocalDate.now().plusDays(1)),
              new ScheduledInstance(2L, "EEMCS", 5, 5, 2, LocalDate.now().plusDays(1)),
              new ScheduledInstance(3L, "EEMCS", 10, 10, 9, LocalDate.now().plusDays(1))
        );
        scheduledInstanceRepository.saveAll(instances);

        FacultyResource update = new FacultyResource("EEMCS", LocalDate.now().plusDays(1), 25, 25, 15);
        updatingJobsService.updateSchedule(update);

        Mockito.verify(restTemplate, Mockito.never()).postForEntity(anyString(),
                Mockito.any(UpdateJob.class), Mockito.eq(Void.class));
        assertThat(scheduledInstanceRepository.findAll().size()).isEqualTo(3);
    }
}