package nl.tudelft.sem.template.example.chain;

import commons.Account;
import commons.Job;
import commons.NetId;
import java.util.Optional;
import nl.tudelft.sem.template.example.domain.InvalidFacultyException;
import nl.tudelft.sem.template.example.domain.InvalidIdException;
import nl.tudelft.sem.template.example.domain.JobRepository;
import org.springframework.stereotype.Service;

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
        if (!role.equals(Account.Faculty)) {
            throw new InvalidFacultyException(role);
        }
        j.setStatus("approved");
        return j;
    }
}
