package nl.tudelft.sem.template.example.models;

import commons.Account;
import commons.Job;
import lombok.Data;
import nl.tudelft.sem.template.example.chain.DirectiveJob;

@Data
public class JobChainModel {

    private Job job;
    private Account authRole;
    private String authFaculty;

    private DirectiveJob directiveJob;

    /**
     * Constructor for JobChainModel.
     *
     * @param job a Job entity
     * @param authRole the role of the authenticated user
     * @param authFaculty the faculty of the authenticated user
     */
    public JobChainModel(Job job, Account authRole, String authFaculty, DirectiveJob directiveJob) {
        this.job = job;
        this.authRole = authRole;
        this.authFaculty = authFaculty;
        this.directiveJob = directiveJob;
    }
}
