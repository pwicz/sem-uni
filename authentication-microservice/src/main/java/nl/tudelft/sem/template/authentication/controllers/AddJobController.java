package nl.tudelft.sem.template.authentication.controllers;

import nl.tudelft.sem.template.authentication.domain.user.AddJobService;
import nl.tudelft.sem.template.authentication.models.AddJobRequestModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

//class Job {
//    private String netId;
//    private String resourceType;
//    private int cpuUsage;
//    private int gpuUsage;
//    private int memoryUsage;
//}

@RestController
public class AddJobController {
//    @PostMapping("/add")
//    public ResponseEntity<String> addJob(@RequestBody Job job) {
//        return ResponseEntity.ok("Adding job...");
//    }

    private final transient AddJobService addJobService;

    public AddJobController(AddJobService addJobService) {
        this.addJobService = addJobService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addJob(@RequestBody AddJobRequestModel job) {
        return ResponseEntity.ok("Adding job...");
    }
}
