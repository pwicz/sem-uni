package nl.tudelft.sem.template.example.controllers;

import commons.Job;
import commons.NetId;
import commons.Role;
import commons.RoleValue;
import commons.Status;
import commons.UpdateJob;
import exceptions.InvalidIdException;
import exceptions.InvalidNetIdException;
import exceptions.InvalidResourcesException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.JobRepository;
import nl.tudelft.sem.template.example.domain.JobService;
import nl.tudelft.sem.template.example.models.IdRequestModel;
import nl.tudelft.sem.template.example.models.JobRequestModel;
import nl.tudelft.sem.template.example.models.JobResponseModel;
import nl.tudelft.sem.template.example.models.NetIdRequestModel;
import nl.tudelft.sem.template.example.models.StatusResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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

    private static final String invalidId = "INVALID_ID";


    /**
     * Constructor of the Job controller.
     *
     * @param repository  the job database
     * @param jobService  the service which handles the communication with the database
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
    public ResponseEntity<List<JobResponseModel>> getAllJobs() throws Exception {
        try {
            NetId netId = new NetId(authManager.getNetId());
            NetId authNetId = new NetId(authManager.getNetId());
            String role = authManager.getRole().toString();

            List<Job> jobs = this.jobService.getAllJobs(netId, authNetId, role);
            List<JobResponseModel> responseModels = jobs.stream()
                    .map(x -> jobService.populateJobResponseModel(x.getJobId(), x.getStatus(), x.getNetId().toString()))
                .collect(Collectors.toList());
            return ResponseEntity.ok(responseModels);
        } catch (InvalidNetIdException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, invalidId, e);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "BAD_CREDENTIALS", e);
        }
    }

    /**
     * The api GET endpoint to get the status of the requested Job.
     *
     * @param request the id of a Job stored in the database.
     * @return status of the job
     */
    @GetMapping(path = "/jobStatus")
    public ResponseEntity<StatusResponseModel> getJobStatusById(@RequestBody IdRequestModel request) throws Exception {
        try {
            NetId authNetId = new NetId(authManager.getNetId());
            long jobId = request.getId();
            Status status = this.jobService.getJobStatus(authNetId, authNetId, jobId);
            StatusResponseModel statusResponseModel = new StatusResponseModel();
            statusResponseModel.setStatus(status.toString());
            return ResponseEntity.ok(statusResponseModel);
        } catch (InvalidNetIdException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, invalidId, e);
        }
    }

    /**
     * The api GET endpoint to get all Jobs belonging to the given netId (user).
     *
     * @param request the parameters for NetId
     * @return list of Jobs belonging to the given netId (user)
     */
    @GetMapping(path = "/getJobs")
    public ResponseEntity<List<JobResponseModel>> getJobsByNetId(@RequestBody NetIdRequestModel request) throws Exception {
        try {
            NetId netId = new NetId(request.getNetId());
            NetId authNetId = new NetId(authManager.getNetId());
            List<Job> jobs = this.jobService.collectJobsByNetId(netId, authNetId);
            List<JobResponseModel> responseModels = jobs.stream()
                .map(x -> jobService.populateJobResponseModel(x.getJobId(), x.getStatus(), x.getNetId().toString()))
                .collect(Collectors.toList());

            return ResponseEntity.ok(responseModels);
        } catch (InvalidNetIdException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, invalidId, e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "EXCEPTION", e);
        }
    }

    /**
     * The API POST endpoint to create a Job using the JobRequestModel.
     *
     * @param request the parameters used to create a new job.
     * @return 200 ok
     */
    @PostMapping("/addJob")
    public ResponseEntity addJob(@RequestBody JobRequestModel request) throws Exception {

        try {
            NetId jobNetId = new NetId(request.getNetId());
            NetId authNetId = new NetId(authManager.getNetId());
            int cpuUsage = request.getCpuUsage();
            int gpuUsage = request.getGpuUsage();
            int memoryUsage = request.getMemoryUsage();
            Role role = authManager.getRole();
            //String role = (String) authManager.getRole();
            LocalDate preferredDate = LocalDate.now();
            Job createdJob = this.jobService.createJob(jobNetId, authNetId, cpuUsage,
                    gpuUsage, memoryUsage, role.getRoleValue(), preferredDate);

            JobResponseModel jobResponseModel = jobService.populateJobResponseModel(createdJob.getJobId(),
                Status.PENDING, createdJob.getNetId().toString());
            return ResponseEntity.ok(jobResponseModel);
        } catch (InvalidNetIdException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, invalidId, e);
        } catch (InvalidResourcesException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_RESOURCE_ALLOCATION", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
    
    /**
     * The api POST endpoint to delete a Job.
     *
     * @param jobId the jobId which identifies the job that needs to be deleted
     */
    @PostMapping("/deleteJob")
    public ResponseEntity deleteJob(@RequestBody long jobId) throws Exception {
        try {
            this.jobService.deleteJob(jobId);
        } catch (InvalidIdException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, invalidId, e);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * REST API post request to update the information about a Job.
     *
     * @param request the parameters to find and update
     * @return 200 HTTP CODE if everything works as planned
     */
    @PostMapping("/update")
    public ResponseEntity updateJob(@RequestBody UpdateJob request) throws Exception {
        try {
            long id = request.getId();
            Status status = Status.valueOf(request.getStatus());
            LocalDate localDate = request.getScheduleDate();

            this.jobService.updateJob(id, status, localDate);
        } catch (InvalidIdException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, invalidId, e);
        }
        return ResponseEntity.ok().build();
    }


    /**
     * Allow sysadmin to see all scheduled jobs for all days.
     *
     * @return response indicating if the operation was successful
     */
    @GetMapping(path = "/getAllScheduledJobs")
    public ResponseEntity<List<Job>> getAllScheduledJobs() throws Exception {
        try {
            NetId netId = new NetId(authManager.getNetId());
            NetId authNetId = new NetId(authManager.getNetId());
            String role = authManager.getRole().toString();

            List<Job> jobs = this.jobService.getAllScheduledJobs(netId, authNetId, role);
            return ResponseEntity.ok(jobs);
        } catch (InvalidNetIdException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, invalidId, e);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "BAD_CREDENTIALS", e);
        }
    }

}
