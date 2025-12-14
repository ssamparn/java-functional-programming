package com.java.functional.programming.streamgatherers.combiner;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

/**
 * Goal is to create a custom gatherer to find the max integer of in a stream integers.
 * Although Java provides a built-in max() terminal operation in stream api,
 * we implement this gatherer to better understand how state is handled in parallel stream processing.
 * We can use a comparator if needed
 */
@Slf4j
public class MaxWithStreamGathererTest {

    @Test
    public void maxWithCustomGathererTest() {
        Stream.of(10, 25, 3, 47, 18, 95)
                .parallel()
                .gather(maxWithGatherer())
                .forEach(integer -> log.info("Max: {}", integer));
    }

    private static Gatherer<Integer, ?, Integer> maxWithGatherer() {
        return Gatherer.of(
                () -> new AtomicInteger(Integer.MIN_VALUE),
                Gatherer.Integrator.ofGreedy((state, element, downstream) -> {
                    state.updateAndGet(current -> Math.max(current, element));
                    return true;
                }),
                (left, right) -> {
                    left.updateAndGet(lv -> Math.max(lv, right.get()));
                    return left;
                },
                (state, downstream) -> downstream.push(state.get())
        );
    }

    @Test
    public void maxWithCustomGathererCustomObjectStateTest() {
        Stream.of(10, 25, 3, 47, 18, 95)
                .parallel()
                .gather(maxWithCustomObjectStateGatherer())
                .forEach(integer -> log.info("Max: {}", integer));
    }

    private static Gatherer<Integer, ?, Integer> maxWithCustomObjectStateGatherer() {
        return Gatherer.of(
                ItemsMax::new,
                Gatherer.Integrator.ofGreedy((state, element, downstream) -> state.integrate(element)),
                ItemsMax::combine,
                ((itemsMax, downstream) -> itemsMax.finish(downstream))
        );
    }

    private static class ItemsMax {
        private int max;

        ItemsMax() {
            this.max = Integer.MIN_VALUE;
        }

        boolean integrate(Integer element) {
            if (element != null) {
                max = Math.max(max, element);
            }
            return true;
        }

        ItemsMax combine(ItemsMax other) {
            this.max = Math.max(this.max, other.max);
            return this;
        }

        void finish(Gatherer.Downstream<? super Integer> downstream) {
            downstream.push(this.max);
        }
    }
}