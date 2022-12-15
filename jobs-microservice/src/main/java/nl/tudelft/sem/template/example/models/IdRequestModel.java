package nl.tudelft.sem.template.example.models;

import lombok.Data;

/**
 * Request model for Job database id (primary key).
 */
@Data
public class IdRequestModel {

    private long id;

    /**
     * Constructor of IdRequestModel.
     *
     * @param id the requested id of a Job in the database
     */
    public IdRequestModel(long id) {
        this.id = id;
    }
}
