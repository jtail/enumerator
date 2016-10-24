package com.github.jtail.enumerator;

import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 *
 */
public class Enumerator {

    public static Map<String, Class<?>> scan(Class<?> packageMarker) {
        throw new UnsupportedOperationException();
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

}
