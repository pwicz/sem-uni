package nl.tudelft.sem.template.authentication.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import commons.Faculty;
import commons.NetId;
import commons.Role;
import commons.RoleValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.template.authentication.authentication.AuthManager;
import nl.tudelft.sem.template.authentication.authentication.JwtTokenGenerator;
import nl.tudelft.sem.template.authentication.domain.user.AppUser;
import nl.tudelft.sem.template.authentication.domain.user.GetFacultyService;
import nl.tudelft.sem.template.authentication.domain.user.HashedPassword;
import nl.tudelft.sem.template.authentication.domain.user.Password;
import nl.tudelft.sem.template.authentication.domain.user.PasswordHashingService;
import nl.tudelft.sem.template.authentication.domain.user.UserRepository;
import nl.tudelft.sem.template.authentication.integration.utils.JsonUtil;
import nl.tudelft.sem.template.authentication.models.AuthenticationRequestModel;
import nl.tudelft.sem.template.authentication.models.AuthenticationResponseModel;
import nl.tudelft.sem.template.authentication.models.ChangeFacultyRequestModel;
import nl.tudelft.sem.template.authentication.models.RegistrationRequestModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockPasswordEncoder", "mockTokenGenerator", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UsersTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private transient AuthManager mockAuthManager;

    @Autowired
    private transient PasswordHashingService mockPasswordEncoder;

    @Autowired
    private transient JwtTokenGenerator mockJwtTokenGenerator;

    @Autowired
    private transient AuthenticationManager mockAuthenticationManager;

    @Autowired
    private transient UserRepository userRepository;

    @Test
    public void register_withValidData_worksCorrectly() throws Exception {
        // Arrange
        final NetId testUser = new NetId("SomeUser");
        final Password testPassword = new Password("password123");
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");
        final Role role = new Role(RoleValue.FAC_ACC);
        final Faculty faculty = new Faculty("EEMCS");
        when(mockPasswordEncoder.hash(testPassword)).thenReturn(testHashedPassword);

        RegistrationRequestModel model = new RegistrationRequestModel();
        model.setNetId(testUser.toString());
        model.setPassword(testPassword.toString());
        model.setRole(role.getAuthority());
        model.setFaculty(faculty.toString());
        System.out.println(JsonUtil.serialize(model));
        // Act
        ResultActions resultActions = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isOk());

        AppUser savedUser = userRepository.findByNetId(testUser).orElseThrow();

        assertThat(savedUser.getNetId()).isEqualTo(testUser);
        assertThat(savedUser.getPassword()).isEqualTo(testHashedPassword);
    }

    @Test
    public void register_withExistingUser_throwsException() throws Exception {
        // Arrange
        final NetId testUser = new NetId("SomeUser");
        final Password newTestPassword = new Password("password456");
        final HashedPassword existingTestPassword = new HashedPassword("password123");
        final Role role = new Role(RoleValue.EMPLOYEE);
        final ArrayList<Faculty> faculties = new ArrayList<>();
        faculties.add(new Faculty("EEMCS"));
        //        final Set<Faculty> faculties = new HashSet<>();
        //        faculties.add(new Faculty("EEMCS"));

        AppUser existingAppUser = new AppUser(testUser, existingTestPassword, role, faculties);
        userRepository.save(existingAppUser);

        RegistrationRequestModel model = new RegistrationRequestModel();
        model.setNetId(testUser.toString());
        model.setPassword(newTestPassword.toString());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isBadRequest());

        AppUser savedUser = userRepository.findByNetId(testUser).orElseThrow();

        assertThat(savedUser.getNetId()).isEqualTo(testUser);
        assertThat(savedUser.getPassword()).isEqualTo(existingTestPassword);
    }

    @Test
    public void login_withValidUser_returnsToken() throws Exception {
        // Arrange
        final NetId testUser = new NetId("SomeUser");
        final Password testPassword = new Password("password123");
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");
        final Role role = new Role(RoleValue.EMPLOYEE);
        final ArrayList<Faculty> faculties = new ArrayList<>();
        final Faculty faculty = new Faculty("EEMCS");
        faculties.add(faculty);
        //        final Set<Faculty> faculties = new HashSet<>();
        //        faculties.add(new Faculty("EEMCS"));
        when(mockPasswordEncoder.hash(testPassword)).thenReturn(testHashedPassword);

        when(mockAuthenticationManager.authenticate(argThat(authentication ->
                !testUser.toString().equals(authentication.getPrincipal())
                    || !testPassword.toString().equals(authentication.getCredentials())
        ))).thenThrow(new UsernameNotFoundException("User not found"));

        final String testToken = "testJWTToken";
        when(mockJwtTokenGenerator.generateToken(
            argThat(userDetails -> userDetails.getUsername().equals(testUser.toString())))
        ).thenReturn(testToken);

        AppUser appUser = new AppUser(testUser, testHashedPassword, role, faculties);
        userRepository.save(appUser);

        AuthenticationRequestModel model = new AuthenticationRequestModel();
        model.setNetId(testUser.toString());
        model.setPassword(testPassword.toString());

        // Act
        ResultActions resultActions = mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));


        // Assert
        MvcResult result = resultActions
                .andExpect(status().isOk())
                .andReturn();

        AuthenticationResponseModel responseModel = JsonUtil.deserialize(result.getResponse().getContentAsString(),
                AuthenticationResponseModel.class);

        assertThat(responseModel.getToken()).isEqualTo(testToken);

        verify(mockAuthenticationManager).authenticate(argThat(authentication ->
                testUser.toString().equals(authentication.getPrincipal())
                    && testPassword.toString().equals(authentication.getCredentials())));
    }

    @Test
    public void changeFacultyTest() throws Exception {
        // Arrange
        final NetId testUser = new NetId("SomeUser");
        final NetId admin = new NetId("Admin");
        final Password testPassword = new Password("password123");
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");
        final Role role = new Role("EMPLOYEE");
        final ArrayList<Faculty> faculties = new ArrayList<>();
        final Faculty faculty = new Faculty("EEMCS");
        faculties.add(faculty);

        when(mockPasswordEncoder.hash(testPassword)).thenReturn(testHashedPassword);

        when(mockAuthenticationManager.authenticate(argThat(authentication ->
            !testUser.toString().equals(authentication.getPrincipal())
                || !testPassword.toString().equals(authentication.getCredentials())
        ))).thenThrow(new UsernameNotFoundException("User not found"));

        final String testToken = "testJWTToken";
        when(mockAuthManager.getRole()).thenReturn(new Role(RoleValue.ADMIN));

        AppUser appUser = new AppUser(testUser, testHashedPassword, role, faculties);
        userRepository.save(appUser);

        AppUser adminUser = new AppUser(admin, testHashedPassword, new Role(RoleValue.ADMIN), faculties);
        userRepository.save(adminUser);

        ChangeFacultyRequestModel model = new ChangeFacultyRequestModel();
        model.setNetId(testUser.toString());
        model.setFaculty("3ME;CS;CG;");

        GetFacultyService getFacultyService = new GetFacultyService(userRepository);
        getFacultyService.changeFaculty(testUser, Arrays.asList(
            new Faculty("3ME"),
            new Faculty("CS"),
            new Faculty("CG")));

        AppUser savedUser = userRepository.findByNetId(testUser).orElseThrow();

        assertThat(savedUser.getNetId()).isEqualTo(testUser);
        List<Faculty> f = Arrays.asList(new Faculty("3ME"), new Faculty("CS"), new Faculty("CG"));
        assertThat(savedUser.getFaculty()).isEqualTo(f);
    }

    @Test
    public void login_withNonexistentUsername_returns403() throws Exception {
        // Arrange
        final String testUser = "SomeUser";
        final String testPassword = "password123";

        when(mockAuthenticationManager.authenticate(argThat(authentication ->
                testUser.equals(authentication.getPrincipal())
                    && testPassword.equals(authentication.getCredentials())
        ))).thenThrow(new UsernameNotFoundException("User not found"));

        AuthenticationRequestModel model = new AuthenticationRequestModel();
        model.setNetId(testUser);
        model.setPassword(testPassword);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isForbidden());

        verify(mockAuthenticationManager).authenticate(argThat(authentication ->
                testUser.equals(authentication.getPrincipal())
                    && testPassword.equals(authentication.getCredentials())));

        verify(mockJwtTokenGenerator, times(0)).generateToken(any());
    }

    @Test
    public void login_withInvalidPassword_returns403() throws Exception {
        // Arrange
        final String testUser = "SomeUser";
        final String wrongPassword = "password1234";
        final String testPassword = "password123";
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");
        when(mockPasswordEncoder.hash(new Password(testPassword))).thenReturn(testHashedPassword);

        when(mockAuthenticationManager.authenticate(argThat(authentication ->
                testUser.equals(authentication.getPrincipal())
                    && wrongPassword.equals(authentication.getCredentials())
        ))).thenThrow(new BadCredentialsException("Invalid password"));

        final ArrayList<Faculty> faculties = new ArrayList<>();
        final Faculty faculty = new Faculty("EEMCS");
        faculties.add(faculty);
        AppUser appUser = new AppUser(new NetId(testUser), testHashedPassword, new Role(RoleValue.EMPLOYEE), faculties);
        userRepository.save(appUser);

        AuthenticationRequestModel model = new AuthenticationRequestModel();
        model.setNetId(testUser);
        model.setPassword(wrongPassword);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isUnauthorized());

        verify(mockAuthenticationManager).authenticate(argThat(authentication ->
                testUser.equals(authentication.getPrincipal())
                    && wrongPassword.equals(authentication.getCredentials())));

        verify(mockJwtTokenGenerator, times(0)).generateToken(any());
    }
}
