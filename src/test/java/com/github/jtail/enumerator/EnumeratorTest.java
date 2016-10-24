package com.github.jtail.enumerator;

import com.github.jtail.enumerator.test.Bird;
import com.github.jtail.enumerator.test.Mammal;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class EnumeratorTest {
    @Test
    public void name() throws Exception {
        Class<?>[] classes = Enumerator.getClasses(Mammal.class.getPackage().getName());
        Assert.assertArrayEquals(new Class<?>[]{Mammal.class, Bird.class}, classes);
    }
}