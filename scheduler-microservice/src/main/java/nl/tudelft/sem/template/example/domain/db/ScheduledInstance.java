package nl.tudelft.sem.template.example.domain.db;

import java.time.LocalDate;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@NoArgsConstructor
public class ScheduledInstance {
    @Id
    private Long id;

    private Long jobId;
    private String faculty;
    private int cpuUsage;
    private int gpuUsage;
    private int memoryUsage;
    private LocalDate date;

    @CreationTimestamp
    private Date createdAt;
}
