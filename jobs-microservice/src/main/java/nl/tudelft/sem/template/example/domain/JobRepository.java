package nl.tudelft.sem.template.example.domain;

import commons.NetId;
import commons.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface JobRepository extends JpaRepository<Job, Long> {


    /**
     * Find all the jobs associated to a netID.
     * @param netID netID of a user
     * @return Optional<Job>
     */
    Optional<List<Job>> findAllByNetId(NetId netID);

}
