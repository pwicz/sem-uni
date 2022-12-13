package nl.tudelft.sem.template.authentication.domain.user;

import commons.Faculty;
import commons.NetId;

import java.util.ArrayList;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * A DDD service for obtaining the faculty of a user.
 */
@Service
public class GetFacultyService {
    private final transient UserRepository userRepository;

    /**
     * Initiates a new getFaculty service.
     *
     * @param userRepository the user repository
     */
    public GetFacultyService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves the faculty of a user.
     *
     * @param netId the netID of the user
     * @return the faculty of the user
     * @throws NetIdDoesNotExistException if the netID does not exist in the database
     */
    public ArrayList<Faculty> getFaculty(NetId netId) throws NetIdDoesNotExistException {
        if (checkNetIdExists(netId)) {
            Optional<AppUser> user = userRepository.findByNetId(netId);

            if (user.isPresent()) {
                return user.get().getFaculty();
            }

            throw new NetIdDoesNotExistException(netId);
        }

        throw new NetIdDoesNotExistException(netId);
    }

    public boolean checkNetIdExists(NetId netId) {
        return userRepository.existsByNetId(netId);
    }
}
