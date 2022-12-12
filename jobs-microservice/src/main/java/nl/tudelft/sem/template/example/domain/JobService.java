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

    public Job createJob(NetId netId, String resourceType, int cpuUsage, int gpuUsage, int memoryUsage) throws Exception {
        if (cpuUsage < 0 || gpuUsage < 0 || memoryUsage < 0) {
            throw new
        }
    }
}
