package nl.tudelft.sem.template.example.domain.processing;

import commons.Faculty;
import commons.FacultyResource;
import commons.ScheduleJob;
import commons.UpdateJob;
import java.time.LocalDate;
import java.util.List;
import nl.tudelft.sem.template.example.domain.db.ScheduledInstance;
import nl.tudelft.sem.template.example.domain.db.ScheduledInstanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("PMD")
@Service
public class UpdatingJobsService {
    private final transient ScheduledInstanceRepository scheduledInstanceRepository;
    private final transient RemovingJobsService removingJobsService;
    private final transient ProcessingJobsService processingJobsService;
    private final transient RestTemplate restTemplate;

    /**
     * Constructor for the service.
     *
     * @param scheduledInstanceRepository .
     * @param restTemplate .
     * @param removingJobsService .
     * @param processingJobsService .
     */
    public UpdatingJobsService(ScheduledInstanceRepository scheduledInstanceRepository, RestTemplate restTemplate,
                               RemovingJobsService removingJobsService, ProcessingJobsService processingJobsService) {
        this.scheduledInstanceRepository = scheduledInstanceRepository;
        this.removingJobsService = removingJobsService;
        this.processingJobsService = processingJobsService;
        this.restTemplate = restTemplate;
    }

    /**
     * Function to update the schedule based on a new amount of resources.
     *
     * @param resource New amount of resources. resource.date indicates the starting date of the change.
     */
    public void updateSchedule(FacultyResource resource) {
        LocalDate currentDate = resource.getDate();

        while (true) {
            List<ScheduledInstance> instancesInDb =
                    scheduledInstanceRepository.findByDateAndFaculty(currentDate, resource.getFaculty());

            if (!instancesInDb.isEmpty()) {
                int cpuUsageSum = instancesInDb.stream().mapToInt(ScheduledInstance::getCpuUsage).sum();
                int gpuUsageSum = instancesInDb.stream().mapToInt(ScheduledInstance::getGpuUsage).sum();
                int memoryUsageSum = instancesInDb.stream().mapToInt(ScheduledInstance::getMemoryUsage).sum();

                int cpuExcess = Math.max(cpuUsageSum - resource.getCpuUsage(), 0);
                int gpuExcess = Math.max(gpuUsageSum - resource.getGpuUsage(), 0);
                int memoryExcess = Math.max(memoryUsageSum - resource.getMemoryUsage(), 0);

                if (cpuExcess > 0 || gpuExcess > 0 || memoryExcess > 0) {
                    reduceExcess(instancesInDb, cpuExcess, gpuExcess, memoryExcess);
                }
            }

            ScheduledInstance next =
                    scheduledInstanceRepository.findFirstByFacultyEqualsAndDateIsGreaterThanEqualOrderByDateAsc(
                            resource.getFaculty(), currentDate);

            if (next == null) {
                // nothing else to process
                break;
            }
            currentDate = next.getDate().plusDays(1);
        }
    }

    private void reduceExcess(List<ScheduledInstance> jobs, int cpu, int gpu, int memory) {
        jobs.sort((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));

        for (ScheduledInstance instance : jobs) {
            if (!(cpu > 0 || gpu > 0 || memory > 0)) {
                break;
            }

            if (!(cpu > 0 && instance.getGpuUsage() > 0 || gpu > 0 && instance.getCpuUsage() > 0
                    || memory > 0 && instance.getMemoryUsage() > 0)) {
                // skip instances that don't use the excess
                continue;
            }

            ScheduleJob job = recreateScheduleJobFromScheduledInstances(instance.getJobId());
            if (job == null || !removingJobsService.removeJob(instance.getJobId())) {
                // something went wrong...
                // TODO: proper error handling with exceptions
                continue;
            }

            cpu -= instance.getCpuUsage();
            gpu -= instance.getGpuUsage();
            memory -= instance.getMemoryUsage();

            // try to schedule the job again the same day
            if (!rescheduleJob(job, instance.getDate())) {
                // could not reschedule, inform the Jobs microservice
                restTemplate.postForEntity(processingJobsService.getJobsUrl() + "/updateStatus",
                        new UpdateJob(job.getJobId(), "cancelled", null), Void.class);
            }
        }
    }

    private ScheduleJob recreateScheduleJobFromScheduledInstances(long jobId) {
        List<ScheduledInstance> instances = scheduledInstanceRepository.findAllByJobId(jobId);
        if (instances.isEmpty()) {
            return null;
        }

        int cpu = instances.stream().mapToInt(ScheduledInstance::getCpuUsage).sum();
        int gpu  = instances.stream().mapToInt(ScheduledInstance::getGpuUsage).sum();
        int memory = instances.stream().mapToInt(ScheduledInstance::getMemoryUsage).sum();

        return new ScheduleJob(jobId, new Faculty(instances.get(0).getJobFaculty()), null, cpu, gpu, memory);
    }

    private boolean rescheduleJob(ScheduleJob job, LocalDate date) {
        List<ScheduledInstance> scheduledInstances =
                processingJobsService.trySchedulingBetween(job, date, date.plusDays(1));

        if (scheduledInstances.size() == 0) {
            return false;
        }

        scheduledInstanceRepository.saveAll(scheduledInstances);
        return true;
    }
}
