package nl.tudelft.sem.template.example.domain.processing;

import commons.FacultyResource;
import commons.ScheduleJob;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import lombok.Synchronized;
import nl.tudelft.sem.template.example.domain.db.ScheduledInstance;
import nl.tudelft.sem.template.example.domain.db.ScheduledInstanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@SuppressWarnings("PMD")
@Service
public class ProcessingJobsService {

    private final transient ScheduledInstanceRepository scheduledInstanceRepository;
    private final transient WebClient client = WebClient.create();

    private String resourcesUrl = "http://localhost:8085";
    private String jobsUrl = "http://localhost:8083";

    public void setResources_url(String resourcesUrl) {
        this.resourcesUrl = resourcesUrl;
    }

    public void setJobs_url(String jobsUrl) {
        this.jobsUrl = jobsUrl;
    }

    ProcessingJobsService(ScheduledInstanceRepository scheduledInstanceRepository) {
        this.scheduledInstanceRepository = scheduledInstanceRepository;
    }

    /**
     * This method tries to schedule a job defined by a ScheduleJob object.
     * Does not return anything, but sends a proper message to the Jobs
     * microservice (whether the job was scheduled or not).
     *
     * @param j a ScheduleJob DTO of a Job to be scheduled
     */
    @Synchronized
    public void scheduleJob(ScheduleJob j) {
        // start with the first possible day: tomorrow
        List<ScheduledInstance> scheduledInstances =
                trySchedulingBetween(j, LocalDate.now().plusDays(1), j.getScheduleBefore());

        if (scheduledInstances.isEmpty()) {
            // TODO: inform the Job microservice that the job was not scheduled
            // can't be done right now because there is no such endpoint.
            return;
        }

        try {
            scheduledInstanceRepository.saveAll(scheduledInstances);
        } catch (Exception e) {
            System.out.println("There was a problem: " + e.getMessage());
        }

        // TODO: inform the Job microservice about a success!
        // can't be done right now because there is no such endpoint.
        System.out.println("saved!");
    }

    private List<ScheduledInstance> trySchedulingBetween(ScheduleJob job, LocalDate start, LocalDate end) {
        LocalDate currentDate = start;
        while (currentDate.isBefore(end)) {
            // 1. Make a request to Clusters microservice to check available resources for a given day
            int cpuToSchedule = job.getCpuUsage();
            int gpuToSchedule = job.getGpuUsage();
            int memoryToSchedule = job.getMemoryUsage();

            List<FacultyResource> facultyResources = getAvailableResources(job.getFaculty(), currentDate);
            List<ScheduledInstance> scheduledInstances = new ArrayList<>();
            for (var r : facultyResources) {
                if (!(cpuToSchedule > 0 || gpuToSchedule > 0 || memoryToSchedule > 0)) {
                    break;
                }
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
                cpuToSchedule -= providedCpu;
                gpuToSchedule -= providedGpu;
                memoryToSchedule -= providedMemory;
                scheduledInstances.add(new ScheduledInstance(job.getJobId(), job.getFaculty(),
                        providedCpu, providedGpu, providedMemory, currentDate));
            }

            if (cpuToSchedule == 0 && gpuToSchedule == 0 && memoryToSchedule == 0) {
                // success!
                return scheduledInstances;
            }

            // 3. If a day is full, try another one.
            currentDate = currentDate.plusDays(1);
        }

        return new ArrayList<>();
    }

    private List<FacultyResource> getAvailableResources(String faculty, LocalDate date) {
        List<FacultyResource> facultyResources =
                client.get().uri(resourcesUrl + "/facultyResources?faculty=" + faculty
                                + "&day=" + date.toString()).retrieve().bodyToFlux(FacultyResource.class)
                        .collectList().block();
        if (facultyResources == null) {
            facultyResources = new ArrayList<>();
        }
        return facultyResources;
    }
}
