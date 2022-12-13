package commons;

import lombok.Data;
import java.time.LocalDate;

/**
 * UpdateJob DTO.
 */
@Data
public class UpdateJob {

    private long id;
    private String status;

    private LocalDate scheduleDate;

    /**
     * Constructor for UpdateJob.
     *
     * @param id id of the Job in the database
     * @param status status of the Job
     * @param scheduleDate the schedule date of the Job
     */
    public UpdateJob(long id, String status, LocalDate scheduleDate) {
        this.id = id;
        this.status = status;
        this.scheduleDate = scheduleDate;
    }
}
