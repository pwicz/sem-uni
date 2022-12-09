package nl.tudelft.sem.template.example.controllers;

import java.util.Optional;

import nl.tudelft.sem.template.example.Job;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.h2.util.StringUtils.isNullOrEmpty;

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

    @GetMapping("/get")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello " + authManager.getNetId());

    }

    @PostMapping(path = "add")
    public ResponseEntity<Object> add() {

        Job job = new Job();
        Job saved = repository.save(job);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/testAdd")
    public ResponseEntity<Job> testAdd() {
        Job newJob = new Job();
        Job savedJob = repository.save(newJob);
        return ResponseEntity.ok(savedJob);
    }

    @PostMapping("/testDelete")
    public void testDelete() {
        Optional<Job> optionalJob = repository.findById(-1L);
        if (optionalJob.isEmpty()) {
            return;
        }
        Optional<Job> p = repository.findById(-1L);
        repository.deleteById(-1L);
    }

    /**
     * The api POST endpoint to add a Job.
     *
     * @param netId the netId of the user creating the job
     * @param resourceType the type of resource needed to execute the job
     * @param cpuUsage the amount of cpu units needed
     * @param gpuUsage the amount of gpu units needed
     * @param memoryUsage the amount of memory units needed
     * @return 200 ok
     */
    @PostMapping("/addJob")
    public ResponseEntity<Job> addJob(@RequestBody String netId, @RequestBody String resourceType,
                                      @RequestBody int cpuUsage, @RequestBody int gpuUsage,
                                      @RequestBody int memoryUsage) {

        // TODO: check for authentication
        //return ResponseEntity.badRequest().build();

        Job newJob = new Job(netId, resourceType, cpuUsage, gpuUsage, memoryUsage);
        Job savedJob = repository.save(newJob);

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
