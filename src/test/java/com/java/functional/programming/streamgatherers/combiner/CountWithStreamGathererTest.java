package com.java.functional.programming.streamgatherers.combiner;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Gatherer;
import java.util.stream.IntStream;

/**
 * Goal is to create a custom gatherer to count the number of items in a stream.
 * Although Java provides a built-in count() terminal operation in stream api,
 * we implement this gatherer to better understand how state is handled in parallel stream processing.
 * */
@Slf4j
public class CountWithStreamGathererTest {

    @Test
    public void countWithCustomGathererTest() {
        IntStream.range(0, 10)
                .boxed()
                .parallel()
                .gather(countWithGatherer())
                .forEach(integer -> log.info("{}", integer));
    }

    /**
     * Key points for this gatherer:
     * State type (A): Integer (or AtomicInteger if we want thread-safe mutation).
     * Initializer: Start with 0L.
     * Integrator: For each element, increment the count.
     * Combiner: Merge two counts by summing them (needed for parallel streams).
     * Finisher: Push the final count downstream.
     * */
    private static <T> Gatherer<T, AtomicInteger, Integer> countWithGatherer() {
        return Gatherer.of(
                AtomicInteger::new,
                Gatherer.Integrator.ofGreedy((state, element, downstream) -> {
                    state.incrementAndGet();
                    return true;
                }),
                (left, right) -> {
                    left.addAndGet(right.get());
                    return left;
                },
                (state, downstream) -> downstream.push(state.get())
        );
    }

    @Test
    public void countWithCustomGathererCustomObjectStateTest() {
        IntStream.range(0, 10)
                .boxed()
                .parallel()
                .gather(countWithCustomObjectStateGatherer())
                .forEach(integer -> log.info("{}", integer));
    }

    private static <T> Gatherer<T, ?, Long> countWithCustomObjectStateGatherer() {
        return Gatherer.of(
                ItemsCounter::new,
                Gatherer.Integrator.ofGreedy((state, element, downstream) -> state.integrate()),
                ItemsCounter::combine,
                (itemsCounter, downstream) -> itemsCounter.finish(downstream)
        );
    }

    private static class ItemsCounter {
        private static final AtomicInteger atomicInteger = new AtomicInteger(0); // just to see how many thread instances are created
        private long count;

        ItemsCounter() {
            this.count = 0;
            log.info("State object instance count: {}", atomicInteger.incrementAndGet());
        }

        boolean integrate() {
            this.count++;
            return true;
        }

        ItemsCounter combine(ItemsCounter other) {
            log.info("combining {} - {}", this.count, other.count);
            this.count += other.count;
            return this;
        }

        void finish(Gatherer.Downstream<? super Long> downstream) {
            log.info("Called finish");
            downstream.push(this.count);
        }
    }
}
