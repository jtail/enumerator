package com.github.jtail.enumerator;

import com.github.jtail.enumerator.types.Bird;
import com.github.jtail.enumerator.types.Mammal;
import com.github.jtail.enumerator.types.AnimalType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Comparator;

/**
 *
 */
public class EnumeratorTest {
    @Test
    public void name() throws Exception {
        Class<?>[] classes = Enumerator.getClasses(AnimalType.class).sorted(
                Comparator.comparing(Class::getName)
        ).toArray(Class<?>[]::new);

        Assert.assertArrayEquals(new Class<?>[]{AnimalType.class, Bird.class, Mammal.class}, classes);
    }
}