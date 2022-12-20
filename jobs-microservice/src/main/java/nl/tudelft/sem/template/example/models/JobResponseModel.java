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

    public JobResponseModel(String netId, String status, Long id) {
        this.netId = netId;
        this.status = status;
        this.id = id;
    }
}
