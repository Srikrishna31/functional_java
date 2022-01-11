package com.prop;

import com.util.Result;

import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {
    private final Result<Properties> properties;

    public PropertyReader(String configFileName) {
        properties = readProperties(configFileName);
    }

    private Result<Properties> readProperties(String configFileName) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFileName)) {
            var properties = new Properties();
            properties.load(inputStream);
            return Result.of(properties);
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    public Result<String> getProperty(String name) {
        return properties.flatMap(props -> getProperty(props, name));
    }

    private Result<String> getProperty(Properties properties, String name) {
        return Result.of(properties.getProperty(name));
    }
}
