package com.github.jtail.enumerator.handlers;

import com.github.jtail.enumerator.types.Bird;

import java.util.function.Function;


@Handler(Bird.DUCK)
public class DuckHandler implements Function<Bird, String> {

    @Override
    public String apply(Bird bird) {
        return "Cooking duck";
    }
}
