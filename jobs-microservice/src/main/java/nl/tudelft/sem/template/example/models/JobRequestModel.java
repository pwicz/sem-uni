package nl.tudelft.sem.template.example.models;

import commons.NetId;
import lombok.Data;

/**
 * Request model for the Job class.
 */
@Data
public class JobRequestModel {

    private NetId netId;
    private String resourceType;
    private int cpuUsage;
    private int gpuUsage;
    private int memoryUsage;

    /**
     * Constructor for the request model when creating a job.
     * @param netId netId of the job issuer
     * @param resourceType resource type
     * @param cpuUsage CPU usage of the job.
     * @param gpuUsage GPU usage of the job.
     * @param memoryUsage memory usage of the job.
     */
    public JobResponseModel(NetId netId, String resourceType, int cpuUsage, int gpuUsage, int memoryUsage) {
        this.netId = netId;
        this.resourceType = resourceType;
        this.cpuUsage = cpuUsage;
        this.gpuUsage = gpuUsage;
        this.memoryUsage = memoryUsage;
    }
}
