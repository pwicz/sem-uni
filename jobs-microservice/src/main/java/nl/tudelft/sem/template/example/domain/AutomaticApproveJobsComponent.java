package nl.tudelft.sem.template.example.domain;

import commons.Job;
import commons.ScheduleJob;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class AutomaticApproveJobsComponent {

    public final transient JobService jobService;

    @Autowired
    public AutomaticApproveJobsComponent(JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * Approves & schedule automatically every day at 6pm all PENDING jobs that are due tomorrow.
     *
     * @throws InvalidScheduleJobException if scheduleJob is null
     */
    @Scheduled(cron = "0 0 18 * * ?")
    public void approveJobsAfter6pm() throws InvalidScheduleJobException {

        // 1. get all pending Jobs
        List<Job> pendingJobs = jobService.getAllPendingJobs();

        // 2. filter and sort so that only pending Jobs due tomorrow are considered
        // and sorted according to their creation date
        List<Job> filteredSortedPendingJobs = filterAndSortPendingJobs(pendingJobs);

        // 3. approve & send jobs to scheduler
        for (Job job : filteredSortedPendingJobs) {
            ScheduleJob scheduleJob = new ScheduleJob(job.getJobId(),
                    job.getFaculty(), job.getPreferredDate(),
                    job.getCpuUsage(), job.getGpuUsage(), job.getMemoryUsage());

            jobService.scheduleJob(scheduleJob);
        }
    }

    /**
     *
     * @param pendingJobs list of Jobs that have the status "PENDING"
     * @return a sorted (according to creationDate) list of jobs that are due tomorrow
     */
    public List<Job> filterAndSortPendingJobs(List<Job> pendingJobs) {
        return pendingJobs.stream()
                .filter(x -> x.getPreferredDate().equals(LocalDate.now().plusDays(1)))
                .sorted((j1, j2) -> j1.getDateCreated().compareTo(j2.getDateCreated()))
                .collect(Collectors.toList());
    }
}
