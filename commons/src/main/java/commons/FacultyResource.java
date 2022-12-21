package commons;

import java.time.LocalDate;

public class FacultyResource {
    private final String faculty;
    private final LocalDate date;
    private final int cpuUsage;
    private final int gpuUsage;
    private final int memoryUsage;
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
