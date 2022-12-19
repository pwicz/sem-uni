package nl.tudelft.sem.template.example.models;

import lombok.Data;

/**
 * Request model for Job database id (primary key) for approval.
 */
@Data
public class ApproveRequestModel {

    private long id;

    /**
     * Constructor for ApproveRequestModel.
     *
     * @param id id of the Job to be approved
     */
    public ApproveRequestModel(long id) {
        this.id = id;
    }
}
