package com.prop;

import com.util.Result;
import com.functional.Function;
import com.util.List;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;

/**
 * A utility class to read the properties file, and return Result objects, to
 * aid functional constructs.
 */
public class PropertyReader {
    private final Result<Properties> properties;
    /**
     * The source is registered in order to be used in error messages.
     */
    private final String source;

    private PropertyReader(Result<Properties> properties, String source) {
        this.properties = properties;
        this.source = source;
    }

    /**
     * This method converts a single property value into a property string
     * that can be used as input for a nested PropertyReader.
     * @param s
     * @return
     */
    public static String toPropertyString(String s) {
        return s.replace(";", "\n");
    }

    /**
     * This method reads a property and converts the value into a property string.
     * @param propertyName
     * @return
     */
    public Result<String> getAsPropertyString(String propertyName) {
        return getAsString(propertyName).map(PropertyReader::toPropertyString);
    }

    private static Result<Properties> readPropertiesFromFile(String configFileName) {
        try (InputStream inputStream = PropertyReader.class.getResourceAsStream(configFileName)) {
            var properties = new Properties();
            properties.load(inputStream);
            return Result.of(properties);
        }catch (NullPointerException e) {
            return Result.failure(String.format("File %s not found in classpath", configFileName));
        }catch (IOException e) {
            return Result.failure(String.format("IOException reading classpath resource %s", configFileName));
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    private static Result<Properties> readPropertiesFromString(String propString) {
        try (Reader reader = new StringReader(propString)) {
            var properties = new Properties();
            properties.load(reader);
            return Result.of(properties);
        } catch(Exception e) {
            return Result.failure(String.format("Exception reading property string %s", propString), e);
        }
    }

    public static PropertyReader filePropertyReader(String fileName) {
        return new PropertyReader(readPropertiesFromFile(fileName), String.format("File: %s", fileName));
    }

    public static PropertyReader stringPropertyReader(String propString) {
        return new PropertyReader(readPropertiesFromString(propString), String.format("String: %s", propString));
    }

    public Result<String> getProperty(String name) {
        return properties.flatMap(props -> getProperty(props, name));
    }

    private Result<String> getProperty(Properties properties, String name) {
        return Result.of(properties.getProperty(name))
                .mapFailure(String.format("Property \"%s\" not found", name));
    }

    public Result<String> getAsString(String name) {
        return getProperty(name);
    }

    public <T> Result<T> getAs(String name, Function<String, Result<T>> f) {
        return getAsString(name).flatMap(p -> {
            try {
                return f.apply(p);
            } catch (Exception e) {
                return Result.failure(String.format("Invalid value while parsing property %s: %s", name, p));
            }
        });
    }
    public Result<Integer> getAsInteger(String name) {
        return getAs(name, x -> Result.of(Integer.parseInt(x)));
    }

    public Result<Double> getAsDouble(String name) {
        return getAs(name, x -> Result.of(Double.parseDouble(x)));
    }

    public Result<Long> getAsLong(String name) {
        return getAs(name, x -> Result.of(Long.parseLong(x)));
    }

    public Result<Boolean> getAsBoolean(String name) {
        return getAs(name, x -> Result.of(Boolean.parseBoolean(x)));
    }

    public <T extends Enum<?>> Result<T> getAsEnum(final String parameterName, final Class<T> enumClass) {
        Function<String, Result<T>> f = t -> {
            try {
                //This is a trick to get the enum class in the next statement.
                T constant = enumClass.getEnumConstants()[0];
                @SuppressWarnings("unchecked")
                T value = (T) Enum.valueOf(constant.getClass(), t);
                return Result.success(value);
            } catch (Exception e) {
                return Result.failure(String.format("Error parsing property %s: value %s can't be parsed to %s.", t,
                        parameterName, enumClass.getName()));
            }
        };
        return getAs(parameterName, f);
    }

    public <T> Result<List<T>> getAsList(String name, Function<String, T> f) {
        var rString = properties.flatMap(props -> getProperty(props, name));

        return rString.flatMap(s -> {
            try {
                return Result.success(List.fromSeperatedString(s, ',').map(f));
            } catch(Exception e) {
                return Result.failure(String.format("Invalid value while parsing property %s: %s", name, s));
            }
        });
    }

    public Result<List<String>> getAsStringList(String name) {
        return getAsList(name, Function.identity());
    }

    public Result<List<Integer>> getAsIntegerList(String name) {
        return getAsList(name, Integer::parseInt);
    }

    public Result<List<Double>> getAsDoubleList(String name) {
        return getAsList(name, Double::parseDouble);
    }

    public Result<List<Long>> getAsLongList(String name) {
        return getAsList(name, Long::parseLong);
    }

    public Result<List<Boolean>> getAsBooleanList(String name) {
        return getAsList(name, Boolean::parseBoolean);
    }
}
