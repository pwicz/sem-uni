package nl.tudelft.sem.template.example.domain.db;

import java.time.LocalDate;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@NoArgsConstructor
public class ScheduledInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long jobId;
    private String faculty;
    private int cpuUsage;
    private int gpuUsage;
    private int memoryUsage;
    private LocalDate date;

    @CreationTimestamp
    private Date createdAt;

    /**
     * Constructs scheduled instance.
     *
     * @param jobId ID of a job that uses the assigned resources.
     * @param faculty Name of a faculty whose resources are being used.
     * @param cpuUsage Usage of CPU resources.
     * @param gpuUsage Usage of GPU resources.
     * @param memoryUsage Usage of memory resources.
     * @param date Day on which the resources are being used.
     */
    public ScheduledInstance(Long jobId, String faculty, int cpuUsage, int gpuUsage, int memoryUsage, LocalDate date) {
        this.jobId = jobId;
        this.faculty = faculty;
        this.cpuUsage = cpuUsage;
        this.gpuUsage = gpuUsage;
        this.memoryUsage = memoryUsage;
        this.date = date;
    }

    public Long getId() {
        return id;
    }
}