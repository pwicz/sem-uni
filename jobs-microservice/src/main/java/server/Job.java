package server;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Job {

    private static long id;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long jobId;
    private String netId;
    private String resourceType;
    private int CPUusage;
    private int GPUusage;
    private int memoryUsage;

    public Job(String netId, String resourceType, int CPUusage, int GPUusage, int memoryUsage) {
        this.jobId = id++;
        this.netId = netId;
        this.resourceType = resourceType;
        this.CPUusage = CPUusage;
        this.GPUusage = GPUusage;
        this.memoryUsage = memoryUsage;
    }

    public static long getId() {
        return id;
    }

    public long getJobId() {
        return jobId;
    }

    public String getNetId() {
        return netId;
    }

    public void setNetId(String netId) {
        this.netId = netId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public int getCPUusage() {
        return CPUusage;
    }

    public void setCPUusage(int CPUusage) {
        this.CPUusage = CPUusage;
    }

    public int getGPUusage() {
        return GPUusage;
    }

    public void setGPUusage(int GPUusage) {
        this.GPUusage = GPUusage;
    }

    public int getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(int memoryUsage) {
        this.memoryUsage = memoryUsage;
    }
}
