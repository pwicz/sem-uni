package nl.tudelft.sem.template.authentication.models;

import lombok.Data;

/**
 * Model representing an add job request.
 */
@Data
public class AddJobRequestModel {
    private String netId;
    private String resourceType;
    private int cpuUsage;
    private int gpuUsage;
    private int memoryUsage;
}
