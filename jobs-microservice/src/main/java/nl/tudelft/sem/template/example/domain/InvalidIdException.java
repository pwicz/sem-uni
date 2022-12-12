package nl.tudelft.sem.template.example.domain;

/**
 * Exception to indicate the provided id does not exist in the database.
 */
public class InvalidIdException extends Exception {

    /**
     * Constructor for InvalidIdException.
     *
     * @param id invalid id provided
     */
    public InvalidIdException(long id) {
        super(Long.toString(id));
    }
}
