package com.github.jtail.enumerator.handlers;

import com.github.jtail.enumerator.types.Bird;

@Handler(Bird.DUCK)
public class DuckHandler implements AbstractHandler<Bird> {

    @Override
    public String apply(Bird bird) {
        return "Cooking duck";
    }
}
