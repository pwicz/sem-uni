package nl.tudelft.sem.template.example.domain;

import commons.Job;
import commons.NetId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    /**
     * Find all the jobs associated to a netId.
     *
     * @param netId netId of a user
     * @return Optional list of jobs
     */
    Optional<List<Job>> findAllByNetId(NetId netId);
}
