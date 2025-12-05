package com.java.functional.programming.streamgatherers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

/**
 * Goal is to implement a takeUntil() using a stream custom gatherer.
 * Requirement:
 *     1. Build a custom sequential Gatherer that allows elements to flow downstream until a condition becomes true.
 *     2. It must: Include the first item that satisfies the condition.
 *                 Stop the stream immediately after that.
 *
 * */
@Slf4j
public class TakeUntilWithGathererTest {

    @Test
    public void takeUntilWithStreamGathererTest() {
        Stream.of(1, 3, 5, 6, 7, 8, 10)
                .gather(takeUntil((Integer item) -> item % 2 == 0)) // Stop when first even number appears
                .forEach(i -> log.info("received: {}", i));
    }


    /**
     * Custom Gatherer that stops after condition becomes true (including that element).
     * This is sequential, short-circuits immediately after the first match, and includes the matching element.
     */
    private static <T> Gatherer<T, Boolean, T> takeUntil(Predicate<T> condition) {
        return Gatherer.ofSequential(
                () -> false,
                (state, element, downstream) -> {
                    downstream.push(element);
                    if (condition.test(element)) {
                        log.info("Condition met at: {}", element);
                        return false; // Stop processing further elements
                    }
                    return true; // Continue
                }
        );
    }
}
