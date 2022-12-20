package nl.tudelft.sem.template.example;

import commons.Job;
import commons.NetId;
import nl.tudelft.sem.template.example.domain.JobRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

/**
 * Example microservice application.
 */
@SpringBootApplication
@EntityScan(basePackages = {"commons"})
public class Application {

    private final JobRepository jobRepository;

    public Application(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    InitializingBean initDatabase() {
        return () -> {
            jobRepository.save(new Job(new NetId("mlica"), "CPU", 10, 10, 10));
            jobRepository.save(new Job(new NetId("mlica"), "GPU", 100, 2000, 1));
            jobRepository.save(new Job(new NetId("mlica"), "MEMORY", 100, 20, 200));
        };
    }
}
