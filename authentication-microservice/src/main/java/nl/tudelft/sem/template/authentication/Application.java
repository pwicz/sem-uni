package nl.tudelft.sem.template.authentication;

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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication()
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
