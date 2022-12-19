package nl.tudelft.sem.template.example.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.AccountNotAuthorizedException;
import nl.tudelft.sem.template.example.domain.FacultyCannotBeReleasedException;
import nl.tudelft.sem.template.example.domain.NodeRepository;
import nl.tudelft.sem.template.example.domain.ReleaseFacultyDto;
import nl.tudelft.sem.template.example.domain.UserNotInThisFacultyException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@SuppressWarnings("PMD")
@Service
public class ReleaseFacultyService {
    private final transient NodeRepository nodeRepository;
    private final transient AuthManager authManager;
    private final transient RestTemplate restTemplate;

    /**
     * Initiates a new ReleaseFacultyService.
     *
     * @param nodeRepository - the node repo
     * @param authManager    - the auth manager
     * @param restTemplate   - the rest template
     */
    public ReleaseFacultyService(NodeRepository nodeRepository, AuthManager authManager, RestTemplate restTemplate) {
        this.nodeRepository = nodeRepository;
        this.authManager = authManager;
        this.restTemplate = restTemplate;
    }

    /**
     * Releases a faculty from the given date for the given number of days.
     *
     * @param releaseFacultyDto - the releaseFaculty data transfer object
     * @throws AccountNotAuthorizedException - exception when account not authorized
     * @throws UserNotInThisFacultyException - exception when the user is not in the faculty of release
     * @throws FacultyCannotBeReleasedException - exception when a faculty cannot be released
     */
    public void releaseFaculty(ReleaseFacultyDto releaseFacultyDto) throws AccountNotAuthorizedException,
            UserNotInThisFacultyException, FacultyCannotBeReleasedException {
        if (!authManager.getRole().equals("FacultyAccount")) {
            throw new AccountNotAuthorizedException(authManager);
        }

        if (!getFaculty(authManager.getNetId()).contains(releaseFacultyDto.getFaculty())) {
            throw new UserNotInThisFacultyException(authManager);
        }

        if (releaseFacultyDto.getDate() == null || releaseFacultyDto.getFaculty() == null
                || releaseFacultyDto.getDate().isBefore(LocalDate.now()) || releaseFacultyDto.getDays() < 1) {
            throw new FacultyCannotBeReleasedException(authManager);
        }

        nodeRepository.updateRelease(releaseFacultyDto.getFaculty(),
                releaseFacultyDto.getDate(), releaseFacultyDto.getDays());
    }

    private List<String> getFaculty(String token) {
        String usersUrl = "http://localhost:8082"; //faculty request model

        ResponseEntity<String[]> facultyType = restTemplate.getForEntity(usersUrl
                + "/faculty", String[].class);

        if (facultyType.getBody() == null) {
            return new ArrayList<>();
        }

        return Arrays.asList(facultyType.getBody());
    }
}
