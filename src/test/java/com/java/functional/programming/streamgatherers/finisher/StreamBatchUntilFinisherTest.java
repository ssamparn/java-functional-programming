package com.java.functional.programming.streamgatherers.finisher;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

/**
 * Goal is to collect items in a batch and emit the batch when the given condition is met.
 * What is batchUntil?
 *  1. It collects elements from a stream into batches until a predicate is satisfied.
 *  2. Once the predicate returns true for an element, the current batch is emitted, and a new batch starts.
 *  3. Useful for scenarios like splitting logs, chunking data for APIs, or processing messages in groups.
 * */
@Slf4j
public class StreamBatchUntilFinisherTest {

    @Test
    public void batchUntilFinisherTest() {
        Stream.generate(() -> ThreadLocalRandom.current().nextInt(1, 100))
                .gather(batchUntil(i -> i % 3 == 0))
                .limit(10)
                .forEach(item -> log.info("{}", item));
    }

    private static <T> Gatherer<T, ?, List<T>> batchUntil(Predicate<T> predicate) {
        return Gatherer.ofSequential(
                () -> new BatchBuffer<T>(predicate),
                Gatherer.Integrator.ofGreedy(BatchBuffer::integrate),
                BatchBuffer::finish
        );
    }

    public static class BatchBuffer<T> {
        private final Predicate<T> predicate;
        private final List<T> list;

        public BatchBuffer(Predicate<T> predicate) {
            this.predicate = predicate;
            this.list = new ArrayList<>();
        }

        boolean integrate(T element, Gatherer.Downstream<? super List<T>> downstream) {
            this.list.add(element);
            if (this.predicate.negate().test(element)) {
                return true;
            }
            List<T> result = List.copyOf(this.list);
            this.list.clear();
            return downstream.push(result);
        }

        // invoked when the stream completes. either all items are processed or it short-circuits.
        void finish(Gatherer.Downstream<? super List<T>> downstream) {
            log.info("called finisher to flush leftover items (if any)");
            if (!this.list.isEmpty() && !downstream.isRejecting()) {
                downstream.push(List.copyOf(this.list)); // emit remainder
            }
        }
    }
}
