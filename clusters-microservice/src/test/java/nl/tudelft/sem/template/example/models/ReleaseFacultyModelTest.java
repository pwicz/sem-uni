package nl.tudelft.sem.template.example.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReleaseFacultyModelTest {
    ReleaseFacultyModel model;

    /**
     * Initialising a ReleaseFacultyModel.
     */
    @BeforeEach
    public void init() {
        model = new ReleaseFacultyModel();
        model.setFaculty("EEMCS");
        model.setDate(LocalDate.now());
        model.setDays(1);
    }

    @Test
    public void constructorTest() {
        model = new ReleaseFacultyModel();
        model.setFaculty("EEMCS");
        model.setDate(LocalDate.now());
        model.setDays(1);

        assertNotNull(model);
        assertThat(model.getFaculty()).isEqualTo("EEMCS");
        assertThat(model.getDate()).isEqualTo(LocalDate.now());
        assertThat(model.getDays()).isEqualTo(1);
    }

    @Test
    public void getFacultyTest() {
        assertThat(model.getFaculty()).isEqualTo("EEMCS");
    }

    @Test
    public void getDateTest() {
        assertThat(model.getDate()).isEqualTo(LocalDate.now());
    }

    @Test
    public void getDaysTest() {
        assertThat(model.getDays()).isEqualTo(1);
    }
}
