package com.java.functional.programming.streamgatherers.stateful;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

/**
 * Goal is to create a simple stateful gatherer showing the limit() operator behaviour of stream api.
 *
 * Before that few points worth mentioning regarding initializer.
 *    1. In the Java Stream Gatherer API, the Initializer is the component responsible for creating the initial state for the gatherer before processing any elements.
 *    2. A Gatherer can maintain internal state while processing elements (e.g., counters, buffers, accumulators).
 *    3. The Initializer is a function that creates this state object at the start of the stream pipeline.
 *    4. It’s passed as the first argument when you build a gatherer using Gatherer.of(...) or Gatherer.ofSequential(...).
 *
 * Signature: Supplier<A> initializer.
 *
 * Why is it important?
 *   - Without an initializer, you can’t maintain state across elements.
 *   - It enables stateful operations like:
 *      - Grouping elements.
 *      - Counting occurrences.
 *      - Building custom collectors.
 * */
@Slf4j
public class StreamLimitStatefulGathererTest {

    @Test
    public void limitWithArrayInitializerGathererTest() {
        Stream.of(1, 2, 3, 4, 5, 6, 7)
                .gather(limitImplWithArray(3)) // custom limit gatherer
                .forEach(i -> log.info("received: {}", i));
    }

    /* *
     * Custom Gatherer that limits the number of elements flowing downstream.
     * */
    private static <T> Gatherer<T, int[], T> limitImplWithArray(int max) {
        return Gatherer.ofSequential(
                () -> {
                    log.info("creating state counter");
                    return new int[]{0}; // mutable container
                },
                (state, element, downstream) -> {
                    log.info("counter: {}, element: {}", state, element);
                    if (state[0] < max) {
                        state[0]++;
                        return downstream.push(element) && state[0] < max; // Should continue next iteration? Stop processing after reaching max.
                    }
                    return false; // Short-circuit
                }
        );
    }

    @Test
    public void limitWithAtomicIntegerInitializerGathererTest() {
        Stream.of(1, 2, 3, 4, 5, 6, 7)
                .gather(limitImplWithAtomicInteger(3)) // custom limit gatherer
                .forEach(i -> log.info("received: {}", i));
    }

    /**
     * Custom Gatherer that limits the number of elements flowing downstream.
     * */
    private static <T> Gatherer<T, AtomicInteger, T> limitImplWithAtomicInteger(int max) {
        return Gatherer.ofSequential(
                () -> {
                    log.info("creating state counter");
                    return new AtomicInteger(0); // Initializer
                },
                (counter, element, downstream) -> {
                    log.info("counter: {}, element: {}", counter, element);
                    if (counter.get() < max) {
                        downstream.push(element);
                        counter.incrementAndGet();
                        return counter.get() < max; // Stop processing after reaching max
                    }
                    return false; // Short-circuit
                }
        );
    }
}
