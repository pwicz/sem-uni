package nl.tudelft.sem.template.example.models;

import java.time.LocalDate;

public class FacultyResourceModel {
    private transient String faculty;
    private transient LocalDate date;

    /**
     * Constructs RequestModel for cluster.
     *
     * @param faculty faculty which resources belong to
     * @param date date when the specified resources are available
     */
    public FacultyResourceModel(String faculty, LocalDate date) {
        this.faculty = faculty;
        this.date = date;
    }

    public String getFaculty() {
        return faculty;
    }

    public LocalDate getDate() {
        return date;
    }
}
