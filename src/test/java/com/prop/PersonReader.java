package com.prop;

import com.io.Person;
import com.util.List;
import com.util.Result;

public class PersonReader {
    public static Result<Person> getAsPerson(String propertyName, PropertyReader propertyReader) {
        Result<String> rString = propertyReader.getAsPropertyString(propertyName);

        Result<PropertyReader> rPropReader = rString.map(PropertyReader::stringPropertyReader);

        return rPropReader.flatMap(PersonReader::readPerson);
    }

    public static Result<List<Person>> getAsPersonList(String propertyName, PropertyReader propertyReader) {
        Result<List<String>> rList = propertyReader.getAsStringList(propertyName);

        return rList.flatMap(list ->
                List.sequence(list.map(s ->
                        readPerson(PropertyReader.stringPropertyReader(PropertyReader.toPropertyString(s))))));
    }

    private static Result<Person> readPerson(PropertyReader propertyReader) {
        return propertyReader.getAsInteger("id")
                .flatMap(id -> propertyReader.getAsString("firstName")
                        .flatMap(firstName -> propertyReader.getAsString("lastName")
                                .map(lastName -> Person.apply(id, firstName, lastName))));
    }
}
