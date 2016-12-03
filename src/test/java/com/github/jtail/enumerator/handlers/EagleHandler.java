package com.github.jtail.enumerator.handlers;

import com.github.jtail.enumerator.types.Bird;

@Handler(Bird.EAGLE)
public class EagleHandler implements AbstractHandler<Bird> {
    @Override
    public String apply(Bird bird) {
        return "Hunting with eagle";
    }
}
