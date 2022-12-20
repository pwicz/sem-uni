package commons;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Model for retrieving the faculty of a user.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacultyResponseModel {
    private List<String> faculty;
}
