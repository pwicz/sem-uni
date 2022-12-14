package commons;

import java.util.ArrayList;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA converter for the Faculty value object.
 */
@Converter
public class FacultyAttributeConverter implements AttributeConverter<ArrayList<Faculty>, String> {
    @Override
    public String convertToDatabaseColumn(ArrayList<Faculty> faculties) {
        String dbvalue = "";

        for (Faculty f : faculties) {
            dbvalue += f.toString() + ";";
        }
        return dbvalue;
    }

    @Override
    public ArrayList<Faculty> convertToEntityAttribute(String dbData) {
        String[] faculties = dbData.split(";");
        ArrayList<Faculty> fac = new ArrayList<>();
        for (String f : faculties) {
            fac.add(new Faculty(f));
        }
        return fac;
    }
}
