package nl.tudelft.sem.template.example.models;

import commons.Job;
import java.util.List;

public class ScheduledJobsResponseModel {
    private List<Job> jobs;

    public ScheduledJobsResponseModel(List<Job> jobs) {
        this.jobs = jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }
}
