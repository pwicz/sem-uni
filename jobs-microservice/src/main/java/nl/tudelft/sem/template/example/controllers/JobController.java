package nl.tudelft.sem.template.example.controllers;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;

import nl.tudelft.sem.template.example.Job;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobController {

    private final transient JobRepository repository;
    private final transient AuthManager authManager;


    /**
     * Constructor of the Job controller.
     *
     * @param repository the job database
     * @param authManager Spring Security component used to authenticate and authorize the user
     */
    @Autowired
    public JobController(JobRepository repository, AuthManager authManager) {
        this.repository = repository;
        this.authManager = authManager;
    }

    /**
     * The api POST endpoint to test adding a Job.
     *
     * @return 200 ok
     */
    @PostMapping(path = "testAdd")
    public ResponseEntity<Job> testAdd() {
        Job job = new Job();
        Job saved = repository.save(job);
        return ResponseEntity.ok(saved);
    }

    /**
     * The api GET endpoint to get all Jobs in the database.
     *
     * @return list of Jobs to be scheduled
     */
    @GetMapping(path = "getAllJobs")
    public ResponseEntity<List<Job>> getAllJobs() {
        List<Job> list = repository.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping(path = "/jobStatus")
    public ResponseEntity<Job> getJobStatusById(@RequestBody UUID uuid) {
        Optional<Job> job = repository.findById(uuid);
        return job.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(job.get());
    }

    @GetMapping(path = "/getJobsNetID")
    public ResponseEntity<List<Job>> getAllNetIDJobs(@RequestBody NetID netID) {
        Optional<List<Job>> jobs = repository.findAllByNetID(netID);
        return jobs.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(jobs.get());
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
        repository.deleteById(jobId);
    }


}
