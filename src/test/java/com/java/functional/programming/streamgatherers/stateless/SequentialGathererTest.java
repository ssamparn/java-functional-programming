package com.java.functional.programming.streamgatherers.stateless;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;
import java.util.stream.Gatherer;
import java.util.stream.IntStream;

/**
 * Goal is to understand how sequential gatherer works.
 * Sequential Gatherer:
 *     1. In Java’s Gatherer API, a Sequential Gatherer refers to a gatherer that processes elements in the order they appear in the stream,
 *        without parallelization or reordering.
 *     2. Sequential means the gatherer guarantees ordered processing—each element is handled one after another, respecting the stream’s encounter order.
 *     3. This is important when:
 *          a. The gatherer maintains state that depends on previous elements (e.g., cumulative sums, grouping).
 *          b. You need deterministic results for ordered streams.
 *     4. By default, gatherers are sequential unless explicitly designed for parallel execution.
 *        If a gatherer is not thread-safe, it must be sequential.
 *
 * Why does it matter?
 *   In parallel streams, gatherers can run concurrently if they support it.
 *   A Sequential Gatherer signals that it cannot safely run in parallel, so the Stream framework will execute it in a single-threaded manner.
 */
@Slf4j
public class SequentialGathererTest {

    @Test
    public void sequentialGathererTest() {
        IntStream.rangeClosed(1, 10)
                .boxed()
                .parallel()
                .gather(genericFilterWithSequentialGatherer(i -> i % 2 == 0))
                .forEach(item -> log.info("received: {}", item)); // Multiple threads (Common Fork-Join pool) will receive the items because of parallel().
    }

    private static <T> Gatherer<T, Void, T> genericFilterWithSequentialGatherer(Predicate<T> predicate) {
        return Gatherer.ofSequential(
                Gatherer.Integrator.ofGreedy(
                        (state, element, downstream) -> {
                            log.info("element: {}", element);
                            if (predicate.test(element)) {
                                downstream.push(element); // A single thread will push the element. Without Gatherer.ofSequential() that is with only Gatherer.of() multiple threads (Common Fork-Join pool) will push the elements.
                            }
                            return true;
                        }
                )
        );
    }

    /**
     * In some cases, depending on the requirement, you might want to create a gatherer where you do not want to support items processing in parallel.
     * Instead, you want to explicitly say that this gatherer is for processing items sequentially one by one.
     * If you want to create such gatherer, it's very easy to create. Basically instead of using Gatherer.of(), you have to use Gatherer.ofSequential() factory method.
     * A sequential gatherer is not for parallel processing. It will be processing all the items sequentially one by one with one thread.
     * */
}
