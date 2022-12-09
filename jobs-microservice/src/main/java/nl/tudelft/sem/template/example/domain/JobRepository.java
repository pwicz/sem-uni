package nl.tudelft.sem.template.example.domain;

import nl.tudelft.sem.template.example.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {


    /**
     * Find all the jobs associated to a netID.
     * @param netID netID of a user
     * @return Optional<Job>
     */
    Optional<Job> findAllByNetID(NetID netID);

    /**
     * Find job by id.
     * @param uuid id of the job
     * @return Optional<Job>
     */
    Optional<Job> findByID(UUID uuid);
}
