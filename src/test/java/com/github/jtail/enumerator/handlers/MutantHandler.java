package com.github.jtail.enumerator.handlers;

import com.github.jtail.enumerator.types.Bird;
import com.github.jtail.enumerator.types.Mammal;

@Handler(Bird.EAGLE)
public class MutantHandler implements AbstractHandler<Mammal> {

    @Override
    public String apply(Mammal mammal) {
        return "Beware of the mutants";
    }
}
