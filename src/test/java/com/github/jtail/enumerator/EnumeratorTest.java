package com.github.jtail.enumerator;

import com.github.jtail.enumerator.handlers.BearHandler;
import com.github.jtail.enumerator.handlers.DuckHandler;
import com.github.jtail.enumerator.handlers.EagleHandler;
import com.github.jtail.enumerator.handlers.MutantHandler;
import com.github.jtail.enumerator.handlers.WolfHandler;
import com.github.jtail.enumerator.types.AnimalType;
import com.github.jtail.enumerator.types.Bird;
import com.github.jtail.enumerator.types.Mammal;
import org.junit.Assert;
import org.junit.Test;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Stream;

public class EnumeratorTest {
    private final Map<String, Class<?>> beanIndex = Enumerator.indexBeans(AnimalType.class);

    @Test
    public void indexBeans() throws Exception {
        Map<String, Class<?>> index = Enumerator.indexBeans(AnimalType.class);

        Assert.assertEquals(Bird.class, index.get(Bird.DUCK));
        Assert.assertEquals(Bird.class, index.get(Bird.EAGLE));
        Assert.assertEquals(Mammal.class, index.get(Mammal.BEAR));
    }

    @Test
    public void indexConsumers() throws Exception {
        MagicBox magic = new MagicBox(beanIndex, Stream.of(new BearHandler(), new EagleHandler(), new DuckHandler()));
        Assert.assertEquals("Cooking duck", magic.handle(Bird.DUCK, new Bird()));
        Assert.assertEquals("Hunting with eagle", magic.handle(Bird.EAGLE, new Bird()));
        Assert.assertEquals("Running from bear", magic.handle(Mammal.BEAR, new Mammal()));
    }

    @Test(expected = IllegalStateException.class)
    public void missmatch() throws Exception {
        new MagicBox(beanIndex, Stream.of(new BearHandler(), new DuckHandler(), new MutantHandler()));
    }

    @Test(expected = IllegalStateException.class)
    public void duplicate() throws Exception {
        new MagicBox(beanIndex, Stream.of(new BearHandler(), new BearHandler(), new MutantHandler()));
    }

    @Test(expected = IllegalStateException.class)
    public void invalidType() throws Exception {
        new MagicBox(beanIndex, Stream.of(new BearHandler(), new DuckHandler(), new WolfHandler()));
    }

    @Test
    public void getClasses() throws Exception {
        Class<?>[] classes = Enumerator.getClasses(AnimalType.class).sorted(
                Comparator.comparing(Class::getName)
        ).toArray(Class<?>[]::new);

        Assert.assertArrayEquals(new Class<?>[]{AnimalType.class, Bird.class, Mammal.class}, classes);
    }
}