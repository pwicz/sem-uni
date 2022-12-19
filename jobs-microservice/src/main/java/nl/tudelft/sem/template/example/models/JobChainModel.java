package nl.tudelft.sem.template.example.models;

import commons.Account;
import commons.Job;
import lombok.Data;

@Data
public class JobChainModel {

    private Job job;
    private Account authRole;
    private String authFaculty;

    public JobChainModel(Job job, Account authRole, String authFaculty) {
        this.job = job;
        this.authRole = authRole;
        this.authFaculty = authFaculty;
    }
}
