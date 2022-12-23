package nl.tudelft.sem.template.example.domain;

import commons.Faculty;
import commons.Job;
import commons.NetId;
import commons.RoleValue;
import commons.ScheduleJob;
import commons.Status;
import exceptions.InvalidIdException;
import exceptions.InvalidNetIdException;
import exceptions.InvalidResourcesException;
import exceptions.ResourceBiggerThanCpuException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.example.models.JobIdRequestModel;
import nl.tudelft.sem.template.example.models.JobResponseModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * A DDD service for handling jobs.
 */

@Service
public class JobService {
    private final transient JobRepository jobRepository;
    private final transient RestTemplate restTemplate;
    private static final String nullValue = "null";

    private final transient String schedulerUrl = "http://localhost:8084";
    private final transient String url = "http://localhost:8083";

    /**
     * Instantiates a new JobService.
     *
     * @param jobRepository               the job repository
     * @param restTemplate                the template to make REST API calls
     */
    public JobService(JobRepository jobRepository, RestTemplate restTemplate) {
        this.jobRepository = jobRepository;
        this.restTemplate = restTemplate;
    }

    /**
     * Makes a POST request to the Scheduler, to schedule Jobs.
     *
     * @param scheduleJob the Job object to be scheduled
     * @return the response message of the Scheduler
     * @throws InvalidScheduleJobException if scheduleJob is null
     */
    public String scheduleJob(ScheduleJob scheduleJob) throws InvalidScheduleJobException, ResponseEntityException {
        if (scheduleJob == null) {
            throw new InvalidScheduleJobException(null);
        }

        ResponseEntity<String> response = restTemplate
                .postForEntity(schedulerUrl + "/schedule", scheduleJob, String.class);

        if (response.getBody() == null) {
            throw new ResponseEntityException();
        }
        return response.getBody();
    }

    /**
     * Makes a POST request to the Scheduler to unschedule Jobs.
     *
     * @param jobId - the id of the job to be unscheduled
     * @return the response message of the scheduler
     * @throws ResponseEntityException - exception that handles empty response from the Scheduler
     */
    public String unscheduleJob(JobIdRequestModel jobId) throws ResponseEntityException {
        ResponseEntity<String> response = restTemplate
                .postForEntity(schedulerUrl + "/unschedule", jobId, String.class);

        if (response.getBody() == null) {
            throw new ResponseEntityException();
        }
        return response.getBody();
    }

    /**
     * Create a new job.
     *
     * @param netId NetId of the job creator
     * @param authNetId NetId of the authenticated user
     * @param cpuUsage CPU usage
     * @param gpuUsage GPU usage
     * @param memoryUsage memory usage
     * @return a new Job
     * @throws Exception if the resources of NetId are invalid
     */

    public Job createJob(NetId netId, NetId authNetId, Faculty faculty, int cpuUsage, int gpuUsage,
                         int memoryUsage, RoleValue role, LocalDate preferredDate) throws Exception {
        if (cpuUsage < 0 || gpuUsage < 0 || memoryUsage < 0) {
            throw new InvalidResourcesException(Math.min(cpuUsage, Math.min(gpuUsage, memoryUsage)));
        }
        if (cpuUsage < Math.max(gpuUsage, memoryUsage)) {
            String resource = gpuUsage > memoryUsage ? "GPU" : "Memory";
            throw new ResourceBiggerThanCpuException(resource);
        }
        if (netId == null) {
            throw new InvalidNetIdException(nullValue);
        }
        if (!netId.toString().equals(authNetId.toString())) {
            throw new InvalidNetIdException(netId.toString());
        }
        if (!role.equals(RoleValue.EMPLOYEE)) {
            System.out.println(role);
            throw new BadCredentialsException(role.toString());
        }

        Job newJob = new Job(netId, faculty, cpuUsage, gpuUsage, memoryUsage, preferredDate);
        jobRepository.save(newJob);

        return newJob;
    }

    /**
     * Create a new job.
     *
     * @param authNetId NetId of the authenticated user
     * @param job a job
     * @param role a role of a user who creates a job
     * @return a new Job
     * @throws Exception if the resources of NetId are invalid
     */
    public Job createJob(NetId authNetId, Job job, RoleValue role) throws Exception {
        if (job.getCpuUsage() < 0 || job.getGpuUsage() < 0 || job.getMemoryUsage() < 0) {
            throw new InvalidResourcesException(Math.min(job.getCpuUsage(),
                    Math.min(job.getGpuUsage(), job.getMemoryUsage())));
        }
        if (job.getNetId() == null) {
            throw new InvalidNetIdException(nullValue);
        }
        if (!job.getNetId().toString().equals(authNetId.toString())) {
            throw new InvalidNetIdException(job.getNetId().toString());
        }
        if (!role.equals(RoleValue.EMPLOYEE)) {
            System.out.println(role);
            throw new BadCredentialsException(role.toString());
        }

        jobRepository.save(job);

        return job;
    }

