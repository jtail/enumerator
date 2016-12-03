package com.github.jtail.enumerator;

import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

    /**
     * Convenience method that can be used when the annotation class being used to mark
     * fields doubles as package marker.
     * @see #indexBeans(Class, Class)
     */
    public static Map<String, Class<?>> indexBeans(Class<? extends Annotation> annotation) {
        return indexBeans(annotation, annotation);
    }

    /**
     * Indexes all beans in a package that carry given annotation on their final static String fields.
     *
     * @param annotation annotation to look for on final static String fields
     * @param packageMarker specifies the location where the classes in the package where this class is
     * @return A map, where key is String field value from the annotated field and value is class name.
     */
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

    public static <A extends Annotation, V> Map<String, V> indexConsumers(
            Class<A> annotation,
            Function<A, String> fn,
            Stream<V> objects,
            Class<?> consumerClazz,
            Map<String, Class<?>> beanIndex
    ) {
        Function<Class<?>, String> keyFromClass = fn.compose(clazz -> clazz.getAnnotation(annotation));
        Function<Object, String> compose1 = o -> {
            Class<?> clazz = o.getClass();
            Type type = Stream.of(clazz.getGenericInterfaces()).findFirst().orElse(null);
            Type parameter = ((ParameterizedType) type).getActualTypeArguments()[0];
            String key = keyFromClass.apply(clazz);
            Class<?> beanClazz = beanIndex.get(key);
            if (beanClazz == null) {
                throw new IllegalStateException("Unknown key [" + key + "]");
            } else if (((Class<?>) parameter).isAssignableFrom(beanClazz)) {
                return key;
            } else {
                throw new IllegalStateException("Class [" + beanClazz.getName() + "] is not assignable from [" + ((Class) parameter).getName() + "]");
            }
        };

        return objects.filter(
                consumerClazz::isInstance
        ).filter(
                c -> c.getClass().getAnnotation(annotation) != null
        ).collect(
                Collectors.toMap(compose1, Function.identity(), (a, b) -> {
                    throw new IllegalStateException("Multiple consumers detected on the same key: [" + a + "] and [" + b + "]");
                })
        );
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageMarker
     */
    public static Stream<Class<?>> getClasses(Class<?> packageMarker) {
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