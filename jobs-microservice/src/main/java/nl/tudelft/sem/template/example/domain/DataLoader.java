package nl.tudelft.sem.template.example.domain;

import commons.Job;
import commons.NetId;
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
        jobRepository.save(new Job(new NetId("mlica"), 10, 10, 10));
        jobRepository.save(new Job(new NetId("mlica"), 100, 2000, 1));
        jobRepository.save(new Job(new NetId("mlica"), 100, 20, 200));
    }
}
