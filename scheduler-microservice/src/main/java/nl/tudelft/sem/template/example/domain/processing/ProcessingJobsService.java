package nl.tudelft.sem.template.example.domain.processing;

import commons.Job;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import nl.tudelft.sem.template.example.domain.db.ScheduledInstance;
import nl.tudelft.sem.template.example.domain.db.ScheduledInstanceRepository;
import org.springframework.stereotype.Service;

@Service
public class ProcessingJobsService {

    private final transient ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private final transient Queue<Job> jobsToProcess;
    private final transient ScheduledInstanceRepository scheduledInstanceRepository;

    ProcessingJobsService(ScheduledInstanceRepository scheduledInstanceRepository) {
        jobsToProcess = new LinkedList<>();
        this.scheduledInstanceRepository = scheduledInstanceRepository;
    }

    public int addToQueue(Job job) {
        jobsToProcess.add(job);
        return jobsToProcess.size();
    }

    private void processJobs() {
        while (!jobsToProcess.isEmpty()) {
            Job j = jobsToProcess.poll();
            System.out.println("Processing job " + j.getNetId());

            // 1. Make a request to Clusters microservice to check available resources for a given day

            // 2. Compare it with already used resources (sum all usage from ScheduledInstances in the db)

            // 3. If a day is full, try another one. If all days are full, send appropriate update to
            // Jobs microservice

            try {
                // 4. Schedule a job if a not-full day was found:
                ScheduledInstance instance = new ScheduledInstance(j.getJobId(), "dummy", j.getCpuUsage(),
                        j.getGpuUsage(), j.getMemoryUsage(), LocalDate.of(2022, 12, 15));
                scheduledInstanceRepository.save(instance);
            } catch (Exception e) {
                System.out.println("There was a problem: " + e.getMessage());
            }


            System.out.println("saved!");
            // 5. Send appropriate update to Jobs microservice
        }
    }

    @PostConstruct
    private void startProcesser() {
        executorService.scheduleAtFixedRate(this::processJobs, 5, 5, TimeUnit.SECONDS);
    }

    @PreDestroy
    private void stopProcesser() {
        executorService.shutdown();
    }
}
