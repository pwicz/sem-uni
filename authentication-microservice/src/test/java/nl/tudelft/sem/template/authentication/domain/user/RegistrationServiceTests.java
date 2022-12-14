package nl.tudelft.sem.template.authentication.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import commons.Faculty;
import commons.NetId;
import java.util.ArrayList;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockPasswordEncoder"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RegistrationServiceTests {

    @Autowired
    private transient RegistrationService registrationService;

    @Autowired
    private transient PasswordHashingService mockPasswordEncoder;

    @Autowired
    private transient UserRepository userRepository;

    @Test
    public void createUser_withValidData_worksCorrectly() throws Exception {
        // Arrange
        final NetId testUser = new NetId("SomeUser");
        final Password testPassword = new Password("password123");
        final Role role = new Role("employee");
        final ArrayList<Faculty> faculties = new ArrayList<>();
        faculties.add(new Faculty("EEMCS"));
        //        final Set<Faculty> faculties = new HashSet<>();
        //        faculties.add(new Faculty("EEMCS"));
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");
        when(mockPasswordEncoder.hash(testPassword)).thenReturn(testHashedPassword);

        // Act
        registrationService.registerUser(testUser, testPassword, role, faculties);

        // Assert
        AppUser savedUser = userRepository.findByNetId(testUser).orElseThrow();

        assertThat(savedUser.getNetId()).isEqualTo(testUser);
        assertThat(savedUser.getPassword()).isEqualTo(testHashedPassword);
    }

    @Test
    public void createUser_withExistingUser_throwsException() {
        // Arrange
        final NetId testUser = new NetId("SomeUser");
        final HashedPassword existingTestPassword = new HashedPassword("password123");
        final Password newTestPassword = new Password("password456");
        final Role role = new Role("employee");
        final ArrayList<Faculty> faculties = new ArrayList<>();
        final Faculty faculty = new Faculty("EEMCS");
        faculties.add(faculty);
        //        final Set<Faculty> faculties = new HashSet<>();
        //        faculties.add(new Faculty("EEMCS"));

        AppUser existingAppUser = new AppUser(testUser, existingTestPassword, role, faculties);
        userRepository.save(existingAppUser);

        // Act
        ThrowingCallable action = () -> registrationService.registerUser(testUser, newTestPassword, role, faculties);

        // Assert
        assertThatExceptionOfType(Exception.class)
                .isThrownBy(action);

        AppUser savedUser = userRepository.findByNetId(testUser).orElseThrow();

        assertThat(savedUser.getNetId()).isEqualTo(testUser);
        assertThat(savedUser.getPassword()).isEqualTo(existingTestPassword);
    }
}
