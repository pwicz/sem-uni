package commons;

import java.time.LocalDate;

public class FacultyTotalResource extends FacultyResource {
    private final int cpuUsageTotal;
    private final int gpuUsageTotal;
    private final int memoryUsageTotal;

    /**
     * Constructs ScheduleJob object.
     *
     * @param faculty faculty which resources belong to
     * @param date date when the specified resources are available
     * @param cpuUsage the number of cpu units availabe
     * @param gpuUsage the number of gpu units availabe
     * @param memoryUsage the number of memory units availabe
     * @param cpuUsageT the total number of cpu units
     * @param gpuUsageT the total number of cpu units
     * @param memoryUsageT the total number of cpu units
     *
     */
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
