package nl.tudelft.sem.template.example.domain;

import exceptions.ResourceBiggerThanCpuException;
import nl.tudelft.sem.template.example.exceptions.NullValueException;
import nl.tudelft.sem.template.example.exceptions.ResourceMismatchException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class ModifyRepoHelperTest {

    private final transient ModifyRepoHelper modifyRepoHelper;

    public ModifyRepoHelperTest() {
        this.modifyRepoHelper = new ModifyRepoHelper();
    }

    @Test
    void checkNullValuesTest() throws NullValueException {
        Node n = new Node("123", null, "Something", "Something", 10, 10, 10);
        assertThrows(NullValueException.class,
                () -> modifyRepoHelper.checkNullValues(n));
    }

    @Test
    void checkResources() throws ResourceMismatchException {
        Node n = new Node("123", null, "Something", "Something", 10, 10, 10);
        assertDoesNotThrow(() -> modifyRepoHelper.checkResources(n));
    }

    @Test
    void checkResourcesThrows(){
        Node n = new Node("123", null, "Something", "Something", 10, 12, 12);
        assertThrows(ResourceMismatchException.class,
                () -> modifyRepoHelper.checkResources(n));
    }
}