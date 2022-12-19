package commons;

import java.time.LocalDate;

public class FacultyResource {
    private final String faculty;
    private final LocalDate date;
    private final int cpuUsage;
    private final int gpuUsage;
    private final int memoryUsage;
    private final int cpuUsageTotal;
    private final int gpuUsageTotal;
    private final int memoryUsageTotal;

    public int getCpuUsageTotal() {
        return cpuUsageTotal;
    }

    public int getGpuUsageTotal() {
        return gpuUsageTotal;
    }

    public int getMemoryUsageTotal() {
        return memoryUsageTotal;
    }

    /**
     * Constructs ScheduleJob object.
     *
     * @param faculty faculty which resources belong to
     * @param date date when the specified resources are available
     * @param cpuUsage the number of cpu units needed
     * @param gpuUsage the number of gpu units needed
     * @param memoryUsage the number of memory units needed
     */
    public FacultyResource(String faculty, LocalDate date, int cpuUsage, int gpuUsage, int memoryUsage) {
        this.faculty = faculty;
        this.date = date;
        this.cpuUsage = cpuUsage;
        this.gpuUsage = gpuUsage;
        this.memoryUsage = memoryUsage;

        this.cpuUsageTotal = cpuUsage;
        this.gpuUsageTotal = gpuUsage;
        this.memoryUsageTotal = memoryUsage;
    }

    /**
     * Constructs ScheduleJob object.
     *
     * @param faculty faculty which resources belong to
     * @param date date when the specified resources are available
     * @param cpuUsage the number of cpu units needed
     * @param gpuUsage the number of gpu units needed
     * @param memoryUsage the number of memory units needed
     * @param cpuUsageT the number of cpu units overall
     * @param gpuUsageT the number of gpu units overall
     * @param memoryUsageT the number of memory units overall
     */
    public FacultyResource(String faculty, LocalDate date, int cpuUsage, int gpuUsage, int memoryUsage,
                           int cpuUsageT, int gpuUsageT, int memoryUsageT) {
        this.faculty = faculty;
        this.date = date;
        this.cpuUsage = cpuUsage;
        this.gpuUsage = gpuUsage;
        this.memoryUsage = memoryUsage;

        this.cpuUsageTotal = cpuUsageT;
        this.gpuUsageTotal = gpuUsageT;
        this.memoryUsageTotal = memoryUsageT;
    }

    public String getFaculty() {
        return faculty;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getCpuUsage() {
        return cpuUsage;
    }

    public int getGpuUsage() {
        return gpuUsage;
    }

    public int getMemoryUsage() {
        return memoryUsage;
    }
}
