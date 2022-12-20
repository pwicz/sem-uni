package commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model for retrieving the faculty of a user.
 */
@Data
public class FacultyResponseModel {
    private List<String> faculty;
}
