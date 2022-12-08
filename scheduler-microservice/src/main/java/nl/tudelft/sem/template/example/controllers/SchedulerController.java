package nl.tudelft.sem.template.example.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// DUMMY CLASS: must be removed and replaced with the real Job
// class that is also used in the Jobs controller
class Job{
    int id;
}

@RestController
public class SchedulerController {

    @PostMapping("/schedule")
    public ResponseEntity<String> scheduleJob(@RequestBody Job job){
        // 1. Send the job to the processing queue

        return ResponseEntity.ok("Processing");
    }
}
