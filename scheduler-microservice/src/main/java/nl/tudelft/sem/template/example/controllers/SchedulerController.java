package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.domain.Job;
import nl.tudelft.sem.template.example.domain.processing.ProcessingJobsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SchedulerController {

    private final transient ProcessingJobsService processingJobsService;

    @Autowired
    public SchedulerController(ProcessingJobsService processingJobsService){
        this.processingJobsService = processingJobsService;
    }

    @PostMapping("/schedule")
    public ResponseEntity<String> scheduleJob(@RequestBody Job job){
        // 1. Send the job to the processing queue
        processingJobsService.addToQueue(job);

        return ResponseEntity.ok("Processing");
    }
}
