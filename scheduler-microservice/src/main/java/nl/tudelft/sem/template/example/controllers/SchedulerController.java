package nl.tudelft.sem.template.example.controllers;

import commons.ScheduleJob;
import nl.tudelft.sem.template.example.authentication.AuthManager;
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
    private final transient AuthManager authManager;

    /**
     * Constructor of the Job controller.
     *
     * @param processingJobsService  the service which handles processing jobs from the database
     * @param removingJobsService  the service which handles the removing jobs from the database
     * @param authManager Spring Security component used to authenticate and authorize the user
     */
    @Autowired
    public SchedulerController(ProcessingJobsService processingJobsService,
                               RemovingJobsService removingJobsService, AuthManager authManager) {
        this.processingJobsService = processingJobsService;
        this.removingJobsService = removingJobsService;
        this.authManager = authManager;
    }

    /**
     * Allows to request a job to be scheduled.
     *
     * @param job job to be scheduled
     * @return confirmation that a job is now being processed
     */
    @PostMapping("/schedule")
    public ResponseEntity<String> scheduleJob(@RequestBody ScheduleJob job) {
        processingJobsService.scheduleJob(job);
        return ResponseEntity.ok("Processing");
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
