package ws15.sbc.factory.common.repository.xBased;

import ws15.sbc.factory.common.repository.EntityMatcher;

import java.io.Serializable;
import java.util.function.Consumer;

class ConditionalConsumerDecorator<T extends Serializable> implements Consumer<Serializable> {

    private final Consumer<T> consumer;
    private final EntityMatcher<T> matcher;

    public ConditionalConsumerDecorator(Consumer<T> consumer, EntityMatcher<T> matcher) {
        this.consumer = consumer;
        this.matcher = matcher;
    }

    @Override
    public void accept(Serializable t) {
        if (matcher.matches(t)) {
            consumer.accept((T) t); //guarded by matcher
        }
    }
}
