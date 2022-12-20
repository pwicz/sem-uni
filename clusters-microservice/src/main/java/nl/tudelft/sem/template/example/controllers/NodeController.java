package nl.tudelft.sem.template.example.controllers;

import commons.FacultyResource;
import commons.NetId;
import commons.Node;
import commons.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final transient String usersUrl = "http://localhost:8081";

    /**
     * Constructor for the NodeController.
     *
     * @param repo the repository interface
     * @param authManager contains details about the user
     * @param restTemplate RestTemplate to send requests
     */
    @Autowired
    public NodeController(NodeRepository repo, AuthManager authManager, RestTemplate restTemplate) {
        this.repo = repo;
        this.authManager = authManager;
        this.restTemplate = restTemplate;
    }

    /**
     * Admin utilises this path to get resources for each faculty.
     *
     * @param faculty the faculty admin wants resources for
     */
    @GetMapping(path = {"/resources/{faculty}"})
    public ResponseEntity<Resource> getTotalResourcesForFaculty(@PathVariable("faculty") String faculty) {
        if (!authManager.getRole().equals("Admin")) {
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

    /**
     * Returns all the nodes available for admin to see.
     *
     */
    @GetMapping(path = {"/resources"})
    public ResponseEntity<List<Node>> getAllNodes() {
        if (!authManager.getRole().equals("Admin")) {
            return ResponseEntity.badRequest().build();
        }
        List<Node> nodes = repo.getAllNodes().get();
        return ResponseEntity.ok(nodes);
    }

    /**
     * Gets the number of free resources available for facculty and day.
     * Only allowed to access if you belong to the faculty.
     *
     * @param faculty faculty you want resources from
     * @param date day you want to see the free resources for
     */
    @GetMapping(path = {"/resources?faculty={faculty}&day={date}"})
    public ResponseEntity<FacultyResource> getFacultyAvailableResourcesForDay(@PathVariable("faculty") String faculty,
                                                                              @PathVariable("date") String date) {
        FacultyResource facultyResources = repo.getFreeResources(faculty, date).get();
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
    public ResponseEntity<Node> addNode(@RequestBody Node node) {
        //check for if url looks like url later
        if (node.getName() == null || node.getUrl() == null
                || node.getFaculty() == null
                || node.getToken() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (node.getCpu() < node.getGpu()
                || node.getCpu() < node.getMemory()) {
            return ResponseEntity.badRequest().build();
        }
        if (!node.getName().equals(authManager.getNetId())) {
            return ResponseEntity.badRequest().build();
        }
        if (!getFaculty(authManager.getNetId()).contains(node.getFaculty())) {
            return ResponseEntity.badRequest().build();
        }
        try {
            //node.setToken(authManager.getToken()); // check if this works
            Node newNode = repo.save(node);
            return ResponseEntity.ok(newNode);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint to release nodes to the free pool.
     * Only facculty accounts are allowed to do this.
     * Only sets the date its released from and till
     *
     * @param faculty faculty of the release
     * @param date date its released from
     * @param days number days of days its released for
     */
    @PostMapping("/releaseFaculty?faculty={faculty}&date={date}&days={days}")
    public ResponseEntity<String> releaseFaculty(@PathVariable("faculty") String faculty,
                                                 @PathVariable("date") LocalDate date, @PathVariable("days") int days) {
        if (!authManager.getRole().equals("FacultyAccount")) {
            return ResponseEntity.badRequest().build();
        }
        if (!getFaculty(authManager.getNetId()).contains(faculty)) {
            return ResponseEntity.badRequest().build();
        }
        if (date == null || faculty == null || date.isBefore(LocalDate.now()) || days < 1) {
            return ResponseEntity.badRequest().build();
        }
        repo.updateRelease(faculty, date, days);
        return ResponseEntity.ok("Released");
    }

    private List<String> getFaculty(String token) {
        ResponseEntity<String[]> facultyType = restTemplate.postForEntity(usersUrl
                + "/faculty", authManager.getNetId(), String[].class);

        if (facultyType.getBody() == null) {
            return new ArrayList<>();
        }

        return Arrays.asList(facultyType.getBody());
    }

    /**
     * Marks Node with the id as deleted.
     * Later when databse clearner is called it will actually delete from database.
     *
     * @param id id of the node you want to delete
     * @param token token of access
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteNode(@PathVariable("id") long id, @RequestBody String token) {
        //Node n = repo.getNodeById(id).get();
        if (token == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!getFaculty(authManager.getNetId()).contains(repo.getNodeById(id).get().getFaculty())) {
            return ResponseEntity.badRequest().build();
        }
        if (!token.equals(repo.getNodeById(id).get().getToken())) {
            return ResponseEntity.badRequest().build();
        }
        repo.setAsDeleted(id, LocalDate.now().plusDays(1L));
        Node n = repo.getNodeById(id).get();
        return ResponseEntity.ok(n.getRemovedDate().toString());
    }

    /**
     * The api GET endpoint to get all Jobs in the database.
     *
     * @return list of Jobs to be scheduled
     */
    @GetMapping(path = "/resourcesNextDay")
    public ResponseEntity<List<FacultyResource>> getResourcesNextDay() throws Exception {
        String role = authManager.getRole().toString();
        if (!role.equals("employee") || !role.equals("admin") || !role.equals("fac_acc")) {
            return ResponseEntity.badRequest().build();
        }

        ResponseEntity<String[]> facultyType = restTemplate.postForEntity(usersUrl
                + "/faculty", new NetId(authManager.getNetId()), String[].class);

        List<String> faculties = Arrays.asList(facultyType.getBody());
        System.out.println(faculties);

        List<FacultyResource> res = new ArrayList<>();

        for (String f : faculties) {
            FacultyResource facultyResources = repo.getFreeResources(f, LocalDate.now().plusDays(1).toString()).get();
            res.add(facultyResources);
        }
        return ResponseEntity.ok(res);
    }
}

