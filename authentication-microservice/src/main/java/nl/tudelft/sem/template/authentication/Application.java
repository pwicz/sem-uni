package nl.tudelft.sem.template.authentication;

import commons.RoleType;
import commons.Faculty;
import commons.NetId;
import nl.tudelft.sem.template.authentication.domain.user.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication()
public class Application {

    private final UserRepository userRepository;
    private final  PasswordHashingService passwordHashingService;

    public Application(UserRepository userRepository, PasswordHashingService passwordHashingService) {
        this.userRepository = userRepository;
        this.passwordHashingService = passwordHashingService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    InitializingBean initDatabase() {
        return () -> {
          userRepository.save(new AppUser(new NetId("mlica"), passwordHashingService.hash(new Password("passwd")), new Role(RoleType.Admin), new ArrayList<>(List.of(new Faculty("EEMCS")))));
          userRepository.save(new AppUser(new NetId("test"), passwordHashingService.hash(new Password("passwd")), new Role(RoleType.Employee), new ArrayList<>(List.of(new Faculty("EEMCS")))));
          userRepository.save(new AppUser(new NetId("test1"), passwordHashingService.hash(new Password("passwd")), new Role(RoleType.Faculty), new ArrayList<>(List.of(new Faculty("EEMCS")))));
        };
    }
}
