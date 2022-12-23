package nl.tudelft.sem.template.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import commons.FacultyRequestModel;
import commons.FacultyResource;
import commons.FacultyResourceModel;
import commons.FacultyResponseModel;
import commons.NetId;
import commons.Resource;
import commons.RoleValue;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.Node;
import nl.tudelft.sem.template.example.domain.NodeRepository;
import nl.tudelft.sem.template.example.dtos.AddNode;
import nl.tudelft.sem.template.example.models.ReleaseFacultyModel;
import nl.tudelft.sem.template.example.models.ToaRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

        Optional<List<Node>> facultyNodesOptional = repo.getNodesByFaculty(faculty);
        if (facultyNodesOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Node> facultyNodes = facultyNodesOptional.get();
        Resource r = resourceCreator(facultyNodes);
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
        if (repo.getAllNodes().isEmpty()) {
            System.out.println("Db is empty");
            return ResponseEntity.ok(new ArrayList<>());
        }
        List<Node> nodes = repo.getAllNodes().get();

        return ResponseEntity.ok(nodes);
    }

    /**
     * Gets the number of free resources available for faculty and day.
     * Only allowed to access if you belong to the faculty.
     *
     * @param facDay request model for faculty and date
     */
    @PostMapping(path = {"/facultyDayResource"})
    public ResponseEntity<FacultyResource> getFacultyAvailableResourcesForDay(@RequestBody FacultyResourceModel facDay) {
        if (repo.getAvailableResources(facDay.getFaculty(), facDay.getDate()).isPresent()) {
            List<Node> n = repo.getAvailableResources(facDay.getFaculty(), facDay.getDate()).get();
            Resource r = resourceCreator(n);
            FacultyResource facultyResources =
                    new FacultyResource(facDay.getFaculty(), facDay.getDate(), r.getCpu(), r.getGpu(), r.getMem());
            return ResponseEntity.ok(facultyResources);
        }
        FacultyResource facultyResources = new FacultyResource(facDay.getFaculty(), facDay.getDate(), 0, 0, 0);
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
    public ResponseEntity<Node> addNode(@RequestBody AddNode node) {
        Node n = Node.nodeCreator(node, authManager.getNetId());
        if (n == null) {
            System.out.println("Value is null");
            return ResponseEntity.badRequest().build();
        }
        if (n.getCpu() < n.getGpu()
                || n.getCpu() < n.getMemory()) {
            System.out.println("CPU resource smaller than GPU or MEMORY");
            return ResponseEntity.badRequest().build();
        }
        List<String> faculties = getFaculty();
        System.out.println(faculties);
        if (!(faculties.contains(n.getFaculty()))) {
            System.out.println("failed after get faculty");
            return ResponseEntity.badRequest().build();
        } else if (checkIfAdmin()) {
            n.setFaculty("FreePool");
        }
        try {
            Node newNode = repo.save(n);
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
    public ResponseEntity<String> releaseFaculty(@RequestBody ReleaseFacultyModel releaseModel) {
        if (authManager.getRole().getRoleValue() != RoleValue.FAC_ACC) {
            System.out.println("Account is not faculty account. Current: " + getFaculty());
            return ResponseEntity.badRequest().build();
        }
        if (releaseModel.getDate() == null || releaseModel.getFaculty() == null
                || releaseModel.getDate().isBefore(LocalDate.now()) || releaseModel.getDays() < 1) {
            System.out.println("Null or date is before today or length is less than 1");
            return ResponseEntity.badRequest().build();
        }
        if (!getFaculty().contains(releaseModel.getFaculty())) {
            System.out.println("Releasing someone else's faculty");
            return ResponseEntity.badRequest().build();
        }
        repo.updateRelease(releaseModel.getFaculty(), releaseModel.getDate(),
                releaseModel.getDate().plusDays(releaseModel.getDays()));
        return ResponseEntity.ok("Released from " + releaseModel.getDate()
                + " to " + releaseModel.getDate().plusDays(releaseModel.getDays()));
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
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteNode(@RequestBody ToaRequestModel token) {
        //Node n = repo.getNodeById(id).get();
        if (token == null || token.getToken() == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Node> nodeOptional = repo.getNodeByToken(token.getToken());
        if (nodeOptional.isEmpty()) {
            System.out.println("Repo is empty");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!checkIfAdmin() && !nodeOptional.get().getName().equals(authManager.getNetId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<String> faculties = getFaculty();
        if (!checkIfAdmin() && !faculties.contains(
                nodeOptional.get().getFaculty())) {
            System.out.println("Faculties don't match");
            return ResponseEntity.badRequest().build();
        }

        repo.setAsDeleted(token.getToken(), LocalDate.now().plusDays(1L));
        Node n = nodeOptional.get();

        //n.setRemovedDate(LocalDate.now().plusDays(1L)); // not updated forever in the database
        try {
            notifySchedulerOfResourceChange(LocalDate.now().plusDays(1L), n.getFaculty());
            return ResponseEntity.ok("Node(s) with token " + token.getToken() + " removed from "
                    + LocalDate.now().plusDays(1L));
        } catch (Exception e) {
            String problem = "Failed Notify: " + n.getRemovedDate().toString() + " updated remove date";
            System.err.println(problem);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed Notify: " + n.getRemovedDate().toString() + " updated remove date");
        }
    }

    private String notifySchedulerOfResourceChange(LocalDate date, String faculty) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<Node> n;
        if (repo.getAvailableResources(faculty, date).isPresent()) {
            n = repo.getAvailableResources(faculty, date).get();
        } else {
            n = List.of(new Node("", "", "", "", 0, 0, 0));
        }
        Resource r = resourceCreator(n);
        FacultyResource f = new FacultyResource();
        f.setFaculty(faculty);
        f.setDate(date);
        f.setCpuUsage(r.getCpu());
        f.setGpuUsage(r.getGpu());
        f.setMemoryUsage(r.getMem());
        HttpEntity<FacultyResource> requestEntity = new HttpEntity<>(f, headers);
        ResponseEntity<String> updated = restTemplate.postForEntity("http://localhost:8084/resource-update",
            requestEntity, String.class);
        return updated.getBody();
    }

    /**
     * The api GET endpoint to get a list of all resources in all clusters that are available
     * to the user.
     *
     * @return list of all resources in all clusters available to the user.
     */
    @GetMapping(path = "/resourcesNextDay")
    public ResponseEntity<List<FacultyResource>> getResourcesNextDay() {
        List<String> faculties = getFaculty();
        System.out.println(faculties);

        List<FacultyResource> res = new ArrayList<>();

        for (String f : faculties) {
            Optional<List<Node>> optionalNodeList = repo.getAvailableResources(f, LocalDate.now().plusDays(1));
            if (optionalNodeList.isEmpty()) {
                continue;
            }
            List<Node> n = optionalNodeList.get();
            Resource r = resourceCreator(n);
            FacultyResource facultyResources =
                    new FacultyResource(f, LocalDate.now().plusDays(1), r.getCpu(), r.getGpu(), r.getMem());
            res.add(facultyResources);
        }
        return ResponseEntity.ok(res);
    }

    private Resource resourceCreator(List<Node> nodes) {
        if (nodes == null) {
            return new Resource(0, 0, 0);
        }
        int cpu = 0;
        int gpu = 0;
        int mem = 0;
        for (Node n : nodes) {
            cpu += n.getCpu();
            gpu += n.getGpu();
            mem += n.getMemory();
        }
        return new Resource(cpu, gpu, mem);
    }
}

