package nl.tudelft.sem.template.example.domain;


import commons.Job;
import commons.NetId;
import org.springframework.stereotype.Service;

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
}
