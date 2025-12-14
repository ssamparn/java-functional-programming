package com.java.functional.programming.streamgatherers.combiner;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Gatherer;
import java.util.stream.IntStream;

/**
 * Goal is to create a custom gatherer to find the top / highest N items from a stream.
 * We implement this gatherer to better understand how state is handled in parallel stream processing.
 * We can use a comparator if needed
 * */
@Slf4j
public class TopNWithStreamGathererTest {

    @Test
    public void topNWithStreamGathererTest() {
        IntStream.rangeClosed(1, 100)
                .boxed()
                .parallel()
                .gather(topNGatherer(5))
                .forEach(integer -> log.info("Top N elements: {}", integer));
    }

    private static Gatherer<Integer, ?, Integer> topNGatherer(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be > 0");
        }
        return Gatherer.of(
                () -> new TopNItems(maxSize),
                Gatherer.Integrator.ofGreedy((state, element, downstream) -> state.integrate(element)),
                TopNItems::combine,
                (state, downstream) -> state.finish(downstream)
        );
    }

    private static class TopNItems {
        private final int maxSize;
        private final Queue<Integer> queue;

        TopNItems(int maxSize) {
            this.maxSize = maxSize;
            this.queue = new PriorityQueue<>(maxSize);
        }

        boolean integrate(Integer element) {
            if (element == null) return true;
            if (queue.size() < maxSize || element > queue.peek()) {
                if (queue.size() == maxSize) {
                    queue.poll(); // remove smallest
                }
                queue.offer(element);
            }
            return true;
        }

        TopNItems combine(TopNItems other) {
            if (other == this) return this;
            other.queue.forEach(this::integrate);
            return this;
        }

        void finish(Gatherer.Downstream<? super Integer> downstream) {
            if (queue.isEmpty()) return;
            // Sort in descending order before pushing
            final List<Integer> result = new ArrayList<>(queue);
            result.sort(Comparator.reverseOrder());
            result.forEach(downstream::push);
        }
    }
}