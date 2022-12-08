package nl.tudelft.sem.template.example.domain.processing;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import nl.tudelft.sem.template.example.domain.Job;
import org.springframework.stereotype.Service;

@Service
public class ProcessingJobsService {

    private final transient ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private final transient Queue<Job> jobsToProcess;

    ProcessingJobsService() {
        jobsToProcess = new LinkedList<>();
    }

    public int addToQueue(Job job) {
        jobsToProcess.add(job);
        return jobsToProcess.size();
    }

    private void processJobs() {
        while (!jobsToProcess.isEmpty()) {
            Job j = jobsToProcess.poll();
            System.out.println("Processing job " + j.name);
        }
    }

    @PostConstruct
    private void startProcesser() {
        executorService.scheduleAtFixedRate(this::processJobs, 30, 30, TimeUnit.SECONDS);
    }

    @PreDestroy
    private void stopProcesser() {
        executorService.shutdown();
    }
}
