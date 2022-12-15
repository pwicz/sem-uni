package nl.tudelft.sem.template.example.integration;

import commons.Job;
import commons.NetId;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.example.domain.JobService;
import nl.tudelft.sem.template.example.integration.utils.JsonUtil;
import nl.tudelft.sem.template.example.models.JobRequestModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class ExampleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Autowired
    private transient JobService mockJobService;

    @Test
    public void helloWorld() throws Exception {
        // Arrange
        // Notice how some custom parts of authorisation need to be mocked.
        // Otherwise, the integration test would never be able to authorise as the authorisation server is offline.
        when(mockAuthenticationManager.getNetId()).thenReturn("ExampleUser");
        when(mockAuthenticationManager.getRole()).thenReturn("employee");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");
        when(mockJobService.createJob(new NetId("ExampleUser"), new NetId("ExampleUser"), "memory", anyInt(), anyInt(), anyInt(), anyString())).thenReturn(new Job(new NetId("ExampleUser"), "memory", 10, 10, 10));

        JobRequestModel model = new JobRequestModel();
        model.setNetId("ExampleUser");
        model.setCpuUsage(10);
        model.setGpuUsage(10);
        model.setMemoryUsage(10);
        model.setResourceType("memory");

        // Act
        ResultActions result = mockMvc.perform(post("/addJob")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));
        // Assert
        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("Hello ExampleUser");

    }
}
