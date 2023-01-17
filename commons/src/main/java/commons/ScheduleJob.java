package commons;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleJob {
    private  long jobId;
    private  String faculty;
    private  LocalDate scheduleBefore;
    private  int cpuUsage;
    private  int gpuUsage;
    private  int memoryUsage;
}
