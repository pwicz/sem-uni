package nl.tudelft.sem.template.example.models;

import java.time.LocalDate;
import lombok.Data;

/**
 * Response Entity model to notify the User about the Job status.
 */
@Data
public class JobNotificationResponseModel {

    private long jobId;
    private String status;
    private LocalDate scheduleDate;

    /**
     * Response Entity model for the Notification of a Job.
     *
     * @param jobId the id of the Job
     * @param status the status of the Job
     * @param scheduleDate the date of executing the Job
     */
    public JobNotificationResponseModel(long jobId, String status, LocalDate scheduleDate) {
        this.jobId = jobId;
        this.status = status;
        this.scheduleDate = scheduleDate;
    }

    @Override
    public String toString() {
        //TODO: need to be updated!
        String text = "Job with job id " + jobId
                + " is " + status + ".";
        if (status.equals("ACCEPTED") || status.equals("RESCHEDULED")) {
            text += " The Job will be executed on the " + scheduleDate.toString();
        } else if (status.equals("PENDING")) {
            text += " Please check at another time, if the Job has been scheduled!";
        }

        return text;
    }
}
