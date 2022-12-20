package nl.tudelft.sem.template.example.chain;

import commons.RoleType;
import commons.FacultyRequestModel;
import commons.FacultyResponseModel;
import commons.Job;
import commons.NetId;

import java.util.Collections;
import java.util.Optional;
import nl.tudelft.sem.template.example.domain.InvalidIdException;
import nl.tudelft.sem.template.example.domain.InvalidNetIdException;
import nl.tudelft.sem.template.example.domain.JobRepository;
import nl.tudelft.sem.template.example.models.JobChainModel;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("PMD")
@Service
public class ChainService {


    private final transient JobRepository jobRepository;

    private final RestTemplate restTemplate;

    /**
     * Constructor for the ChainService service.
     *
     * @param jobRepository Job database in the Jobs microservice
     */
    public ChainService(JobRepository jobRepository, RestTemplate restTemplate) {

        this.jobRepository = jobRepository;
        this.restTemplate = restTemplate;
    }

    /**
     * Approve a Job from the database by updating the status.
     *
     * @param netId netId
     * @param role role of the person trying to approve a Job.
     * @param id id of the Job in the database
     * @return the approved Job
     * @throws Exception if the Job can not be scheduled or some parameters are not expected
     */
    public Job approveJob(NetId netId, RoleType role, Long id) throws Exception {
        return handleJob(netId, role, id, DirectiveJob.Approve);
    }

    /**
     * Reject a Job from the database by updating the status.
     *
     * @param netId netId
     * @param role role of the person trying to reject a Job.
     * @param id id of the Job in the database
     * @return the rejected Job
     * @throws Exception if the Job cannot be rejected or some parameters are not expected
     */
    public Job rejectJob(NetId netId, RoleType role, Long id) throws Exception {
        return handleJob(netId, role, id, DirectiveJob.Reject);
    }

    /**
     * Handle a Job from the database by updating the status using chain of responsibility.
     *
     * @param netId netId
     * @param role role of the person trying to handle the Job.
     * @param id id of the Job in the database
     * @param directiveJob whether the person tries to approve or reject the Job
     * @return the handled Job
     * @throws Exception if the Job can not be handled or some parameters are not expected
     */
    private Job handleJob(NetId netId, RoleType role, Long id, DirectiveJob directiveJob) throws Exception {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if (jobOptional.isEmpty()) {
            throw new InvalidIdException(id);
        }
        Job j = jobOptional.get();
        String faculty = getFaculty(netId);

        // Chain Of Responsibility
        JobChainModel jobChainModel = new JobChainModel(j, role, faculty, directiveJob);
        Validator handler = new FacultyValidator();
        Validator handler2 = new FacultyResourceValidator();
        handler.setNext(handler2);
        handler2.setNext(new PoolResourceValidator());
        try {
            boolean valid = handler.handle(jobChainModel);
            if (valid) {
                j.setStatus("approved");
            } else {
                j.setStatus("rejected");
            }
            return j;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    private String getFaculty(NetId netId) throws Exception {
        FacultyRequestModel requestModel = new FacultyRequestModel();
        requestModel.setNetId(netId.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<FacultyRequestModel> entity = new HttpEntity<>(requestModel, headers);
        ResponseEntity<FacultyResponseModel> responseModelResponseEntity = restTemplate
                .exchange("http://localhost:8081/faculty", HttpMethod.POST, entity, FacultyResponseModel.class);

        if (!responseModelResponseEntity.getStatusCode().is2xxSuccessful()) {
            throw new InvalidNetIdException(netId.toString());
        }

        FacultyResponseModel response = responseModelResponseEntity.getBody();
        if (response == null) {
            throw new Exception("null body");
        }
        return response.getFaculty();
    }
}
