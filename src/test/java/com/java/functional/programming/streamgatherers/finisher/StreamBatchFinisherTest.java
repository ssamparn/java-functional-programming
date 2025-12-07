package com.java.functional.programming.streamgatherers.finisher;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Gatherer;
import java.util.stream.IntStream;

/**
 * Goal is to collect items and emit them in batches
 */
@Slf4j
public class StreamBatchFinisherTest {

    @Test
    public void testStreamGathererIntroduction() {
        IntStream.rangeClosed(1, 10)
                .boxed()
                .gather(Batching.batch(4)) // implement a gatherer which will batch items with a given batch size
                .forEach(item -> log.info("{}", item));
    }

    private final class Batching {

        private Batching() {

        }

        /**
         * Create a sequential gatherer that groups elements into fixed-size batches.
         * The finisher flushes any partially filled final batch.
         */
        private static <T> Gatherer<T, BatchBuffer<T>, List<T>> batch(int batchSize) {
            if (batchSize <= 0) {
                throw new IllegalArgumentException("batchSize must be > 0");
            }

            return Gatherer.ofSequential(
                    () -> new BatchBuffer<>(batchSize),
                    Gatherer.Integrator.ofGreedy((batchBuffer, element, downstream) -> {
                        log.info("received element: {}", element);
                        batchBuffer.batch.add(element);
                        if (batchBuffer.batch.size() == batchBuffer.batchSize) {
                            downstream.push(List.copyOf(batchBuffer.batch)); // Emit a full batch downstream
                            batchBuffer.batch.clear(); // clear the buffer
                        }
                        return true; // keep consuming
                    }),
                    (batchBuffer, downstream) -> {
                        // FINISHER: flush leftover items (if any)
                        log.info("called finisher to flush leftover items (if any)");
                        if (!batchBuffer.batch.isEmpty() && !downstream.isRejecting()) {
                            downstream.push(List.copyOf(batchBuffer.batch)); // emit remainder
                        }
                    }
            );
        }

        /**
         * Internal state holder
         */
        static final class BatchBuffer<T> {
            private final int batchSize;
            private final List<T> batch;

            BatchBuffer(int batchSize) {
                this.batchSize = batchSize;
                this.batch = new ArrayList<>(batchSize);
            }
        }
    }
}
