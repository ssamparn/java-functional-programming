package com.java.functional.programming.streamgatherers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

/**
 * Goal is to implement a flatmap() using a stream custom gatherer
 *
 */
@Slf4j
public class FlatMapWithGathererTest {

    record Order(String id, List<String> items) {

    }

    @Test
    public void flatMapWithStreamGathererTest() {
        List<Order> orders = List.of(
                new Order(UUID.randomUUID().toString(), List.of("Apple", "Banana")),
                new Order(UUID.randomUUID().toString(), List.of("Orange", "Grapes")),
                new Order(UUID.randomUUID().toString(), List.of("Mango"))
        );
        log.info("orders: {}", orders);

        // Use gatherer to flatten items
        orders.stream()
                .gather(flatMapGatherer(order -> order.items().stream()))
                .limit(3)
                .forEach(item -> log.info("received: {}", item));
    }

    private static <T, R> Gatherer<T, Void, R> flatMapGatherer(Function<T, Stream<R>> function) {
        return Gatherer.of(
                Gatherer.Integrator.ofGreedy(
                        (state, element, downstream) ->
                            function.apply(element)
                                    .peek(e -> log.info("emitting: {}", e))
                                    .allMatch(downstream::push)
                )
        );
    }
}
