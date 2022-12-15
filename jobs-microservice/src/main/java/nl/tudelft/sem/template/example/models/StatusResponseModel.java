package nl.tudelft.sem.template.example.models;

import lombok.Data;

/**
 * Response Entity model for the status of a Job.
 */
@Data
public class StatusResponseModel {

    private String status;

    /**
     * Constructor for StatusResponseModel.
     *
     * @param status the status of the request Job
     */
    public StatusResponseModel(String status) {
        this.status = status;
    }
}
