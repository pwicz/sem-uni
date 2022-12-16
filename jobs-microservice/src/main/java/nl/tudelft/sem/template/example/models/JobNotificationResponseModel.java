package nl.tudelft.sem.template.example.models;


import lombok.Data;

import java.time.LocalDate;

/**
 * Response Entity model to notify the User about the Job status.
 */
@Data
public class JobNotificationResponseModel {

    private long jobId;
    private String status;
    private LocalDate scheduleDate;

    public JobNotificationResponseModel(long jobId, String status, LocalDate scheduleDate) {
        this.jobId = jobId;
        this.status = status;
        this.scheduleDate = scheduleDate;
    }

    @Override
    public String toString() {
        //TODO: need to be updated!
        boolean accepted = status.equals("ACCEPTED");
        boolean canceled = status.equals("CANCELED");
        boolean pending = status.equals("PENDING");
        boolean rescheduled = status.equals("RESCHEDULED");

        String text = "Job with job id " + jobId
                + " is " + status + ".";
        if (accepted || rescheduled) {
            text += " The Job will be executed on the " + scheduleDate.toString();
        } else if (pending) {
            text += " Please check at another time, if the Job has been scheduled!";
        }

        return text;
    }
}
