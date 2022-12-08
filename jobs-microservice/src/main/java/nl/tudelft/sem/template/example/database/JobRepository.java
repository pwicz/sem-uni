package nl.tudelft.sem.template.example.database;

import org.springframework.data.jpa.repository.JpaRepository;
import nl.tudelft.sem.template.example.Job;

public interface JobRepository extends JpaRepository<Job, Long> {
}
