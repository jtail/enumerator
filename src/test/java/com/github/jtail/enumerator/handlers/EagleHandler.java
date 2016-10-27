package com.github.jtail.enumerator.handlers;

import com.github.jtail.enumerator.types.Bird;

import java.util.function.Function;


@Handler(Bird.EAGLE)
public class EagleHandler implements Function<Bird, String> {
    @Override
    public String apply(Bird bird) {
        return "Hunting with eagle";
    }
}
