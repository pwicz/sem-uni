package nl.tudelft.sem.template.example.controllers;

import java.util.List;
import java.util.Optional;

import nl.tudelft.sem.template.example.Job;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobController {

    private final JobRepository repository;
    private final transient AuthManager authManager;


    /**
     * Constructor of the Job controller.
     *
     * @param repository the job database
     * @param authManager
     */
    @Autowired
    public JobController(JobRepository repository, AuthManager authManager) {
        this.repository = repository;
        this.authManager = authManager;
    }

    @PostMapping(path = "testAdd")
    public ResponseEntity<Job> testAdd() {
        Job job = new Job();
        Job saved = repository.save(job);
        return ResponseEntity.ok(saved);
    }

    @GetMapping(path = "getAllJobs")
    public ResponseEntity<List<Job>> getAllJobs() {
        List<Job> list = repository.findAll();
        return ResponseEntity.ok(list);
    }

    /**
     * The api POST endpoint to add a Job.
     *
     * @param job the Job that is added to the database
     * @return 200 ok
     */
    @PostMapping("/addJob")
    public ResponseEntity<Job> addJob(@RequestBody Job job) {
        Job savedJob = repository.save(job);
        return ResponseEntity.ok(savedJob);
    }

    /**
     * The api POST endpoint to delete a Job.
     *
     * @param jobId the jobId which identifies the job that needs to be deleted
     */
    @PostMapping("/deleteJob")
    public void deleteJob(@RequestBody long jobId) {
        Optional<Job> optionalJob = repository.findById(jobId);
        if (optionalJob.isEmpty()) {
            return;
        }
        Optional<Job> p = repository.findById(jobId);
        repository.deleteById(jobId);
    }


}
