package server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cluster")
public class NodeController {
    private final NodeRepository repo;

    @Autowired
    public NodeController(NodeRepository repo){
        this.repo = repo;
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

    @ResponseBody @RequestMapping(path = {"/resources/day/{faculty}"})
    public ResponseEntity<Resource> getFacultyAvailableResourcesForDay(@PathVariable("faculty") String faculty, @RequestBody LocalDate date){
//        if (!repo.existsByFaculty(faculty)) {
//            return ResponseEntity.badRequest().build();
//        }
        List<Node> facultyNodes = repo.getFreeResources(faculty, date.toString()).get();
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

}
