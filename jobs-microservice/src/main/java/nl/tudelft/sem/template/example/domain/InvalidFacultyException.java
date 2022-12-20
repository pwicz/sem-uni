package nl.tudelft.sem.template.example.domain;

import commons.RoleType;

/**
 * Exception to indicate the provided Faculty does not correspond to the required faculty.
 */
public class InvalidFacultyException extends Exception {

    static final long serialVersionUID = -3387516993124229948L;

    /**
     * Constructor for InvalidFacultyException.
     *
     * @param faculty faculty
     */
    public InvalidFacultyException(RoleType faculty) {
        super(faculty.toString());
    }
}
