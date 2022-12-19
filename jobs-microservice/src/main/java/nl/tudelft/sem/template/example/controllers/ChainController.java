package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.chain.ChainService;
import nl.tudelft.sem.template.example.models.JobResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<JobResponseModel> approveJob(@RequestBody request) {

    }
}
