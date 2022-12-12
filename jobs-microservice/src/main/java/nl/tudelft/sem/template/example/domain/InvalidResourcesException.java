package nl.tudelft.sem.template.example.domain;


/**
 * Exception to indicate that some requested resources are invalid.
 */
public class InvalidResourcesException extends Exception {

    /**
     * Constructor of InvalidResourcesException
     *
     * @param resource the invalid resource
     */
    public InvalidResourcesException(int resource) {
        super(Integer.toString(resource));
    }

}
