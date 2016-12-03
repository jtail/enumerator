package com.github.jtail.enumerator.handlers;

import com.github.jtail.enumerator.types.Mammal;

@Handler("wolf")
public class WolfHandler implements AbstractHandler<Mammal> {

    @Override
    public String apply(Mammal mammal) {
        return "Wolf eats a lot";
    }
}
