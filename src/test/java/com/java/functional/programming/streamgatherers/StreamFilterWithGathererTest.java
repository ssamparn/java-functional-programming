package com.java.functional.programming.streamgatherers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Gatherer;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Goal is to implement a custom Gatherer to simulate filter behaviour.
 */
@Slf4j
public class StreamFilterWithGathererTest {

    @Test
    public void streamFilterTest() {
        List<Integer> evens = IntStream.rangeClosed(1, 10)
                .boxed()
                .filter(i -> i % 2 == 0) // Here filter is for allowing only even numbers. But Imagine Java team has not provided the filter() api.
                .toList();
        assertThat(evens).containsExactly(2, 4, 6, 8, 10);
    }

    @Test
    public void streamFilterWithIntegratorTest() {
        IntStream.rangeClosed(1, 10)
                .boxed()
                .gather(Gatherer.of(new EvenFilter()))
                .forEach(item -> log.info("received: {}", item));
    }

    private static class EvenFilter implements Gatherer.Integrator<Void, Integer, Integer> {
        @Override
        public boolean integrate(Void state, Integer element, Gatherer.Downstream<? super Integer> downstream) {
            log.info("element: {}", element);
            if (element % 2 == 0) {
                downstream.push(element); // Emit only even numbers
            }
            return true; // Continue processing
        }
    }

    @Test
    public void streamFilterWithGathererTest() {
        IntStream.rangeClosed(1, 10)
                .boxed()
                .gather(filter())
                .forEach(item -> log.info("received: {}", item));
    }

    private static Gatherer<Integer, Void, Integer> filter() {
        return Gatherer.of((state, element, downstream) -> {
            if (element % 2 == 0) {
                downstream.push(element);
            }
            return true;
        });
    }

    @Test
    public void streamFilterWithGenericGathererTest() {
        List<Integer> evens = IntStream.rangeClosed(1, 10)
                .boxed()
                .parallel()
                .gather(genericFilter(i -> i % 2 == 0))
                .toList();

        assertThat(evens).containsExactly(2, 4, 6, 8, 10);
    }

    private static <T> Gatherer<T, Void, T> genericFilter(Predicate<T> predicate) {
        return Gatherer.of((state, element, downstream) -> {
            if (predicate.test(element)) {
                log.info("element: {}", element);
                downstream.push(element);
            }
            return true;
        });
    }

    /**
     * Since gather() is a stateless operator, we can use parallel streams as well,
     * which will be executed on common fork-join pool.
     * */
}
