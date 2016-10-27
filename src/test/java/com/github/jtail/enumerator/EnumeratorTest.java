package com.github.jtail.enumerator;

import com.github.jtail.enumerator.handlers.BearHandler;
import com.github.jtail.enumerator.handlers.DuckHandler;
import com.github.jtail.enumerator.handlers.EagleHandler;
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
        Map<String, Class<? extends Function<?, String>>> handlers =
                Enumerator.indexConsumers(Handler.class, Handler::value);

        Assert.assertEquals(DuckHandler.class, handlers.get(Bird.DUCK));
        Assert.assertEquals(EagleHandler.class, handlers.get(Bird.EAGLE));
        Assert.assertEquals(BearHandler.class, handlers.get(Mammal.BEAR));
    }

    @Test
    public void getClasses() throws Exception {
        Class<?>[] classes = Enumerator.getClasses(AnimalType.class).sorted(
                Comparator.comparing(Class::getName)
        ).toArray(Class<?>[]::new);

        Assert.assertArrayEquals(new Class<?>[]{AnimalType.class, Bird.class, Mammal.class}, classes);
    }
}