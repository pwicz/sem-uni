package nl.tudelft.sem.template.example.models;

import lombok.Data;

@Data
public class RejectRequestModel {

    private Long id;

    public RejectRequestModel(Long id) {
        this.id = id;
    }
}
