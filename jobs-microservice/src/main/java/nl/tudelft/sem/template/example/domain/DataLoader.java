package nl.tudelft.sem.template.example.domain;

import commons.Faculty;
import commons.Job;
import commons.NetId;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final transient JobRepository jobRepository;

    @Autowired
    public DataLoader(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public void run(String ...args) throws Exception {
        loadUsers();
    }

    public void loadUsers() {
        jobRepository.save(new Job(new NetId("employee"), new Faculty("EEMCS"), "resnet model", 300, 200, 100, LocalDate.now()));
        jobRepository.save(new Job(new NetId("adam"), new Faculty("EE"), "computer simulation", 300, 200, 100, LocalDate.now()));
        jobRepository.save(new Job(new NetId("megan"), new Faculty("AE"), "fluid dynamics simulation", 10, 10, 10, LocalDate.now()));
    }
}
