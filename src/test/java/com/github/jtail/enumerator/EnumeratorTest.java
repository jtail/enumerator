package com.github.jtail.enumerator;

import com.github.jtail.enumerator.types.AnimalType;
import com.github.jtail.enumerator.types.Bird;
import com.github.jtail.enumerator.types.Mammal;
import org.junit.Assert;
import org.junit.Test;

import java.util.Comparator;
import java.util.Map;

public class EnumeratorTest {
    @Test
    public void scan() throws Exception {
        Map<String, Class<?>> index = Enumerator.scan(AnimalType.class);

        Assert.assertEquals(Bird.class, index.get(Bird.DUCK));
        Assert.assertEquals(Bird.class, index.get(Bird.EAGLE));
        Assert.assertEquals(Mammal.class, index.get(Mammal.BEAR));
    }

    @Test
    public void getClasses() throws Exception {
        Class<?>[] classes = Enumerator.getClasses(AnimalType.class).sorted(
                Comparator.comparing(Class::getName)
        ).toArray(Class<?>[]::new);

        Assert.assertArrayEquals(new Class<?>[]{AnimalType.class, Bird.class, Mammal.class}, classes);
    }
}