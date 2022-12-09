package nl.tudelft.sem.template.example;


import javax.persistence.*;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @Column(name = "job_id", nullable = false, unique = true)
    private long jobId;
    @Column(name = "net_id", nullable = false)
    @Convert(converter = NetIdAttributeConverter.class)
    private String netId;

    @Column(name = "resourceType", nullable = false)
    private String resourceType;

    @Column(name = "cpu_usage", nullable = false)
    private int cpuUsage;

    @Column(name = "gpu_usage", nullable = false)
    private int gpuUsage;

    @Column(name = "memory_usage", nullable = false)
    private int memoryUsage;

    @Column(name = "status", nullable = false)
    private String status;


    /**
     * Constructor for the Job class, which represents jobs that need to be done.
     *
     * @param netId the netId of the user creating the job
     * @param resourceType the type of resource needed to execute the job
     * @param cpuUsage the amount of cpu units needed
     * @param gpuUsage the amount of gpu units needed
     * @param memoryUsage the amount of memory units needed
     */
    public Job(String netId, String resourceType, int cpuUsage, int gpuUsage, int memoryUsage) {
        this.netId = netId;
        this.resourceType = resourceType;
        this.cpuUsage = cpuUsage;
        this.gpuUsage = gpuUsage;
        this.memoryUsage = memoryUsage;
    }

    /**
     * TEST CONSTRUCTOR.
     */
    public Job() {
        this.netId = "TEST";
        this.resourceType = "TYPE";
        cpuUsage = 0;
        gpuUsage = 0;
        memoryUsage = 0;
    }


    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
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

    public int getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(int cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public int getGpuUsage() {
        return gpuUsage;
    }

    public void setGpuUsage(int gpuUsage) {
        this.gpuUsage = gpuUsage;
    }

    public int getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(int memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
