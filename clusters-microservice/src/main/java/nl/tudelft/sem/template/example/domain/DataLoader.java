package nl.tudelft.sem.template.example.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final transient NodeRepository nodeRepository;

    @Autowired
    public DataLoader(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @Override
    public void run(String ...args) throws Exception {
        loadUsers();
    }

    public void loadUsers() {
        nodeRepository.save(new Node("node1", "node1", "EEMCS", "token", 1000, 1000, 1000));
        nodeRepository.save(new Node("node2", "node2", "EE", "token1", 100, 250, 500));
        nodeRepository.save(new Node("node3", "node2", "AE", "token2", 500, 500, 2000));
        nodeRepository.save(new Node("node4", "node2", "Pool", "token3", 50, 100, 100));
    }
}
