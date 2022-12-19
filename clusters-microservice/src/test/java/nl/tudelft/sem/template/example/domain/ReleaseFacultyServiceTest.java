package nl.tudelft.sem.template.example.domain;

import java.time.LocalDate;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.services.ReleaseFacultyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReleaseFacultyServiceTest {
    @MockBean
    private AuthManager authManager;

    @Autowired
    private ReleaseFacultyService releaseFacultyService;

    String faculty;
    LocalDate date;
    int days;

    ReleaseFacultyDto releaseFacultyDto;

    /**
     * Initializes fields.
     */
    @BeforeEach
    public void init() {
        faculty = "EEMCS";
        date = LocalDate.now();
        days = 2;

        releaseFacultyDto = new ReleaseFacultyDto(faculty, date, days);
    }

    @Test
    public void unauthorizedTest() {
        Mockito.when(authManager.getRole()).thenReturn("Magician");

        System.out.println(authManager.getRole());

        Assertions.assertThrows(AccountNotAuthorizedException.class, () -> {
            releaseFacultyService.releaseFaculty(releaseFacultyDto);
        });
    }

    //    @Test
    //    public void facultyNotPresent() {
    //        Mockito.when(authManager.getRole()).thenReturn("FacultyAccount");
    //        Mockito.when(authManager.getNetId()).thenReturn("NetId");
    //
    //        String[] facs = new String[3];
    //        facs[0] = "EEMCS";
    //        facs[1] = "3ME";
    //        facs[2] = "Architecture";
    //
    //        Mockito.when(releaseFacultyServiceMocked.getFaculty("netId")).thenReturn(Arrays.asList(facs));
    //
    //        ReleaseFacultyDto dto = new ReleaseFacultyDto("Magic", LocalDate.now(), 2);
    //
    //        Assertions.assertThrows(UserNotInThisFacultyException.class, () -> {
    //            releaseFacultyService.releaseFaculty(dto);
    //        });
    //    }

    //    @Test
    //    public void unauthorizedTest() {
    //        Mockito.when(authManager.getRole()).thenReturn("Magician");
    //
    //        Assertions.assertThrows(AccountNotAuthorizedException.class, () -> {
    //            releaseFacultyService.releaseFaculty(releaseFacultyDto);
    //        });
    //
    //        Mockito.verify(nodeRepository, Mockito.times(0)).updateRelease(releaseFacultyDto.getFaculty(),
    //                releaseFacultyDto.getDate(), releaseFacultyDto.getDays());
    //    }

    //    @Test
    //    public void userNotInThisFacultyTest() {
    //        Mockito.when(authManager.getRole()).thenReturn("FacultyAccount");
    //        Mockito.when(authManager.getNetId()).thenReturn("netId");
    //
    //        String[] facs = new String[3];
    //        facs[0] = "EEMCS";
    //        facs[1] = "3ME";
    //        facs[2] = "Architecture";
    //
    ////        Mockito.when(restTemplate.getForEntity("http://localhost:8081/faculty", String[].class)).
    ////                thenReturn(new ResponseEntity<String[]>(facs, HttpStatus.OK));
    //
    //        Mockito.when(releaseFacultyService.getFaculty("netId")).thenReturn(Arrays.asList(facs));
    //
    //        //Mockito.verify(restTemplate, Mockito.notNull()).getForEntity("http://localhost:8081/faculty", String[].class).getBody();
    //
    //        ReleaseFacultyDto dto = new ReleaseFacultyDto("Magic", LocalDate.now(), 2);
    //
    //        assertThat(releaseFacultyService.getFaculty(authManager.getNetId()).contains(dto.getFaculty())).isFalse();
    //
    //        Assertions.assertThrows(UserNotInThisFacultyException.class, () -> {
    //           releaseFacultyService.releaseFaculty(dto);
    //        });
    //    }
}
