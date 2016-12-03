package com.github.jtail.enumerator;

import com.github.jtail.enumerator.handlers.AbstractHandler;
import com.github.jtail.enumerator.handlers.Handler;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class MagicBox {

    private final Map<String, AbstractHandler> handlers;

    public MagicBox(Map<String, Class<?>> beanIndex, Stream<AbstractHandler> handlers) {
        this.handlers = Enumerator.indexConsumers(
                Handler.class, Handler::value, handlers, AbstractHandler.class, beanIndex
        );
    }

    @SuppressWarnings("unchecked")
    public String handle(String type, Object o) {
        return ((Function<Object, String>) handlers.get(type)).apply(o);
    }
}
