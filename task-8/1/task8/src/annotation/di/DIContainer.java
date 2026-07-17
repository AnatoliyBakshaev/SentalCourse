package annotation.di;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DIContainer {
    private static DIContainer instance;
    private final Map<Class<?>, Object> components;
    private final Map<Class<?>, Class<?>> componentClasses;
    private final Set<String> scannedPackages;

    private DIContainer() {
        this.components = new ConcurrentHashMap<>();
        this.componentClasses = new ConcurrentHashMap<>();
        this.scannedPackages = new HashSet<>();
    }

    public static DIContainer getInstance() {
        if (instance == null) {
            instance = new DIContainer();
        }
        return instance;
    }

    public void scan(String... packageNames) {
        for (String packageName : packageNames) {
            if (!scannedPackages.contains(packageName)) {
                scannedPackages.add(packageName);
                scanPackage(packageName);
            }
        }
        createInstances();
        injectDependencies();
        printComponents();
    }

    private void scanPackage(String packageName) {
        try {
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = Thread.currentThread()
                    .getContextClassLoader()
                    .getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());

                if (directory.exists() && directory.isDirectory()) {
                    scanDirectory(directory, packageName);
                }
            }
        } catch (Exception e) {
            System.err.println("ошибка сканирования пакета " + packageName + ": " + e.getMessage());
        }
    }

    private void scanDirectory(File directory, String packageName) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(Component.class)) {
                        // Регистрируем сам класс
                        componentClasses.put(clazz, clazz);
                        System.out.println("зарегистрирован компонент: " + className);

                        // Регистрируем по интерфейсам
                        for (Class<?> iface : clazz.getInterfaces()) {
                            componentClasses.put(iface, clazz);
                            System.out.println("зарегистрирован компонент: " + iface.getSimpleName() + " -> " + clazz.getSimpleName());
                        }
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("не удалось загрузить класс: " + className);
                }
            }
        }
    }

    private void createInstances() {
        for (Map.Entry<Class<?>, Class<?>> entry : componentClasses.entrySet()) {
            Class<?> key = entry.getKey();
            Class<?> implementation = entry.getValue();
            if (!components.containsKey(key)) {
                try {
                    Object instance = implementation.getDeclaredConstructor().newInstance();
                    components.put(key, instance);
                } catch (Exception e) {
                    System.err.println("не удалось создать экземпляр " + implementation.getName());
                }
            }
        }
    }

    private void injectDependencies() {
        for (Object instance : components.values()) {
            inject(instance);
        }
    }

    public void inject(Object obj) {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(Inject.class) || field.isAnnotationPresent(Autowired.class)) {
                Class<?> fieldType = field.getType();

                Object dependency = components.get(fieldType);

                if (dependency == null) {
                    for (Map.Entry<Class<?>, Object> entry : components.entrySet()) {
                        if (fieldType.isAssignableFrom(entry.getKey())) {
                            dependency = entry.getValue();
                            break;
                        }
                    }
                }

                if (dependency == null) {
                    try {
                        if (!fieldType.isInterface() && !Modifier.isAbstract(fieldType.getModifiers())) {
                            dependency = fieldType.getDeclaredConstructor().newInstance();
                            components.put(fieldType, dependency);
                            inject(dependency);
                        }
                    } catch (Exception e) {
                        System.err.println("не удалось создать зависимость для " + field.getName());
                    }
                }

                if (dependency != null) {
                    try {
                        field.setAccessible(true);
                        field.set(obj, dependency);
                        System.out.println("внедрена зависимость: " + field.getName() + " в " + clazz.getSimpleName());
                    } catch (IllegalAccessException e) {
                        System.err.println("не удалось внедрить зависимость " + field.getName());
                    }
                }
            }
        }
    }

    public void register(Class<?> clazz) {
        componentClasses.put(clazz, clazz);
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            components.put(clazz, instance);
        } catch (Exception e) {
            System.err.println("не удалось зарегистрировать компонент: " + e.getMessage());
        }
    }

    public void registerInstance(Class<?> clazz, Object instance) {
        components.put(clazz, instance);
        componentClasses.put(clazz, instance.getClass());
    }

    @SuppressWarnings("unchecked")
    public <T> T getComponent(Class<T> clazz) {
        return (T) components.get(clazz);
    }

    public void printComponents() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("  зарегистрированные компоненты (" + components.size() + ")");
        System.out.println("==================================================");
        for (Map.Entry<Class<?>, Object> entry : components.entrySet()) {
            System.out.println("  " + entry.getKey().getSimpleName() + " -> " +
                    entry.getValue().getClass().getSimpleName());
        }
        System.out.println("==================================================");
    }

    public void clear() {
        components.clear();
        componentClasses.clear();
        scannedPackages.clear();
    }
}