package nl.tudelft.sem.template.example.models;


import lombok.Data;

/**
 * Response Entity model for Job class.
 */
@Data
public class JobResponseModel {

    private String netId;
    private String status;
    private Long id;
}
