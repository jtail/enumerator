package com.github.jtail.enumerator;

import com.github.jtail.enumerator.handlers.AbstractHandler;
import com.github.jtail.enumerator.handlers.Handler;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class MagicBox {

    private final Map<String, AbstractHandler<?>> handlers;

    public MagicBox(Map<String, Class<?>> beanIndex, Stream<AbstractHandler<?>> handlers) {
        this.handlers = Enumerator.indexConsumers(handlers, this::fromAnnotation, beanIndex);
    }

    private Optional<String> fromAnnotation(AbstractHandler<?> handler) {
        return Optional.ofNullable(handler.getClass().getAnnotation(Handler.class)).map(Handler::value);
    }

    @SuppressWarnings("unchecked")
    public <T> String handle(String type, T o) {
        return ((AbstractHandler<T>) handlers.get(type)).apply(o);
    }
}
