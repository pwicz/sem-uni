package nl.tudelft.sem.template.example.models;

import java.time.LocalDate;
import lombok.Data;

@Data
public class FacultyResourceModel {
    private String faculty;
    private LocalDate date;
}
