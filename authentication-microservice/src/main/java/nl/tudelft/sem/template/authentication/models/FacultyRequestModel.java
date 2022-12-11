package nl.tudelft.sem.template.authentication.models;

import lombok.Data;
import nl.tudelft.sem.template.authentication.domain.user.NetId;

/**
 * Model for retrieving the faculty of a user.
 */
@Data
public class FacultyRequestModel {
    private String netId;
}
