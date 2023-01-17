package nl.tudelft.sem.template.authentication.domain;

import commons.Faculty;
import commons.NetId;
import commons.Role;
import commons.RoleValue;
import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.authentication.domain.user.AppUser;
import nl.tudelft.sem.template.authentication.domain.user.Password;
import nl.tudelft.sem.template.authentication.domain.user.PasswordHashingService;
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
        userRepository.save(new AppUser(new NetId("mlica"),
            passwordHashingService.hash(new Password("passwd")),
            new Role(RoleValue.FAC_ACC), new ArrayList<>(List.of(new Faculty("EEMCS")))));
        userRepository.save(new AppUser(new NetId("admin"),
            passwordHashingService.hash(new Password("admin")),
            new Role(RoleValue.ADMIN), new ArrayList<>(List.of(new Faculty("EEMCS")))));
        userRepository.save(new AppUser(new NetId("employee"),
            passwordHashingService.hash(new Password("employee")),
            new Role(RoleValue.EMPLOYEE), new ArrayList<>(List.of(new Faculty("EEMCS")))));
    }
}
