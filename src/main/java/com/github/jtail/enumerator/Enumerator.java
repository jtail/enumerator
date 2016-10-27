package com.github.jtail.enumerator;

import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.AbstractMap.SimpleEntry;

/**
 *
 */
public class Enumerator {

    public static Map<String, Class<?>> indexBeans(Class<? extends Annotation> annotation) {
        return indexBeans(annotation, annotation);
    }

    public static Map<String, Class<?>> indexBeans(Class<? extends Annotation> annotation, Class<?> packageMarker) {
        return getClasses(packageMarker).flatMap(
                c -> getFieldsFinalStaticString(c).filter(
                        field -> field.getAnnotation(annotation) != null
                ).map(
                        field -> toEntry(c, field)
                )
        ).collect(
                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)
        );
    }

    public static <T extends Annotation, C> Map<String, Class<? extends C>> indexConsumers(Class<T> annotation, Function<T, String> fn) {
        return indexConsumers(annotation, fn, annotation);
    }

    public static <T extends Annotation, C> Map<String, Class<? extends C>> indexConsumers(Class<T> annotation, Function<T, String> fn, Class<?> packageMarker) {
        return getClasses(packageMarker).filter(
                c -> c.getAnnotation(annotation) != null
        ).map(
                c -> (Class<C>) c
        ).collect(
                Collectors.toMap(c -> fn.apply(c.getAnnotation(annotation)), Function.identity())
        );
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageMarker
     */
    static Stream<Class<?>> getClasses(Class<?> packageMarker) {
        ClassLoader classLoader = packageMarker.getClassLoader();
        String packageName = packageMarker.getPackage().getName();
        return getClasses(packageName, classLoader);
    }

    @SneakyThrows(IOException.class)
    private static Stream<Class<?>> getClasses(String packageName, ClassLoader classLoader) {
        Enumeration<URL> resources = classLoader.getResources(packageName.replace('.', '/'));
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        return dirs.stream().flatMap(dir -> findClasses(dir, packageName));
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     */
    private static Stream<Class<?>> findClasses(File directory, String packageName) {
        return Optional.ofNullable(directory.listFiles()).map(
                files -> Stream.of(files).flatMap(file -> {
                    if (file.isDirectory()) {
                        assert !file.getName().contains(".");
                        return findClasses(file, packageName + "." + file.getName());
                    } else if (file.getName().endsWith(".class")) {
                        try {
                            return Stream.of(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e.getMessage(), e);
                        }
                    } else {
                        return Stream.empty();
                    }
                })
        ).orElse(Stream.empty());
    }

    private static <T> Stream<Field> getFieldsFinalStaticString(Class<T> clazz) {
        return Stream.of(clazz.getFields()).filter(
                Enumerator::isStaticFinal
        ).filter(f -> String.class.isAssignableFrom(f.getType()));
    }

    private static boolean isStaticFinal(Field f) {
        int modifiers = f.getModifiers();
        return Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
    }

    @SneakyThrows(IllegalAccessException.class)
    private static SimpleEntry<String, ? extends Class<?>> toEntry(Class<?> c, Field field) {
        return new SimpleEntry<>((String) field.get(null), c);
    }

}
