package nl.tudelft.sem.template.example.domain.db;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledInstanceRepository extends JpaRepository<ScheduledInstance, Long> {
}
