package com.github.jtail.enumerator;

import com.github.jtail.enumerator.handlers.Handler;
import com.github.jtail.enumerator.types.AnimalType;
import com.github.jtail.enumerator.types.Bird;
import com.github.jtail.enumerator.types.Mammal;
import org.junit.Assert;
import org.junit.Test;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;

public class EnumeratorTest {
    @Test
    public void indexBeans() throws Exception {
        Map<String, Class<?>> index = Enumerator.indexBeans(AnimalType.class);

        Assert.assertEquals(Bird.class, index.get(Bird.DUCK));
        Assert.assertEquals(Bird.class, index.get(Bird.EAGLE));
        Assert.assertEquals(Mammal.class, index.get(Mammal.BEAR));
    }

    @Test
    public void indexConsumers() throws Exception {
        Map<String, Function<Object, String>> handlers =
                Enumerator.indexConsumers(Handler.class, Handler::value);

        Assert.assertEquals("Cooking duck", handlers.get(Bird.DUCK).apply(new Bird()));
        Assert.assertEquals("Hunting with eagle", handlers.get(Bird.EAGLE).apply(new Bird()));
        Assert.assertEquals("Running from bear", handlers.get(Mammal.BEAR).apply(new Mammal()));
    }

    @Test
    public void getClasses() throws Exception {
        Class<?>[] classes = Enumerator.getClasses(AnimalType.class).sorted(
                Comparator.comparing(Class::getName)
        ).toArray(Class<?>[]::new);

        Assert.assertArrayEquals(new Class<?>[]{AnimalType.class, Bird.class, Mammal.class}, classes);
    }
}