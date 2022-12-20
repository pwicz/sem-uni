package nl.tudelft.sem.template.example.models;

import commons.Faculty;
import commons.RoleType;
import commons.Job;
import lombok.Data;
import nl.tudelft.sem.template.example.chain.DirectiveJob;

import java.util.List;

@Data
public class JobChainModel {

    private Job job;
    private RoleType authRole;
    private List<Faculty> authFaculty;

    private DirectiveJob directiveJob;

    /**
     * Constructor for JobChainModel.
     *
     * @param job a Job entity
     * @param authRole the role of the authenticated user
     * @param authFaculty the faculty of the authenticated user
     */
    public JobChainModel(Job job, RoleType authRole, List<Faculty> authFaculty, DirectiveJob directiveJob) {
        this.job = job;
        this.authRole = authRole;
        this.authFaculty = authFaculty;
        this.directiveJob = directiveJob;
    }
}
