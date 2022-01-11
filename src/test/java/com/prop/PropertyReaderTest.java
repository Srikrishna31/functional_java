package com.prop;

import org.junit.Test;

public class PropertyReaderTest {
    @Test
    public void testProp() {
        var propertyReader = new PropertyReader("/sample.properties");
        propertyReader.getProperty("host")
                .forEachOrFail(System.out::println)
                .forEach(System.out::println);

        propertyReader.getProperty("name")
                .forEachOrFail(System.out::println)
                .forEach(System.out::println);

        propertyReader.getProperty("year")
                .forEachOrFail(System.out::println)
                .forEach(System.out::println);
    }
}
