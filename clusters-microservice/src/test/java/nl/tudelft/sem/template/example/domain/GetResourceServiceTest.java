package nl.tudelft.sem.template.example.domain;

import static org.assertj.core.api.Assertions.assertThat;

import commons.FacultyResource;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class GetResourceServiceTest {

    @Autowired
    private transient NodeRepository nodeRepository;

    @Autowired
    private transient GetResourceService getResourceService;

    @Test
    void getResourcesNextDay() {

        nodeRepository.saveAll(List.of(new Node("XYZ", "XYZ", "EEMCS2", "XYZ", 10, 10, 10),
                new Node("XYZ2", "XYZ2", "EEMCS2", "XYZ2", 15, 2, 5),
                new Node("XYZ3", "XYZ3", "3ME", "XYZ3", 10, 10, 10)));

        var answer = getResourceService.getResourcesNextDay(List.of("EEMCS2"));
        assertThat(answer.size()).isEqualTo(1);
        assertThat(answer.stream().mapToInt(FacultyResource::getCpuUsage).sum()).isEqualTo(25);
        assertThat(answer.stream().mapToInt(FacultyResource::getGpuUsage).sum()).isEqualTo(12);
        assertThat(answer.stream().mapToInt(FacultyResource::getMemoryUsage).sum()).isEqualTo(15);
    }
}