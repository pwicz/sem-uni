package nl.tudelft.sem.template.example.chain;

import commons.Account;
import commons.FacultyRequestModel;
import commons.FacultyResponseModel;
import commons.Job;
import commons.NetId;
import java.util.Optional;
import nl.tudelft.sem.template.example.domain.InvalidIdException;
import nl.tudelft.sem.template.example.domain.InvalidNetIdException;
import nl.tudelft.sem.template.example.domain.JobRepository;
import nl.tudelft.sem.template.example.models.JobChainModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChainService {


    private final transient JobRepository jobRepository;

    /**
     * Constructor for the ChainService service.
     *
     * @param jobRepository Job database in the Jobs microservice
     */
    public ChainService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    /**
     * Approve a Job from the database by updating the status.
     *
     * @param netId netId
     * @param role role of the person trying to approve a job.
     * @param id id of the Job in the database
     * @return the approved Job
     * @throws Exception if the Job can not be scheduled or some parameters are not expected
     */
    public Job approveJob(NetId netId, Account role, Long id) throws Exception {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if (jobOptional.isEmpty()) {
            throw new InvalidIdException(id);
        }
        Job j = jobOptional.get();
        String faculty = getFaculty(netId);

        // Chain Of Responsibility
        JobChainModel jobChainModel = new JobChainModel(j, role, faculty); // TODO: add proper faculty
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

        RestTemplate restTemplate = new RestTemplate();
        FacultyRequestModel requestModel = new FacultyRequestModel(netId.toString());
        ResponseEntity<FacultyResponseModel> responseModelResponseEntity = restTemplate
                .getForEntity("/faculty", FacultyResponseModel.class, requestModel);

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
