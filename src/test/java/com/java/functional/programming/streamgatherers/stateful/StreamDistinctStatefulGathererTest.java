package com.java.functional.programming.streamgatherers.stateful;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

/**
 * Goal is to create a simple stateful gatherer showing the distinct() operator behaviour of stream api.
 * */
@Slf4j
public class StreamDistinctStatefulGathererTest {

    @Test
    public void distinctWithStreamGathererTest() {
        Stream.of(1, 2, 2, 3, 1, 4, 3, 1, 5, 5, 2, 4)
                .gather(distinct()) // custom distinct gatherer
                .forEach(i -> log.info("received: {}", i));
    }

    /* *
     * Custom Gatherer that finds the distinct elements flowing downstream.
     * Uses a stateful Set to track seen elements.
     * */
    private static <T> Gatherer<T, Set<T>, T> distinct() {
        return Gatherer.ofSequential(
                HashSet::new, // Initializer: create a new HashSet for state
                (state, element, downstream) -> {
                    if (state.add(element)) { // Only push if not seen before
                        log.info("counter: {}, element: {}", state, element);
                        downstream.push(element);
                    }
                    return true; // Always continue
                }
        );
    }

    /**
     * The above distinct example is good for finite stream. But what if we have an infinite stream?
     * If we keep on adding items into our state, so will we not get out of memory error?
     * What if our requirement is slightly different? For example, we want to keep only the last n unique items.
     * Evict the oldest item when the cache (HashSet) is full.
     *
     * This requirement is essentially a bounded distinct operator:
     *   - Keep only the last N unique elements.
     *   - If the cache is full, evict the oldest element (like an LRU cache).
     *   - Still ensure uniqueness within the current window.
     * */
    @Test
    public void boundedDistinctWithStreamGathererTest() {
        Stream.of(1, 2, 2, 1, 3, 4, 3, 1, 5)
                .gather(distinctLastN(3))// custom bounded distinct gatherer
                .forEach(i -> log.info("received: {}", i));
    }


    /**
     * Custom Gatherer that keeps only the last N unique elements.
     * Evicts oldest when cache is full.
     * State: LinkedHashSet preserves insertion order.
     *   Logic:
     *    - If element is new, add it and push downstream.
     *    - If size exceeds n, remove the oldest element.
     * Memory Safety: Cache never grows beyond n.
     */
    private static <T> Gatherer<T, ?, T> distinctLastN(int size) {
        return Gatherer.ofSequential(
                () -> new LinkedHashSet<T>(), // Initializer: preserves the insertion order.
                ((cache, element, downstream) -> {
                    if (!cache.contains(element)) {
                        log.info("counter: {}, element: {}", cache, element);
                        cache.add(element);
                        downstream.push(element);
                        if (cache.size() > size) {
                            // Evict oldest element
                            var iterator = cache.iterator();
                            if (iterator.hasNext()) {
                                T oldest = iterator.next();
                                iterator.remove();
                                log.info("Evicted oldest: {}", oldest);
                            }
                        }
                    }
                    return true; // Continue processing
                }),
                (cache, downstream) -> {
                    log.info("Final cache: {}", cache); // Finalizer: log the final cache once
                }
        );
    }
}
