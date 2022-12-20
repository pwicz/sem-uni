package nl.tudelft.sem.template.example.domain.processing;

import commons.FacultyResource;
import commons.ScheduleJob;
import commons.UpdateJob;
import commons.exceptions.ResourceBiggerThanCpuException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Synchronized;
import nl.tudelft.sem.template.example.domain.db.ScheduledInstance;
import nl.tudelft.sem.template.example.domain.db.ScheduledInstanceRepository;
import nl.tudelft.sem.template.example.models.FacultyResponseModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("PMD")
@Service
public class ProcessingJobsService {

    private final transient ScheduledInstanceRepository scheduledInstanceRepository;
    private final RestTemplate restTemplate;

    private String resourcesUrl = "http://localhost:8085";
    private String jobsUrl = "http://localhost:8083";
    private String authUrl = "http://localhost:8081";

    public void setResources_url(String resourcesUrl) {
        this.resourcesUrl = resourcesUrl;
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
        //TODO: calculate possible day

        List<ScheduledInstance> scheduledInstances =
                trySchedulingBetween(j, LocalDate.now().plusDays(possibleInXdays), j.getScheduleBefore());

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

    protected List<ScheduledInstance> trySchedulingBetween(ScheduleJob job, LocalDate start, LocalDate end) {
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
                scheduledInstances.add(new ScheduledInstance(job.getJobId(), job.getFaculty(), r.getFaculty(),
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

        ResponseEntity<FacultyResource[]> facultyResourcesResponse = restTemplate.getForEntity(resourcesUrl
                + "/resources?faculty=" + faculty + "&day=" + date.toString(), FacultyResource[].class);

        if (facultyResourcesResponse == null) {
            return new ArrayList<>();
        }

        return Arrays.asList(facultyResourcesResponse.getBody());
    }

    /**
     * Gets the number of available resources out of total resources for the next day. Only admin can access it.


     * @return  List of faculty resource
     */
    public List<FacultyResource> getAllResourcesNextDay() {

        ResponseEntity<FacultyResponseModel> fac = restTemplate.getForEntity(authUrl
                + "/faculties", FacultyResponseModel.class);

        List<String> faculties = Arrays.asList(fac.getBody().getFaculties());

        List<FacultyResource> res = new ArrayList<>();

        LocalDate tmrw = LocalDate.now().plusDays(1);
        for (String f : faculties) {
            ResponseEntity<FacultyResource> facultyResourcesResponse = restTemplate.getForEntity(resourcesUrl
                    + "/resources?faculty=" + f + "&day=" + tmrw, FacultyResource.class);

            FacultyResource total  = facultyResourcesResponse.getBody();
            if (facultyResourcesResponse.getBody() == null) {
                continue;
            }
            List<ScheduledInstance> instancesInDb =
                    scheduledInstanceRepository.findByDateAndFaculty(tmrw, f);
            int cpuUsageSum = instancesInDb.stream().mapToInt(ScheduledInstance::getCpuUsage).sum();
            int gpuUsageSum = instancesInDb.stream().mapToInt(ScheduledInstance::getGpuUsage).sum();
            int memoryUsageSum = instancesInDb.stream().mapToInt(ScheduledInstance::getMemoryUsage).sum();

            FacultyResource fr = new FacultyResource(f, tmrw,
                    cpuUsageSum, gpuUsageSum, memoryUsageSum,
                    total.getCpuUsageTotal(), total.getGpuUsageTotal(), total.getMemoryUsageTotal()
            );
            res.add(fr);
        }
        return res;
    }

    /**
     * Checks if it is 5 minutes before a new day starts.
     *
     * @return true if the current time is between 25:55 and 00:00 (excluding)
     */
    private boolean isFiveMinutesBeforeDayStarts() {
        LocalTime currentTime = LocalTime.now();
        LocalTime startTime = LocalTime.of(23, 55);
        LocalTime endTime = LocalTime.of(0, 0);
        return currentTime.isAfter(startTime) && currentTime.isBefore(endTime);
    }
}
