package nl.tudelft.sem.template.example.controllers;

import commons.Account;
import commons.Job;
import commons.NetId;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.chain.ChainService;
import nl.tudelft.sem.template.example.domain.InvalidIdException;
import nl.tudelft.sem.template.example.domain.InvalidNetIdException;
import nl.tudelft.sem.template.example.models.ApproveRequestModel;
import nl.tudelft.sem.template.example.models.JobResponseModel;
import nl.tudelft.sem.template.example.models.RejectRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ChainController {

    private final transient AuthManager authManager;
    private final transient ChainService chainService;

    @Autowired
    public ChainController(AuthManager authManager, ChainService chainService) {
        this.authManager = authManager;
        this.chainService = chainService;
    }

    /**
     * Approve the scheduling of a Job by a faculty account using Chain of Responsibility.
     *
     * @param request ApproveRequestModel needed to identify the Job
     * @return JobResponseModel with information about the approved Job
     */
    @PostMapping("/approve")
    public ResponseEntity<JobResponseModel> approveJob(@RequestBody ApproveRequestModel request) {
        try {
            NetId netId = new NetId(authManager.getNetId());
            Account role = (Account) authManager.getRole();
            Long id = request.getId();

            Job approvedJob = chainService.approveJob(netId, role, id);
            JobResponseModel jobResponseModel = new JobResponseModel(approvedJob.getNetId().toString(),
                    approvedJob.getStatus());
            return ResponseEntity.ok(jobResponseModel);
        } catch (InvalidIdException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_ID", e);
        } catch (InvalidNetIdException e) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "BAD_CREDENTIALS", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "BAD_REQUEST", e);
        }
    }

    /**
     * Reject the scheduling of a Job by a faculty account using Chain of Responsibility.
     *
     * @param request RejectRequestModel needed to identify a Job
     * @return JobResponseModel with information about the rejected Job.
     */
    @PostMapping("/reject")
    public ResponseEntity<JobResponseModel> rejectJob(@RequestBody RejectRequestModel request) {
        try {
            NetId netId = new NetId(authManager.getNetId());
            Account role = (Account) authManager.getRole();
            Long id = request.getId();

            Job rejectedJob = chainService.rejectJob(netId, role, id);
            JobResponseModel jobResponseModel = new JobResponseModel(rejectedJob.getNetId().toString(),
                    rejectedJob.getStatus());
            return ResponseEntity.ok(jobResponseModel);
        } catch (InvalidIdException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_ID", e);
        } catch (InvalidNetIdException e) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "BAD_CREDENTIALS", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "BAD_REQUEST", e);
        }
    }
}
