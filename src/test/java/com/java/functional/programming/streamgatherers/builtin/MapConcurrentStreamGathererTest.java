package com.java.functional.programming.streamgatherers.builtin;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.stream.Gatherers;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

/**
 * mapConcurrent():
 *   - Gatherers.mapConcurrent(maxConcurrency, functionMapper) is a built‑in Stream Gatherer that applies mapper to each element concurrently.
 *   - It uses virtual threads to execute the function while preserving encounter order in the downstream stream, while rest of the pipeline gets executed by main thread.
 *   - You control the level of concurrency with maxConcurrency parameter, and you use it via the new intermediate op stream.gather(...).
 *   - It’s ideal when you want async/concurrent mapping (e.g., IO calls) but still want a simple, sequential-looking stream pipeline and ordered results.
 *
 * Where to use (and not to use) mapConcurrent()?
 *  Use it when:
 *    - IO‑bound work dominates (HTTP calls, DB queries, filesystem, RPC). Virtual threads + concurrency can yield simple, scalable pipelines with ordered results.
 *    - You want ordered results but don’t want to rewrite code with CompletableFuture orchestration.
 *    - You need a fixed concurrency pattern inside an otherwise sequential pipeline (i.e., you don’t want/need a fully parallel stream across the whole pipeline).
 *      This as a major advantage over toggling parallel().
 *
 * Prefer something else when:
 *    - The work is CPU‑bound and benefits from fork-join style parallel streams (or explicit thread pools).
 *    - mapConcurrent() still uses virtual threads (good for blocking) and preserves ordering, which can add overhead for pure CPU tasks.
 *    - Consider parallel() pipelines or custom executors in those cases. (Background: Gatherers provide new extension points; parallelization in streams traditionally comes from parallel() and spliterators.)
 *    - You don’t need ordered results. Then parallel() + unordered collectors may be faster/simpler.
 * */
@Slf4j
public class MapConcurrentStreamGathererTest {

    /**
     * This runs up to 4 concurrent mapping tasks while keeping the results in order.
     * */
    @Test
    public void mapConcurrentStreamGathererBasicUsageTest() {
        List<Integer> integers = Stream.of(1, 2, 3, 4, 5)
                .peek(integer -> log.info("{}", integer))
                .gather(Gatherers.mapConcurrent(4, x -> x * 2))
                .toList();
        assertThat(integers, hasItems(2, 4, 6, 8, 10));
    }

    record UserProfile(int id, String name) {

    }

    private UserProfile fetchProfile(int id) {
        try {
            // blocking IO (DB/HTTP) simulated here
            Thread.sleep(Duration.ofSeconds(1));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("Fetching profile {}", id);
        return new UserProfile(id, "User-" + id);
    }

    /**
     * Because it uses virtual threads, you can dial concurrency high for blocking operations without hogging platform threads.
     * This is the canonical scenario where mapConcurrent shines.
     * */
    @Test
    public void mapConcurrentStreamGathererIOBoundPipelinesTest() {
        IntStream.rangeClosed(1, 100)
                .boxed()
                .peek(id -> log.info("id: {}", id))
                .gather(Gatherers.mapConcurrent(100, this::fetchProfile)) // Up to 100 virtual threads. Since only fetchProfile() is executed by virtual thread, rest of the stream pipeline is executed by main thread.
                .forEach(profile -> log.info("{}", profile));
    }

    /**
     * Stream.parallel() vs Stream.mapConcurrent():
     * Gatherers.mapConcurrent(maxConcurrency, mapper):
     *   - Adds a bounded, ordered, concurrent mapping stage inside a sequential stream pipeline.
     *   - Uses virtual threads to run up to maxConcurrency mapper calls at once.
     *   - Preserves encounter order of results.
     *   - Best for IO‑bound tasks (HTTP/DB/file), where many operations block and virtual threads shine.

     * parallel() (parallel streams):
     *   - Makes the entire pipeline evaluate in parallel using the Fork/Join common pool.
     *   - Concurrency level is implementation‑driven (number of cores, spliterator characteristics), not explicitly bounded per stage.
     *   - Ordering can change unless you enforce it (e.g., forEachOrdered).
     *   - Best for CPU‑bound transformations where data can be partitioned efficiently.
     *
     * When to choose which?
     *   - Choose mapConcurrent when you have blocking IO and want order‑preserving concurrency with a clear cap (e.g., “no more than 64 calls at once”), without parallelizing the entire pipeline.
     *   - Choose parallel() when your computation is CPU‑intensive, associative, and benefits from splitting the data across cores; be mindful of ordering and side effects.
     *
     * Bottom line:
     *   Use mapConcurrent() when you need bounded, ordered concurrency inside a pipeline, particularly for IO‑bound operations—cleaner than juggling CompletableFutures and more efficient thanks to virtual threads.
     *   Use parallel() when your workload is CPU‑bound, associative, and benefits from data parallelism across cores, and you’re okay with managing ordering/side‑effects appropriately.
     * */
}
