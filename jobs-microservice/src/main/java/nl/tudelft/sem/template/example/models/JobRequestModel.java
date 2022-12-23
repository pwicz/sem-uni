package nl.tudelft.sem.template.example.models;

import lombok.Data;

/**
 * Request model for the Job class.
 */
@Data
public class JobRequestModel {
    private String netId;
    private String description;
    private int cpuUsage;
    private int gpuUsage;
    private int memoryUsage;
}
