package commons;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "jobs")
@NoArgsConstructor
public class Job {

    @Id
    @Column(name = "job_id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long jobId;

    @Column(name = "net_id", nullable = false)
    @Convert(converter = NetIdAttributeConverter.class)
    private NetId netId;

    @Column(name = "cpu_usage", nullable = false)
    private int cpuUsage;

    @Column(name = "gpu_usage", nullable = false)
    private int gpuUsage;

    @Column(name = "memory_usage", nullable = false)
    private int memoryUsage;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "scheduleDate", nullable = false)
    private Optional<LocalDate> optionalScheduleDate;


    /**
     * Constructor for the Job class, which represents jobs that need to be done.
     *
     * @param netId the netId of the user creating the job
     * @param cpuUsage the amount of cpu units needed
     * @param gpuUsage the amount of gpu units needed
     * @param memoryUsage the amount of memory units needed
     */
    public Job(NetId netId, int cpuUsage, int gpuUsage, int memoryUsage) {
        this.netId = netId;
        this.cpuUsage = cpuUsage;
        this.gpuUsage = gpuUsage;
        this.memoryUsage = memoryUsage;
        this.status = "pending";
        this.optionalScheduleDate = Optional.empty();
    }

    /**
     * TEST CONSTRUCTOR.
     */
    public Job(int temp) {
        this.netId = new NetId("test");
        cpuUsage = 0;
        gpuUsage = 0;
        memoryUsage = 0;
        this.status = "accept";
        this.optionalScheduleDate = Optional.of(LocalDate.now().plusDays(3));
    }


    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public NetId getNetId() {
        return netId;
    }

    public void setNetId(NetId netId) {
        this.netId = netId;
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

    public LocalDate getScheduleDate() {
        return optionalScheduleDate.orElse(null);
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.optionalScheduleDate = Optional.of(scheduleDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Job job = (Job) o;
        return jobId == job.jobId && cpuUsage == job.cpuUsage && gpuUsage == job.gpuUsage && memoryUsage == job.memoryUsage
                && Objects.equals(netId, job.netId) && Objects.equals(status, job.status)
                && Objects.equals(optionalScheduleDate, job.optionalScheduleDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId, netId, cpuUsage, gpuUsage, memoryUsage, status);
    }
}
