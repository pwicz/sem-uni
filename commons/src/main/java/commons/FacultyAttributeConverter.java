package commons;

import commons.Faculty;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA converter for the Faculty value object.
 */
@Converter
public class FacultyAttributeConverter implements AttributeConverter<Faculty, String> {
    @Override
    public String convertToDatabaseColumn(Faculty faculty) {
        return faculty.toString();
    }

    @Override
    public Faculty convertToEntityAttribute(String dbData) {
        return new Faculty(dbData);
    }
}
