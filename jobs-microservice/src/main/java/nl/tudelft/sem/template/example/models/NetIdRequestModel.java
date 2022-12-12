package nl.tudelft.sem.template.example.models;

import lombok.Data;

@Data
public class NetIdRequestModel {

    private String netId;

    /**
     * Constructor for NetIdRequestModel
     *
     * @param netId string of the provided NetId
     */
    NetIdRequestModel(String netId) {
        this.netId = netId;
    }
}
