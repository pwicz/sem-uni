package nl.tudelft.sem.template.example.domain.strategies;

import commons.FacultyResource;
import commons.ScheduleJob;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.example.domain.ResourceGetter;
import nl.tudelft.sem.template.example.domain.db.ScheduledInstance;
import nl.tudelft.sem.template.example.domain.db.ScheduledInstanceRepository;

public class ScheduleOneCluster implements SchedulingStrategy {
    private final transient ResourceGetter resourceGetter;
    private final transient ScheduledInstanceRepository scheduledInstanceRepository;

    public ScheduleOneCluster(ResourceGetter resourceGetter, ScheduledInstanceRepository scheduledInstanceRepository) {
        this.resourceGetter = resourceGetter;
        this.scheduledInstanceRepository = scheduledInstanceRepository;
    }

    @Override
    @SuppressWarnings("PMD")
    public List<ScheduledInstance> scheduleBetween(ScheduleJob job, LocalDate start, LocalDate end) {
        LocalDate currentDate = start;
        while (currentDate.isBefore(end)) {
            int cpuToSchedule = job.getCpuUsage();
            int gpuToSchedule = job.getGpuUsage();
            int memoryToSchedule = job.getMemoryUsage();

            // 1. Make a request to Clusters microservice to check available resources for a given day
            List<FacultyResource> facultyResources = resourceGetter.getAvailableResources(job.getFaculty(), currentDate);
            for (var r : facultyResources) {
                // 2. Compare it with already used resources (sum all usage from ScheduledInstances in the db)
                List<ScheduledInstance> instancesInDb =
                        scheduledInstanceRepository.findByDateAndFaculty(r.getDate(), r.getFaculty());
                int cpuUsageSum = instancesInDb.stream().mapToInt(ScheduledInstance::getCpuUsage).sum();
                int gpuUsageSum = instancesInDb.stream().mapToInt(ScheduledInstance::getGpuUsage).sum();
                int memoryUsageSum = instancesInDb.stream().mapToInt(ScheduledInstance::getMemoryUsage).sum();

                int cpuFree = r.getCpuUsage() - cpuUsageSum;
                int gpuFree = r.getGpuUsage() - gpuUsageSum;
                int memoryFree = r.getMemoryUsage() - memoryUsageSum;

                int providedCpu = Math.min(cpuToSchedule, cpuFree);
                int providedGpu = Math.min(gpuToSchedule, gpuFree);
                int providedMemory = Math.min(memoryToSchedule, memoryFree);

                if (providedCpu == cpuToSchedule && providedGpu == gpuToSchedule && providedMemory == memoryToSchedule) {
                    // found suitable cluster
                    return List.of(new ScheduledInstance(job.getJobId(), job.getFaculty(), r.getFaculty(),
                            providedCpu, providedGpu, providedMemory, currentDate));
                }

            }

            // 3. If a day is full, try another one.
            currentDate = currentDate.plusDays(1);
        }

        return new ArrayList<>();
    }
}
