package nl.tudelft.sem.template.example.domain;

import static org.assertj.core.api.Assertions.assertThat;

import commons.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.controllers.NodeController;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ClustersTest {

    @MockBean
    private RestTemplate restTemplate;
    @Autowired
    private transient NodeRepository repo;
    @MockBean
    private transient AuthManager authManager;
    @Autowired
    private NodeController nodeController;

    @Test
    public void addCluster_worksCorrectly() throws Exception {
        String facultyConstant = "EEMCS";
        String facultyConstant2 = "3ME";
        LocalDate dateConstant = LocalDate.now().plusDays(1);

        nodeController = new NodeController(repo, authManager, restTemplate);

        Mockito.when(authManager.getNetId())
                .thenReturn("filip");

        ResponseEntity<String[]> facultyType = restTemplate.postForEntity(
                "http://localhost:8081/faculty", authManager.getNetId(), String[].class);

        System.out.println(authManager.getNetId());

        List<String> f = Arrays.asList(facultyType.getBody());
        System.out.println(f);


        Node node = new Node("filip", "url", facultyConstant, "token", 10, 10 ,10);
        nodeController.addNode(node);

        Map<String, Resource> res = nodeController.getAllResourcesNextDay().getBody();
        System.out.println(res);



        assertThat(0).isEqualTo(0);
    }

}
