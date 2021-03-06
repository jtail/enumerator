package com.github.jtail.enumerator.handlers;

import com.github.jtail.enumerator.types.Mammal;

@Handler(Mammal.BEAR)
public class BearHandler implements AbstractHandler<Mammal> {
    @Override
    public String apply(Mammal mammal) {
        return "Running from bear";
    }
}
