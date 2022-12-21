package commons;

import java.time.LocalDate;

public class FacultyTotalResource extends FacultyResource{
    private final int cpuUsageTotal;
    private final int gpuUsageTotal;
    private final int memoryUsageTotal;

    public FacultyTotalResource(String faculty, LocalDate date, int cpuUsage, int gpuUsage, int memoryUsage,
                                int cpuUsageT, int gpuUsageT, int memoryUsageT) {
        super(faculty, date, cpuUsage, gpuUsage, memoryUsage);
        this.cpuUsageTotal = cpuUsageT;
        this.gpuUsageTotal = gpuUsageT;
        this.memoryUsageTotal = memoryUsageT;
    }

    public int getCpuUsageTotal() {
        return cpuUsageTotal;
    }

    public int getGpuUsageTotal() {
        return gpuUsageTotal;
    }

    public int getMemoryUsageTotal() {
        return memoryUsageTotal;
    }
}
