package commons;

import java.time.LocalDate;
import lombok.Data;

@Data
public class FacultyTotalResource {
    private String faculty;
    private LocalDate date;
    private int cpuUsageTotal;
    private int gpuUsageTotal;
    private int memoryUsageTotal;
    private int cpuUsage;
    private int gpuUsage;
    private int memoryUsage;
}
