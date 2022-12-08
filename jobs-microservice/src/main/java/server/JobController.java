package server;

import java.util.Optional;

import nl.tudelft.sem.template.example.Job;
import nl.tudelft.sem.template.example.database.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class JobController {

    private final JobRepository repository;

    /**
     * Constructor of the Job controller.
     *
     * @param repository the job database
     */
    @Autowired
    public JobController(JobRepository repository) {
        this.repository = repository;
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
