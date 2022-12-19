package nl.tudelft.sem.template.example.chain;

import commons.Account;
import commons.Job;
import commons.NetId;
import commons.Resource;
import nl.tudelft.sem.template.example.domain.InvalidFacultyException;
import nl.tudelft.sem.template.example.domain.InvalidIdException;
import nl.tudelft.sem.template.example.domain.JobRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
