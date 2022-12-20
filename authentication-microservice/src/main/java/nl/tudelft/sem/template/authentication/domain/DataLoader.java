package nl.tudelft.sem.template.authentication.domain;

import commons.Faculty;
import commons.NetId;
import commons.RoleType;
import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.authentication.domain.user.AppUser;
import nl.tudelft.sem.template.authentication.domain.user.Password;
import nl.tudelft.sem.template.authentication.domain.user.PasswordHashingService;
import nl.tudelft.sem.template.authentication.domain.user.Role;
import nl.tudelft.sem.template.authentication.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final transient UserRepository userRepository;

    private final transient PasswordHashingService passwordHashingService;

    @Autowired
    public DataLoader(UserRepository userRepository, PasswordHashingService passwordHashingService) {
        this.userRepository = userRepository;
        this.passwordHashingService = passwordHashingService;
    }

    @Override
    public void run(String ...args) throws Exception {
        loadUsers();
    }

    public void loadUsers() {
        /*userRepository.save(new AppUser(new NetId("mlica"),
            passwordHashingService.hash(new Password("passwd")),
            new Role(RoleType.Admin), new ArrayList<>(List.of(new Faculty("EEMCS")))));
        userRepository.save(new AppUser(new NetId("test"),
            passwordHashingService.hash(new Password("passwd")),
            new Role(RoleType.Employee), new ArrayList<>(List.of(new Faculty("EEMCS")))));
        userRepository.save(new AppUser(new NetId("test1"),
            passwordHashingService.hash(new Password("passwd")),
            new Role(RoleType.Faculty), new ArrayList<>(List.of(new Faculty("EEMCS")))));*/
    }
}
