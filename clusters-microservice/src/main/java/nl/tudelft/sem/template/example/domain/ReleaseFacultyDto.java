package nl.tudelft.sem.template.example.domain;

import java.time.LocalDate;

public class ReleaseFacultyDto {
    private final String faculty;
    private final LocalDate date;
    private final int days;

    /**
     * Constructs a ReleaseFaculty object.
     *
     * @param faculty - the list of faculties a user is assigned to
     * @param date - the date when the resource is freed
     * @param days - the number of days the resource is freed
     */
    public ReleaseFacultyDto(String faculty, LocalDate date, int days) {
        this.faculty = faculty;
        this.date = date;
        this.days = days;
    }

    public String getFaculty() {
        return faculty;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getDays() {
        return days;
    }
}
