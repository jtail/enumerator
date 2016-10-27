package com.github.jtail.enumerator.handlers;

import com.github.jtail.enumerator.types.Mammal;

import java.util.function.Function;


@Handler(Mammal.BEAR)
public class BearHandler implements Function<Mammal, String> {
    @Override
    public String apply(Mammal mammal) {
        return "Running from bear";
    }
}
