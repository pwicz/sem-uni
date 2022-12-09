package nl.tudelft.sem.template.example.domain;

import nl.tudelft.sem.template.example.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
}
