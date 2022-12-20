package nl.tudelft.sem.template.example;

import commons.Node;
import nl.tudelft.sem.template.example.domain.NodeRepository;
import org.springframework.beans.factory.InitializingBean;
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

    private final NodeRepository nodeRepository;

    public Application(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    InitializingBean initDatabase() {
        return () -> {
            nodeRepository.save(new Node("node1", "node1", "EEMCS", "token", 1000, 1000, 1000));
            nodeRepository.save(new Node("node2", "node2", "Pool", "token", 1000, 1000, 1000));
        };
    }
}
