package nl.tudelft.sem.template.example.controllers;

import commons.Job;
import commons.ScheduleJob;
import nl.tudelft.sem.template.example.domain.processing.ProcessingJobsService;
import nl.tudelft.sem.template.example.domain.processing.RemovingJobsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SchedulerController {

    private final transient ProcessingJobsService processingJobsService;
    private final transient RemovingJobsService removingJobsService;

    @Autowired
    public SchedulerController(ProcessingJobsService processingJobsService,
                               RemovingJobsService removingJobsService) {
        this.processingJobsService = processingJobsService;
        this.removingJobsService = removingJobsService;
    }

    /**
     * Allows to request a job to be scheduled.
     *
     * @param job job to be scheduled
     * @return confirmation that a job is now being processed
     */
    @PostMapping("/schedule")
    public ResponseEntity<String> scheduleJob(@RequestBody ScheduleJob job) {
        int queuePosition = processingJobsService.addToQueue(job);
        return ResponseEntity.ok("Processing. Position in queue: " + queuePosition);
    }

    /**
     * Allow to unschedule a job.
     *
     * @param jobId ID of a job that is to be unscheduled
     * @return response indicating if the operation was successful
     */
    @PostMapping("/unschedule/{jobId}")
    public ResponseEntity<String> unscheduleJob(@PathVariable("jobId") long jobId) {
        boolean status = removingJobsService.removeJob(jobId);
        if (!status) {
            return ResponseEntity.badRequest().body("Job with " + jobId + " id could not be unscheduled.");
        }

        return ResponseEntity.ok("Job was unscheduled.");
    }
}
