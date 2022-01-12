package com.prop;

import com.io.Person;
import com.util.Result;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import com.actor.Actor.Type;
import com.util.List;

public class PropertyReaderTest {
    @Test
    public void testProp() {
        var propertyReader = PropertyReader.filePropertyReader("/sample.properties");
        propertyReader.getProperty("host")
                .forEachOrFail(System.out::println)
                .forEach(System.out::println);

        propertyReader.getProperty("name")
                .forEachOrFail(System.out::println)
                .forEach(System.out::println);

        propertyReader.getProperty("year")
                .forEachOrFail(System.out::println)
                .forEach(System.out::println);

        Result<List<Integer>> list = propertyReader.getAsIntegerList("list");
        list.forEachOrFail(System.out::println).forEach(System.out::println);

        Result<Type> type = propertyReader.getAsEnum("type", Type.class);
        type.forEachOrFail(System.out::println).forEach(System.out::println);

        Result<Person> person2 = PersonReader.getAsPerson("person", propertyReader);
        person2.forEachOrFail(System.out::println).forEach(System.out::println);

        var person = propertyReader.getProperty("id").map(Integer::parseInt)
                .flatMap(id -> propertyReader.getProperty("firstName")
                        .flatMap(firstName -> propertyReader.getProperty("lastName")
                                .map(lastName -> Person.apply(id, firstName, lastName))));

        person.forEachOrFail(System.out::println).forEach(System.out::println);

        String persons = "employees:\\" +
                "id:3;firstName:Jane;lastName:Doe,\\" +
                "id:5;firstName:Paul;lastName:Smith,\\" +
                "id:8;firstName:Mary;lastName:Winston";

        var propReader = PropertyReader.stringPropertyReader(persons);
        var res = PersonReader.getAsPersonList("employees", propReader);

        res.forEachOrFail(System.out::println).forEach(System.out::println);

    }

    @Test
    public void testPropertyString(){
        String propString = "id:3\nfirstName:Jane\nlastName:Doe";
        var propertyReader = PropertyReader.stringPropertyReader(propString);

        var person = propertyReader.getProperty("id").map(Integer::parseInt)
                .flatMap(id -> propertyReader.getProperty("firstName")
                        .flatMap(firstName -> propertyReader.getProperty("lastName")
                                .map(lastName -> Person.apply(id, firstName, lastName))));

        person.forEachOrFail(System.out::println).forEach(System.out::println);

        assertTrue(Person.apply(3, "Jane", "Doe").equals(person.getOrElse(Person.apply(0, "", ""))));
    }
}
