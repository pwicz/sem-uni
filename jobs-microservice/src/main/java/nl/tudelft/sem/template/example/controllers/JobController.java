package nl.tudelft.sem.template.example.controllers;

import commons.Job;
import commons.NetId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.InvalidNetIdException;
import nl.tudelft.sem.template.example.domain.InvalidResourcesException;
import nl.tudelft.sem.template.example.domain.JobRepository;
import nl.tudelft.sem.template.example.domain.JobService;
import nl.tudelft.sem.template.example.models.JobRequestModel;
import nl.tudelft.sem.template.example.models.JobResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class JobController {

    private final transient JobRepository repository;
    private final transient AuthManager authManager;
    private final transient JobService jobService;


    /**
     * Constructor of the Job controller.
     *
     * @param repository the job database
     * @param authManager Spring Security component used to authenticate and authorize the user
     */
    @Autowired
    public JobController(JobRepository repository, AuthManager authManager, JobService jobService) {
        this.repository = repository;
        this.authManager = authManager;
        this.jobService = jobService;
    }

    /**
     * The api POST endpoint to test adding a Job.
     *
     * @return 200 ok
     */
    @PostMapping(path = "testAdd")
    public ResponseEntity<JobResponseModel> testAdd() {
        Job job = new Job(1);
        repository.save(job);
        return ResponseEntity.ok().build();
    }

    /**
     * The api GET endpoint to get all Jobs in the database.
     *
     * @return list of Jobs to be scheduled
     */
    @GetMapping(path = "/getAllJobs")
    public ResponseEntity<List<JobResponseModel>> getAllJobs() {
        List<Job> list = repository.findAll();
        List<JobResponseModel> rm = list.stream().map(
                x -> new JobResponseModel(x.getNetId().toString(), x.getStatus())).collect(Collectors.toList());
        return ResponseEntity.ok(rm);
    }

    /**
     * The api GET endpoint to get the status of the requested Job.
     *
     * @param id the id of the Job
     * @return status of the job
     */
    @GetMapping(path = "/jobStatus")
    public ResponseEntity<JobResponseModel> getJobStatusById(@RequestBody long id) {
        Optional<Job> job = repository.findById(id);
        if (job.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new JobResponseModel(job.get().getNetId().toString(), job.get().getStatus()));
    }

    /**
     * The api GET endpoint to get all Jobs belonging to the given netId (user).
     *
     * @param netId the netId of the user
     * @return list of Jobs belonging to the given netId (user)
     */
    @GetMapping(path = "/getJobsNetId")
    public ResponseEntity<List<JobResponseModel>> getAllNetIdJobs(@RequestBody NetId netId) {
        Optional<List<Job>> jobs = repository.findAllByNetId(netId);
        if (jobs.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<JobResponseModel> rm = jobs.get().stream().map(
                x -> new JobResponseModel(x.getNetId().toString(), x.getStatus())).collect(Collectors.toList());
        return ResponseEntity.ok(rm);
    }

    /**
     * The API POST endpoint to create a Job using the JobRequestModel.
     *
     * @param request the parameters used to create a new job.
     * @return 200 ok
     */
    @PostMapping("/addJob")
    public ResponseEntity<JobResponseModel> addJob(@RequestBody JobRequestModel request) {

        try {
            NetId jobNetId = new NetId(request.getNetId());
            NetId authNetId = new NetId(authManager.getNetId());
            String resourceType = request.getResourceType();
            int cpuUsage = request.getCpuUsage();
            int gpuUsage = request.getGpuUsage();
            int memoryUsage = request.getMemoryUsage();

            Job createdJob = this.jobService.createJob(jobNetId, authNetId, resourceType, cpuUsage, gpuUsage, memoryUsage);

            JobResponseModel jobResponseModel = new JobResponseModel(createdJob.getNetId().toString(), "pending approval");

            return ResponseEntity.ok(jobResponseModel);
        }
        catch (InvalidNetIdException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "INVALID_ID", e);
        }
        catch (InvalidResourcesException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_RESOURCE_ALLOCATION", e);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "EXCEPTION", e);
        }
    }

    /**
     * The api POST endpoint to delete a Job.
     *
     * @param jobId the jobId which identifies the job that needs to be deleted
     */
    @PostMapping("/deleteJob")
    public ResponseEntity<JobResponseModel> deleteJob(@RequestBody long jobId) {
        Optional<Job> optionalJob = repository.findById(jobId);
        if (optionalJob.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(jobId);
        return ResponseEntity.ok().build();
    }


}
