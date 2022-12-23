package nl.tudelft.sem.template.example.domain.processing;

import commons.FacultyResource;
import commons.FacultyResponseModel;
import commons.FacultyTotalResource;
import commons.ScheduleJob;
import commons.UpdateJob;
import exceptions.ResourceBiggerThanCpuException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Synchronized;
import nl.tudelft.sem.template.example.domain.ResourceGetter;
import nl.tudelft.sem.template.example.domain.db.ScheduledInstance;
import nl.tudelft.sem.template.example.domain.db.ScheduledInstanceRepository;
import nl.tudelft.sem.template.example.domain.strategies.ScheduleBetweenClusters;
import nl.tudelft.sem.template.example.domain.strategies.ScheduleBetweenClustersMostResourcesFirst;
import nl.tudelft.sem.template.example.domain.strategies.ScheduleOneCluster;
import nl.tudelft.sem.template.example.domain.strategies.SchedulingStrategy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("PMD")
@Service
public class ProcessingJobsService {

    private final transient ScheduledInstanceRepository scheduledInstanceRepository;
    private final RestTemplate restTemplate;
    private final ResourceGetter resourceGetter;
    private SchedulingStrategy schedulingStrategy;

    private String resourcesUrl = "http://localhost:8085";
    private String jobsUrl = "http://localhost:8083";
    private String authUrl = "http://localhost:8081";

    public void setResources_url(String resourcesUrl) {
        this.resourcesUrl = resourcesUrl;
        resourceGetter.setResourcesUrl(resourcesUrl);
    }

    public void setJobs_url(String jobsUrl) {
        this.jobsUrl = jobsUrl;
    }

    public String getResourcesUrl() {
        return resourcesUrl;
    }

    public String getJobsUrl() {
        return jobsUrl;
    }

    ProcessingJobsService(ScheduledInstanceRepository scheduledInstanceRepository, RestTemplate restTemplate) {
        this.scheduledInstanceRepository = scheduledInstanceRepository;
        this.restTemplate = restTemplate;
        this.resourceGetter = new ResourceGetter(this.restTemplate, resourcesUrl);
        schedulingStrategy = new ScheduleBetweenClusters(this.resourceGetter,
                this.scheduledInstanceRepository);
    }

    /**
     * This method tries to schedule a job defined by a ScheduleJob object.
     * Does not return anything, but sends a proper message to the Jobs
     * microservice (whether the job was scheduled or not).
     *
     * @param j a ScheduleJob DTO of a Job to be scheduled
     */
    @Synchronized
    public void scheduleJob(ScheduleJob j) throws ResourceBiggerThanCpuException {
        // verify the CPU >= Max(GPU, Memory) requirement
        if (j.getCpuUsage() < Math.max(j.getGpuUsage(), j.getMemoryUsage())) {
            String resource = j.getGpuUsage() > j.getMemoryUsage() ? "GPU" : "Memory";
            throw new ResourceBiggerThanCpuException(resource);
        }

        // start with the first possible day: tomorrow or the day after tomorrow
        int possibleInXdays = 1;

        if (isFiveMinutesBeforeDayStarts(LocalTime.now())) {
            possibleInXdays = 2;
        }

        List<ScheduledInstance> scheduledInstances =
                schedulingStrategy.scheduleBetween(j, LocalDate.now().plusDays(possibleInXdays), j.getScheduleBefore());

        if (scheduledInstances.isEmpty()) {
            // inform the Job microservice that the job was not scheduled
            restTemplate.postForEntity(jobsUrl + "/updateStatus",
                    new UpdateJob(j.getJobId(), "unscheduled", null), Void.class);
            return;
        }

        try {
            scheduledInstanceRepository.saveAll(scheduledInstances);
        } catch (Exception e) {
            System.out.println("There was a problem: " + e.getMessage());
        }

        // inform the Job microservice about a success!
        restTemplate.postForEntity(jobsUrl + "/updateStatus",
                new UpdateJob(j.getJobId(), "scheduled", scheduledInstances.get(0).getDate()), Void.class);
        System.out.println("saved!");
    }

    /**
     * Gets the number of available resources out of total resources for the next day. Only admin can access it.
     *
     * @return  List of faculty resource
     */
    public List<FacultyTotalResource> getAllResourcesNextDay() {

        ResponseEntity<FacultyResponseModel> fac = restTemplate.getForEntity(authUrl
                + "/faculties", FacultyResponseModel.class);

        List<String> faculties = new ArrayList<>();
        if (fac.hasBody()) {
            faculties = fac.getBody().getFaculty();
        }

        List<FacultyTotalResource> res = new ArrayList<>();

        LocalDate tmrw = LocalDate.now().plusDays(1);
        for (String f : faculties) {
            List<ScheduledInstance> instancesInDb =
                    scheduledInstanceRepository.findByDateAndFaculty(tmrw, f);
            int cpuUsageSum = instancesInDb.stream().mapToInt(ScheduledInstance::getCpuUsage).sum();
            int gpuUsageSum = instancesInDb.stream().mapToInt(ScheduledInstance::getGpuUsage).sum();
            int memoryUsageSum = instancesInDb.stream().mapToInt(ScheduledInstance::getMemoryUsage).sum();

            List<FacultyResource> availableResources = resourceGetter.getAvailableResources(f, tmrw);
            int cpuAvailableSum = availableResources.stream().mapToInt(FacultyResource::getCpuUsage).sum();
            int gpuAvailableSum = availableResources.stream().mapToInt(FacultyResource::getGpuUsage).sum();
            int memoryAvailableSum = availableResources.stream().mapToInt(FacultyResource::getMemoryUsage).sum();

            FacultyTotalResource fr = new FacultyTotalResource(f, tmrw, cpuUsageSum, gpuUsageSum, memoryUsageSum,
                    cpuAvailableSum, gpuAvailableSum, memoryAvailableSum);
            res.add(fr);
        }
        return res;
    }

    /**
     * Checks if it is 5 minutes before a new day starts.
     *
     * @return true if the current time is between 25:55 (including) and 00:00 (excluding)
     */
    public boolean isFiveMinutesBeforeDayStarts(LocalTime currentTime) {
        LocalTime startTime = LocalTime.of(23, 55);
        return currentTime.isAfter(startTime) || currentTime.equals(startTime);
    }

    public SchedulingStrategy getSchedulingStrategy() {
        return schedulingStrategy;
    }

    public void setSchedulingStrategy(SchedulingStrategy schedulingStrategy) {
        this.schedulingStrategy = schedulingStrategy;
    }

    /**
     * Sets specified scheduling strategy.
     *
     * @param strategy name of the strategy
     */
    public void setSchedulingStrategy(String strategy) throws InvalidStrategyNameException {
        switch (strategy) {
            case "one-cluster":
                setSchedulingStrategy(new ScheduleOneCluster(resourceGetter, scheduledInstanceRepository));
                break;
            case "multiple-clusters":
                setSchedulingStrategy(new ScheduleBetweenClusters(resourceGetter, scheduledInstanceRepository));
                break;
            case "multiple-clusters-most-resources-first":
                setSchedulingStrategy(new ScheduleBetweenClustersMostResourcesFirst(resourceGetter,
                        scheduledInstanceRepository));
                break;
            default:
                throw new InvalidStrategyNameException(strategy);
        }
    }
}