    /**
     * Remove a job from the database.
     *
     * @param id the unique id of the Job.
     * @throws Exception if there is no Job with the provided id.
     */
    public void deleteJob(long id) throws Exception {
        if (!jobRepository.existsById(id)) {
            throw new InvalidIdException(id);
        }
        jobRepository.deleteById(id);
    }

    /**
     * Collect all the jobs in the database created by a specific user.
     *
     * @param netId NetId of the request creator
     * @param authNetId NetId of the authenticated user
     * @return a list of Job corresponding to the NetId provided
     * @throws Exception if the NetId is invalid or there is no associated Job to the NetId
     */
    public List<Job> collectJobsByNetId(NetId netId, NetId authNetId) throws Exception {
        if (netId == null) {
            throw new InvalidNetIdException(nullValue);
        }
        Optional<List<Job>> jobs = jobRepository.findAllByNetId(netId);
        if (jobs.isEmpty() || !netId.toString().equals(authNetId.toString())) {
            throw new InvalidNetIdException(netId.toString());
        }
        return jobs.get();
    }

    /**
     * Retrieve the status of a specific Job stored in the database.
     *
     * @param netId NetId of the request creator
     * @param authNetId NetId of the authenticated user
     * @param jobId the unique id of the Job
     * @return a String with the status of the Job
     * @throws Exception if the NetId is invalid or the NetId does not have permission to access the requested job.
     */
    public Status getJobStatus(NetId netId, NetId authNetId, long jobId) throws Exception {
        if (netId == null) {
            throw new InvalidNetIdException(nullValue);
        }

        Optional<Job> job = jobRepository.findById(jobId);
        if (job.isEmpty()) {
            throw new InvalidIdException(jobId);
        }
        if (!netId.toString().equals(authNetId.toString()) || !job.get().getNetId().toString().equals(netId.toString())) {
            throw new InvalidNetIdException(netId.toString());
        }
        return job.get().getStatus();
    }

    /**
     * Retrieve all the Job entities from the database.
     *
     * @param netId NetId of the request creator
     * @param authNetId NetId of the authenticated user
     * @param role role of the request creator
     * @return a list of Job entities containing all jobs in the database.
     * @throws Exception if the NetId is invalid or the creator of the request does not have the admin role.
     */
    public List<Job> getAllJobs(NetId netId, NetId authNetId, String role) throws Exception {
        if (netId == null) {
            throw new InvalidNetIdException(nullValue);
        }
        if (!netId.toString().equals(authNetId.toString())) {
            throw new InvalidNetIdException(netId.toString());
        }
        if (!role.equals("admin")) {
            throw new BadCredentialsException(role);
        }
        return jobRepository.findAll();
    }


    /**
     * Retrieve all the Job entities from the database.
     *
     * @param netId NetId of the request creator
     * @param authNetId NetId of the authenticated user
     * @param role role of the request creator
     * @return a list of Job entities containing all "ACCEPTED" jobs in the database.
     * @throws Exception if the NetId is invalid or the creator of the request does not have the admin role.
     */
    public List<Job> getAllScheduledJobs(NetId netId, NetId authNetId, String role) throws Exception {
        if (netId == null) {
            throw new InvalidNetIdException(nullValue);
        }
        if (!netId.toString().equals(authNetId.toString())) {
            throw new InvalidNetIdException(netId.toString());
        }
        if (!role.equals("admin")) {
            throw new BadCredentialsException(role);
        }
        return jobRepository.findAll().stream().filter(j -> j.getStatus() == Status.ACCEPTED).collect(Collectors.toList());
    }

    /**
     * Update information about the Job specified by a microservice.
     *
     * @param id id of the Job
     * @param status the new status of the Job
     * @param localDate the time the Job is scheduled to start
     * @throws Exception if the id does not exist in the database
     */
    public void updateJob(long id, Status status, LocalDate localDate) throws Exception {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if (jobOptional.isEmpty()) {
            throw new InvalidIdException(id);
        }
        Job job = jobOptional.get();
        job.setStatus(status);
        job.setPreferredDate(localDate);
        jobRepository.save(job);
    }

    /**
     * Populate a JobResponseModel DTO.
     *
     * @param id id of the Job
     * @param status status of the Job
     * @param netId netId of the user that created the Job
     * @return JobResponseModel
     */
    public JobResponseModel populateJobResponseModel(long id, Status status, String netId) {
        JobResponseModel jobResponseModel = new JobResponseModel();
        jobResponseModel.setId(id);
        jobResponseModel.setStatus(status);
        jobResponseModel.setNetId(netId);
        return jobResponseModel;
    }
}
