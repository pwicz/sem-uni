package nl.tudelft.sem.template.example.domain;

import commons.Faculty;
import commons.Job;
import commons.NetId;
import commons.ScheduleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AutomaticApproveJobsComponent {

    public final transient JobService jobService;

    @Autowired
    public AutomaticApproveJobsComponent(JobService jobService) {
        this.jobService = jobService;
    }


    @Scheduled(cron = "0 0 18 * * ?")
    public void approveJobsAfter6pm() throws Exception {

        // 1. get all pending Jobs
        List<Job> pendingJobs = jobService.getAllPendingJobs("approvalSystem");

        // 2. filter and sort so that only pending Jobs due tomorrow are considered and sorted according to their creation date
        List<Job> filteredSortedPendingJobs = pendingJobs.stream()
                .filter(x -> x.getPreferredDate() == LocalDate.now().plusDays(1)).sorted(new Comparator<Job>() {
                    @Override
                    public int compare(Job j1, Job j2) {
                        return j1.getDateCreated().compareTo(j2.getDateCreated());
                    }
                }).collect(Collectors.toList());

        // 3. approve jobs and stop, when CPU ressource == 0
        for (Job job : filteredSortedPendingJobs) {
            // check resources
                // make GET request to scheduler MS "/allResourcesNextDay" -> add it to JobService
            // create ScheduleJob

                NetId netId = job.getNetId();
                //Faculty faculty =
                //ScheduleJob scheduleJob = new ScheduleJob(job.getJobId(), faculty, job.getCpuUsage(), job.getGpuUsage(), job.getMemoryUsage());

            // call scheduleJob function


        }


    }
}
