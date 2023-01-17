package nl.tudelft.sem.template.example.controllers;

import commons.FacultyRequestModel;
import commons.FacultyResource;
import commons.FacultyResourceModel;
import commons.FacultyResponseModel;
import commons.Resource;
import commons.RoleValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.GetResourceService;
import nl.tudelft.sem.template.example.domain.ModifyRepoService;
import nl.tudelft.sem.template.example.domain.Node;
import nl.tudelft.sem.template.example.models.ReleaseFacultyModel;
import nl.tudelft.sem.template.example.models.ToaRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;



@RestController
@RequestMapping("/cluster")
public class NodeController {
    private final transient RestTemplate restTemplate;
    private final transient AuthManager authManager;
    private final transient ModifyRepoService modifyRepoService;
    private final transient GetResourceService getResourceService;

    /**
     * Constructor for the NodeController.
     *
     * @param authManager  contains details about the user
     * @param restTemplate RestTemplate to send requests
     */
    @Autowired
    public NodeController(AuthManager authManager, RestTemplate restTemplate,
                          ModifyRepoService modifyRepoService,
                          GetResourceService getResourceService) {
        this.authManager = authManager;
        this.restTemplate = restTemplate;
        this.modifyRepoService = modifyRepoService;
        this.getResourceService = getResourceService;
    }

    /**
     * Admin utilises this path to get resources for each faculty.
     *
     * @param faculty the faculty admin wants resources for
     */
    @GetMapping(path = {"/resources/{faculty}"})
    public ResponseEntity<Resource> getTotalResourcesForFaculty(@PathVariable("faculty") String faculty) {
        if (!checkIfAdmin()) {
            System.out.println("Admin privileges required. Current Role:" + authManager.getRole().toString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }
        Resource r = getResourceService.getTotalResourcesForFaculty(faculty);
        return ResponseEntity.ok(r);
    }

    //This is only needed to fix PMD
    private boolean checkIfAdmin() {
        return authManager.getRole().getRoleValue() == RoleValue.ADMIN;
    }

    /**
     * Returns all the nodes available for admin to see.
     */
    @GetMapping(path = {"/resources"})
    public ResponseEntity<List<Node>> getAllNodes() {
        if (!checkIfAdmin()) {
            System.out.println("Admin privileges required. Current Role:" + authManager.getRole().toString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Node> nodes = getResourceService.getAllNodes();
        return ResponseEntity.ok(nodes);
    }

    /**
     * Test to see what role you currently are.
     */
    @GetMapping(path = {"/role"})
    public ResponseEntity<String> userRole() {
        if (authManager.getRole() == null) {
            System.out.println("user has null role");
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(authManager.getRole().toString());
    }

    /**
     * Gets the number of free resources available for facculty and day.
     * Only allowed to access if you belong to the faculty.
     *
     * @param facDay request model for faculty and date
     */
    @PostMapping(path = {"/facultyDayResource"})
    public ResponseEntity<FacultyResource> getFacultyAvailableResourcesForDay(@RequestBody FacultyResourceModel facDay) {
        FacultyResource facultyResources = getResourceService.getFacultyAvailableResourcesForDay(
                facDay.getFaculty(), facDay.getDate());

        return ResponseEntity.ok(facultyResources);
    }

    /**
     * Endpoint where you can add node.
     * The node has to belong to the faculty you are in
     * The nodes resources has to match cpu >= gpu && cpu >= mem
     *
     * @param node you want to add
     */
    @PostMapping(path = {"/addNode"})
    public ResponseEntity<Node> addNode(@RequestBody Node node) {
        List<String> faculties = getFaculty();
        faculties.add("FreePool");
        if (checkIfAdmin()) {
            node.setFaculty("FreePool");
        }
        Node newNode = modifyRepoService.addNode(node, authManager.getNetId(), faculties);
        if (newNode == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(newNode);
    }

    /**
     * Endpoint to release nodes to the free pool.
     * Only facculty accounts are allowed to do this.
     * Only sets the date its released from and till
     */
    @PostMapping("/releaseFaculty")
    public ResponseEntity<String> releaseFaculty(@RequestBody ReleaseFacultyModel releaseModel) {
        if (authManager.getRole().getRoleValue() != RoleValue.FAC_ACC) {
            System.out.println("Account is not faculty account. Current: " + getFaculty());
            return ResponseEntity.badRequest().build();
        }
        String response = modifyRepoService.releaseFaculty(releaseModel.getFaculty(),
                releaseModel.getDate(), releaseModel.getDays(), getFaculty());
        if (response == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(response);
    }

    private List<String> getFaculty2(String netId) {
        String usersUrl = "http://localhost:8081/faculty"; //authentication microservice
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        FacultyRequestModel f = new FacultyRequestModel();
        f.setNetId(netId);
        HttpEntity<FacultyRequestModel> requestEntity = new HttpEntity<>(f, headers);
        ResponseEntity<FacultyResponseModel> facultyType = restTemplate.postForEntity(usersUrl,
                requestEntity, FacultyResponseModel.class);

        if (facultyType.getBody() == null) {
            return new ArrayList<>();
        }
        return facultyType.getBody().getFaculty();
    }

    private List<String> getFaculty() {
        String facultiesString = authManager.getFaculty().getAuthority();
        return new ArrayList<>(Arrays.asList(facultiesString.split(";")));
    }

    /**
     * Marks Node with the id as deleted.
     * Later when database clearer is called it will actually delete it from database.
     *
     * @param token token of access
     */
    @PostMapping("/delete")
    public ResponseEntity<String> deleteNode(@RequestBody ToaRequestModel token) {
        if (token == null) {
            return new ResponseEntity<>("No token of access provided", HttpStatus.BAD_REQUEST);
        }
        List<String> faculties = getFaculty();
        String response = modifyRepoService.disableNodeFromRepo(token.getToken(), faculties);
        if (response == null) {
            return new ResponseEntity<>("Failed to notify", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * The api GET endpoint to get a list of all resources in all clusters that are available
     * to the user.
     *
     * @return list of all resources in all clusters available to the user.
     */
    @GetMapping(path = "/resourcesNextDay")
    public ResponseEntity<List<FacultyResource>> getResourcesNextDay() {
        List<FacultyResource> res = getResourceService.getResourcesNextDay(getFaculty());
        return ResponseEntity.ok(res);
    }
}

