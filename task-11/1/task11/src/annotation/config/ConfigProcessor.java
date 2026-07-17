package annotation.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigProcessor {
    private static final String DEFAULT_CONFIG_FILE = "application.properties";
    private static final Map<String, Properties> loadedProperties = new HashMap<>();

    public static void process(Object obj) {
        process(obj, DEFAULT_CONFIG_FILE);
    }

    public static void process(Object obj, String defaultConfigFileName) {
        Class<?> clazz = obj.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);

                String configFileName = annotation.configFileName();
                if (configFileName.isEmpty()) {
                    configFileName = defaultConfigFileName;
                }

                String propertyName = annotation.propertyName();
                if (propertyName.isEmpty()) {
                    propertyName = clazz.getSimpleName().toUpperCase() + "." + field.getName().toUpperCase();
                }

                try {
                    Properties props = loadProperties(configFileName);
                    String value = props.getProperty(propertyName);

                    if (value == null) {
                        System.err.println("свойство не найдено: " + propertyName + " в файле " + configFileName);
                        continue;
                    }

                    field.setAccessible(true);
                    Object convertedValue = convertValue(value, field);
                    field.set(obj, convertedValue);

                    System.out.println("конфигурация: " + field.getName() + " = " + convertedValue);

                } catch (Exception e) {
                    System.err.println("ошибка конфигурации поля " + field.getName() + ": " + e.getMessage());
                }
            }
        }
    }

    private static Properties loadProperties(String configFileName) {
        if (loadedProperties.containsKey(configFileName)) {
            return loadedProperties.get(configFileName);
        }

        Properties props = new Properties();

        // Пробуем загрузить из разных мест
        String[] possiblePaths = {
                "config/" + configFileName,           // config/application.properties
                "src/config/" + configFileName,       // src/config/application.properties
                configFileName,                        // application.properties (корень проекта)
                "/config/" + configFileName,          // /config/application.properties
        };

        for (String path : possiblePaths) {
            try (InputStream input = ConfigProcessor.class.getClassLoader()
                    .getResourceAsStream(path)) {
                if (input != null) {
                    props.load(input);
                    loadedProperties.put(configFileName, props);
                    System.out.println("загружена конфигурация: " + path);
                    return props;
                }
            } catch (IOException e) {
                // Игнорируем и пробуем следующий путь
            }
        }

        // Пробуем загрузить из файловой системы
        try (InputStream input = new FileInputStream(configFileName)) {
            props.load(input);
            loadedProperties.put(configFileName, props);
            System.out.println("загружена конфигурация из файловой системы: " + configFileName);
            return props;
        } catch (IOException e) {
            System.err.println("не удалось загрузить конфигурацию: " + configFileName);
        }

        loadedProperties.put(configFileName, props);
        return props;
    }

    private static Object convertValue(String value, Field field) {
        Class<?> fieldType = field.getType();

        // Примитивные типы
        if (fieldType == int.class || fieldType == Integer.class) {
            return Integer.parseInt(value.trim());
        }
        if (fieldType == double.class || fieldType == Double.class) {
            return Double.parseDouble(value.trim());
        }
        if (fieldType == boolean.class || fieldType == Boolean.class) {
            return Boolean.parseBoolean(value.trim());
        }
        if (fieldType == long.class || fieldType == Long.class) {
            return Long.parseLong(value.trim());
        }
        if (fieldType == float.class || fieldType == Float.class) {
            return Float.parseFloat(value.trim());
        }
        if (fieldType == String.class) {
            return value;
        }

        // Enum
        if (fieldType.isEnum()) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            Enum result = Enum.valueOf((Class<Enum>) fieldType, value.trim().toUpperCase());
            return result;
        }

        // Массивы (разделитель запятая)
        if (fieldType.isArray()) {
            String[] parts = value.split(",");
            Class<?> componentType = fieldType.getComponentType();

            if (componentType == int.class || componentType == Integer.class) {
                return Arrays.stream(parts).map(String::trim).mapToInt(Integer::parseInt).toArray();
            }
            if (componentType == double.class || componentType == Double.class) {
                return Arrays.stream(parts).map(String::trim).mapToDouble(Double::parseDouble).toArray();
            }
            if (componentType == String.class) {
                return Arrays.stream(parts).map(String::trim).toArray(String[]::new);
            }
            if (componentType == boolean.class || componentType == Boolean.class) {
                return Arrays.stream(parts).map(String::trim).map(Boolean::parseBoolean).toArray(Boolean[]::new);
            }
            // Enum массивы
            if (componentType.isEnum()) {
                @SuppressWarnings({"unchecked", "rawtypes"})
                Enum[] enumArray = Arrays.stream(parts)
                        .map(String::trim)
                        .map(s -> Enum.valueOf((Class<Enum>) componentType, s.toUpperCase()))
                        .toArray(Enum[]::new);
                return enumArray;
            }

            return parts;
        }

        // Коллекции (List)
        if (fieldType == List.class) {
            String[] parts = value.split(",");
            Type genericType = field.getGenericType();

            if (genericType instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericType;
                Type[] actualTypeArguments = pt.getActualTypeArguments();

                if (actualTypeArguments.length > 0) {
                    Class<?> elementType = (Class<?>) actualTypeArguments[0];

                    if (elementType == Integer.class || elementType == int.class) {
                        return Arrays.stream(parts).map(String::trim).map(Integer::parseInt)
                                .collect(Collectors.toList());
                    }
                    if (elementType == Double.class || elementType == double.class) {
                        return Arrays.stream(parts).map(String::trim).map(Double::parseDouble)
                                .collect(Collectors.toList());
                    }
                    if (elementType == Boolean.class || elementType == boolean.class) {
                        return Arrays.stream(parts).map(String::trim).map(Boolean::parseBoolean)
                                .collect(Collectors.toList());
                    }
                    if (elementType.isEnum()) {
                        @SuppressWarnings({"unchecked", "rawtypes"})
                        List<Enum> enumList = Arrays.stream(parts)
                                .map(String::trim)
                                .map(s -> Enum.valueOf((Class<Enum>) elementType, s.toUpperCase()))
                                .collect(Collectors.toList());
                        return enumList;
                    }
                }
            }
            return Arrays.asList(parts);
        }

        // Коллекции (Set)
        if (fieldType == Set.class) {
            String[] parts = value.split(",");
            Type genericType = field.getGenericType();

            if (genericType instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericType;
                Type[] actualTypeArguments = pt.getActualTypeArguments();

                if (actualTypeArguments.length > 0) {
                    Class<?> elementType = (Class<?>) actualTypeArguments[0];
                    if (elementType.isEnum()) {
                        @SuppressWarnings({"unchecked", "rawtypes"})
                        Set<Enum> enumSet = Arrays.stream(parts)
                                .map(String::trim)
                                .map(s -> Enum.valueOf((Class<Enum>) elementType, s.toUpperCase()))
                                .collect(Collectors.toSet());
                        return enumSet;
                    }
                }
            }
            return new HashSet<>(Arrays.asList(parts));
        }

        // Если ничего не подошло - возвращаем как String
        return value;
    }

    public static void reload(Object obj) {
        loadedProperties.clear();
        process(obj);
    }

    public static void reload(Object obj, String configFileName) {
        loadedProperties.remove(configFileName);
        process(obj, configFileName);
    }
}