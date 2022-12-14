package server;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/cluster")
public class NodeController {
    private final NodeRepository repo;
    private final transient AuthManager authManager;

    @Autowired
    public NodeController(NodeRepository repo, AuthManager authManager){
        this.repo = repo;
        this.authManager = authManager;
    }

    @GetMapping(path = {"/resources/{faculty}"})
    public ResponseEntity<Resource> getTotalResourcesForFaculty(@PathVariable("faculty") String faculty){
//        if (!repo.existsByFaculty(faculty)) {
//            return ResponseEntity.badRequest().build();
//        }
        List<Node> facultyNodes = repo.getNodesByFaculty(faculty).get();
        int cpu = 0;
        int gpu = 0;
        int mem = 0;
        for (Node n: facultyNodes) {
            cpu += n.getResource().getCPU();
            gpu += n.getResource().getGPU();
            mem += n.getResource().getMEM();
        }
        Resource r = new Resource(cpu, gpu, mem);
        return ResponseEntity.ok(r);
    }

    @GetMapping(path = {"/resources?faculty={faculty}&day={date}"})
    public ResponseEntity<Resource> getFacultyAvailableResourcesForDay(@PathVariable("faculty") String faculty, @PathVariable("date") String date){
//        if (!repo.existsByFaculty(faculty)) {
//            return ResponseEntity.badRequest().build();
//        }
        Resource facultyResources = repo.getFreeResources(faculty, date).get();
        return ResponseEntity.ok(facultyResources);
    }

    @PostMapping(path = {"/addNode"})
    public ResponseEntity<Node> addNode(@RequestBody Node node){
        //check if role is valid
        //check for if url looks like url
        if (node.getName()==null || node.getUrl()==null || node.getFaculty()==null ||
                node.getToken()==null || node.getResource() == null){
            return ResponseEntity.badRequest().build();
        }
        if (node.getResource().getCPU()<node.getResource().getGPU() || node.getResource().getCPU()<node.getResource().getMEM()){
            return ResponseEntity.badRequest().build();
        }
        if (!node.getName().equals(authManager.getNetId())){
            return ResponseEntity.badRequest().build();
        }
        try {
            Node newNode = repo.save(node);
            return ResponseEntity.ok(newNode);
        } catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/releaseFaculty?faculty={faculty}&date={date}&days={days}")
    public ResponseEntity<String> releaseFaculty(@PathVariable("faculty") String faculty, @PathVariable("date") LocalDate date, @PathVariable("days") int days){
        if (date == null || faculty == null || date.isBefore(LocalDate.now()) || days<1){
            return ResponseEntity.badRequest().build();
        }
        repo.updateRelease(faculty, date.toString(), days);
        return ResponseEntity.ok("Released");
    }

    //TODO:
    //@DeleteMapping("/delete/{id}")
    //hella complicated

//    public Question getRandomQuestion() {
//        return ClientBuilder.newClient(new ClientConfig()) //
//                .target(serverURL).path("api/questions/random") // the URL path which we HTTP GET for comparative questions
//                .request(APPLICATION_JSON) //
//                .accept(APPLICATION_JSON) //
//                .get(new GenericType<>() {
//                });

}
