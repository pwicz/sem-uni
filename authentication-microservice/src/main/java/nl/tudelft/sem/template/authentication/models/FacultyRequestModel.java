package nl.tudelft.sem.template.authentication.models;

import lombok.Data;

/**
 * Model for retrieving the faculty of a user.
 */
@Data
public class FacultyRequestModel {
    private String netId;
}
