package nl.tudelft.sem.template.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import commons.FacultyRequestModel;
import commons.FacultyResource;
import commons.FacultyResourcesRequestModel;
import commons.FacultyResponseModel;
import commons.NetId;
import nl.tudelft.sem.template.example.domain.Node;
import commons.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.NodeRepository;
import nl.tudelft.sem.template.example.models.ReleaseFacultyModel;
import nl.tudelft.sem.template.example.models.ToaRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    private final transient NodeRepository repo;
    private final transient AuthManager authManager;
    private final transient JasonUtil jsonUtil;

    private final transient String usersUrl = "http://localhost:8081";

    /**
     * Constructor for the NodeController.
     *
     * @param repo         the repository interface
     * @param authManager  contains details about the user
     * @param restTemplate RestTemplate to send requests
     */
    @Autowired
    public NodeController(NodeRepository repo, AuthManager authManager, RestTemplate restTemplate, JasonUtil jsonUtil) {
        this.repo = repo;
        this.authManager = authManager;
        this.restTemplate = restTemplate;
        this.jsonUtil = jsonUtil;
    }

    /**
     * Admin utilises this path to get resources for each faculty.
     *
     * @param faculty the faculty admin wants resources for
     */
    @GetMapping(path = {"/resources/{faculty}"})
    public ResponseEntity<Resource> getTotalResourcesForFaculty(@PathVariable("faculty") String faculty) {
        if (!checkIfAdmin()) {
            System.out.println("Needed Admin permissions.Current: " + authManager.getRole().toString());
            return ResponseEntity.badRequest().build();
        }

        List<Node> facultyNodes = repo.getNodesByFaculty(faculty).get();
        int cpu = 0;
        int gpu = 0;
        int mem = 0;
        for (Node n : facultyNodes) {
            cpu += n.getCpu();
            gpu += n.getGpu();
            mem += n.getMemory();
        }
        Resource r = new Resource(cpu, gpu, mem);
        return ResponseEntity.ok(r);
    }

    //This is onyl needed to fix PMD
    private boolean checkIfAdmin() {
        return authManager.getRole().toString().equals("admin");
    }

    /**
     * Returns all the nodes available for admin to see.
     */
    @GetMapping(path = {"/resources"})
    public ResponseEntity<List<Node>> getAllNodes(@RequestBody String token) {
        if (!checkIfAdmin()) {
            System.out.println("Admin privileges required. Current Role:" + authManager.getRole().toString());
            return ResponseEntity.badRequest().build();
        }
        if (repo.getAllNodes().isEmpty()) {
            System.out.println("Db is empty");
            return ResponseEntity.ok(new ArrayList<Node>());
        }
        List<Node> nodes = repo.getAllNodes().get();

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

    @GetMapping(path = {"/facultyDayResource"})
    public ResponseEntity<FacultyResource> getFacultyAvailableResourcesForDay(@RequestBody FacultyResourcesRequestModel facDay) {
        Resource r = repo.getFreeResources(facDay.getFaculty(), facDay.getDate()).get();
        FacultyResource facultyResources = new FacultyResource(facDay.getFaculty(), facDay.getDate(),
                r.getCpu(), r.getGpu(), r.getMem());
        return ResponseEntity.ok(facultyResources);
    }

    /**
     * Endpoint where you can add node.
     * The node has to belongto the facculty you are in
     * The nodes resources has to match cpu >= gpu || cpu >= mem
     *
     * @param node you want to add
     */
    @PostMapping(path = {"/addNode"})
    public ResponseEntity<Node> addNode(@RequestBody Node node) throws JsonProcessingException {
        if (node.getName() == null || node.getUrl() == null
                || node.getFaculty() == null
                || node.getToken() == null) {
            System.out.println("Value is null");
            return ResponseEntity.badRequest().build();
        }
        if (node.getCpu() < node.getGpu()
                || node.getCpu() < node.getMemory()) {
            System.out.println("CPU resource smaller than GPU or MEMORY");
            return ResponseEntity.badRequest().build();
        }
        if (!node.getName().equals(authManager.getNetId())) {
            System.out.println("Node doesnt belong to " + node.getName());
            return ResponseEntity.badRequest().build();
        }
        List<String> faculties = getFaculty(authManager.getNetId());
        if (!(faculties.contains(node.getFaculty()))) {
            System.out.println("failed after get faculty");
            return ResponseEntity.badRequest().build();
        } else if (checkIfAdmin()) {
            node.setFaculty("FreePool");
        }
        try {
            //node.setToken(authManager.getToken()); // check if this works
            Node newNode = repo.save(node);
            return ResponseEntity.ok(newNode);
        } catch (Exception e) {
            System.out.println("failed to add node\n");
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint to release nodes to the free pool.
     * Only facculty accounts are allowed to do this.
     * Only sets the date its released from and till
     */
    @PostMapping("/releaseFaculty")
    public ResponseEntity<String> releaseFaculty(@RequestBody ReleaseFacultyModel releaseModel)
                                                throws JsonProcessingException {
        if (!authManager.getRole().equals("FacultyAccount")) {
            System.out.println("Account is not facculty account. Current: " + getFaculty(authManager.getNetId()));
            return ResponseEntity.badRequest().build();
        }
        if (releaseModel.getDate() == null || releaseModel.getFaculty() == null
                || releaseModel.getDate().isBefore(LocalDate.now()) || releaseModel.getDays() < 1) {
            System.out.println("Null or date is before today or length is less than 1");
            return ResponseEntity.badRequest().build();
        }
        if (!getFaculty(authManager.getNetId()).contains(releaseModel.getFaculty())) {
            System.out.println("Releasing someone elses faculty");
            return ResponseEntity.badRequest().build();
        }
        repo.updateRelease(releaseModel.getFaculty(), releaseModel.getDate(),
                releaseModel.getDate().plusDays(releaseModel.getDays()));
        return ResponseEntity.ok("Released from " + releaseModel.getDate()
                + " to " + releaseModel.getDate().plusDays(releaseModel.getDays()));
    }

    private List<String> getFaculty(String netId) throws JsonProcessingException {
        String usersUrl = "http://localhost:8081/faculty"; //authentication microservice
        HttpHeaders headers = new HttpHeaders();
        //headers.setBearerAuth(authManager.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        FacultyRequestModel f = new FacultyRequestModel();
        f.setNetId(netId);
        HttpEntity<FacultyRequestModel> requestEntity = new HttpEntity<>(f, headers);
        ResponseEntity<FacultyResponseModel> facultyType = restTemplate.postForEntity(usersUrl,
                requestEntity, FacultyResponseModel.class);

        if (facultyType.getBody() == null) {
            return new ArrayList<>();
        }
        //System.out.println("Faculty = " + facultyType.getBody().getFaculty().toString());
        return facultyType.getBody().getFaculty();
    }

    /**
     * Marks Node with the id as deleted.
     * Later when databse clearner is called it will actually delete from database.
     *
     * @param id    id of the node you want to delete
     * @param token token of access
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteNode(@PathVariable("id") long id,
                                             @RequestBody ToaRequestModel token) throws JsonProcessingException {
        //Node n = repo.getNodeById(id).get();
        if (token == null) {
            return ResponseEntity.badRequest().build();
        }
        if (repo.findById(id).isEmpty()) {
            System.out.println("Repo is empty");
            return ResponseEntity.badRequest().build();
        }
        List<String> faculties = getFaculty(authManager.getNetId());
        if (!checkIfAdmin() && !faculties.contains(repo.findById(id).get().getFaculty())) {
            System.out.println("Facultys dont match");
            return ResponseEntity.badRequest().build();
        }
        if (!token.getToken().equals(repo.findById(id).get().getToken())) {
            System.out.println("Tokens of access dont match");
            System.out.println("Token provided: " + token);
            System.out.println("Token required: " + repo.findById(id).get().getToken());
            return ResponseEntity.badRequest().build();
        }
        repo.setAsDeleted(id, LocalDate.now().plusDays(1L));
        Node n = repo.getNodeById(id).get();
        System.out.println(n.getRemovedDate());
        //n.setRemovedDate(LocalDate.now().plusDays(1L)); // not updated forever in the database
        try {
            String response = notifySchedulerOfResourceChange(LocalDate.now().plusDays(1L), n.getFaculty());
            return ResponseEntity.ok(response + " " + n.getRemovedDate());
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            return ResponseEntity.ok("Failed Notify: " + n.getRemovedDate().toString() + " updated remove date");
        }
    }

    //the addresses need to match up in the future
    private String notifySchedulerOfResourceChange(LocalDate date, String faculty) {
        String schedulerUrl = "http://localhost:8084"; //scheduler microservice

        ResponseEntity<String> facultyType = restTemplate.getForEntity(schedulerUrl
                + "/receiveResourceChange?faculty=" + faculty + "&day=" + date.toString(), String.class);

        if (facultyType.getBody() == null) {
            return "Failed Notify";
        }

        return facultyType.getBody();
    }

    /**
     * The api GET endpoint to get all Jobs in the database.
     *
     * @return list of Jobs to be scheduled
     */
    @GetMapping(path = "/resourcesNextDay")
    public ResponseEntity<List<FacultyResource>> getResourcesNextDay() throws Exception {
        String role = authManager.getRole().toString();
        if (!role.equals("EMPLOYEE") && !role.equals("ADMIN") && !role.equals("FAC_ACC")) {
            return ResponseEntity.badRequest().build();
        }

        ResponseEntity<String[]> facultyType = restTemplate.postForEntity(usersUrl
                + "/faculty", new NetId(authManager.getNetId()), String[].class);

        List<String> faculties = Arrays.asList(Objects.requireNonNull(facultyType.getBody()));
        System.out.println(faculties);

        List<FacultyResource> res = new ArrayList<>();

        for (String f : faculties) {
            Resource r = repo.getFreeResources(f, LocalDate.now().plusDays(1)).get();
            FacultyResource facultyResources = new FacultyResource(f, LocalDate.now().plusDays(1),
                    r.getCpu(), r.getGpu(), r.getMem());
            res.add(facultyResources);
        }
        return ResponseEntity.ok(res);
    }
}

