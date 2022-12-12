package nl.tudelft.sem.template.example.domain;


import commons.Job;
import commons.NetId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * A DDD service for handling jobs.
 */

@Service
public class JobService {


    private final transient JobRepository jobRepository;

    /**
     * Instantiates a new JobService.
     *
     * @param jobRepository the job repository
     */
    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    /**
     * Create a new job.
     *
     * @param netId NetId of the job creator
     * @param authNetId NetId of the authenticated user
     * @param resourceType resource type
     * @param cpuUsage CPU usage
     * @param gpuUsage GPU usage
     * @param memoryUsage memory usage
     * @return a new Job
     * @throws Exception if the resources of NetId are invalid
     */
    public Job createJob(NetId netId, NetId authNetId, String resourceType, int cpuUsage, int gpuUsage, int memoryUsage) throws Exception {
        if (cpuUsage < 0 || gpuUsage < 0 || memoryUsage < 0) {
            throw new InvalidResourcesException(Math.min(cpuUsage, Math.min(gpuUsage, memoryUsage)));
        }
        if (netId == null) {
            throw new InvalidNetIdException("null");
        }
        if (!netId.toString().equals(authNetId.toString())) {
            throw new InvalidNetIdException(netId.toString());
        }

        Job newJob = new Job(netId, resourceType, cpuUsage, gpuUsage, memoryUsage);
        jobRepository.save(newJob);

        return newJob;
    }

    /**
     * Remove a job from the database.
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
            throw new InvalidNetIdException("null");
        }
        Optional<List<Job>> jobs = jobRepository.findAllByNetId(netId);
        if (jobs.isEmpty() || !netId.toString().equals(authNetId.toString())) {
            throw new InvalidNetIdException(netId.toString());
        }
        return jobs.get();
    }
}
