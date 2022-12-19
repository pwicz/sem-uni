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

    @PostMapping("/approve")
    public ResponseEntity<JobResponseModel> approveJob(@RequestBody ApproveRequestModel request) {
        try {
            NetId netId = new NetId(authManager.getNetId());
            Account role = (Account) authManager.getRole();
            Long id = request.getId();

            Job approvedJob = chainService.approveJob(netId, role, id);
            return ResponseEntity.ok().build();
        } catch (InvalidIdException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_ID", e);
        } catch (InvalidNetIdException e) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "BAD_CREDENTIALS", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "BAD_REQUEST", e);
        }
    }
}
