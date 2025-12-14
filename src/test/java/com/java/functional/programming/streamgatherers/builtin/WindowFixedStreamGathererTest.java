package com.java.functional.programming.streamgatherers.builtin;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.stream.Gatherers;
import java.util.stream.IntStream;

/**
 * What is Gatherers.windowFixed(int windowSize):
 *    It is a built‑in gatherer that groups the upstream elements into consecutive, fixed‑size windows (lists) and pushes each window downstream as soon as it is full.
 *    The final window may be smaller if the input size isn’t a multiple of windowSize. The windows are encounter‑ordered and each produced window is an unmodifiable List.
 *
 * When to use windowFixed?
 *    1) Batching for I/O and APIs:
 *       Bulk DB writes (e.g: insert/update in chunks of N), HTTP batching, or message publishing in groups to respect service limits and reduce round trips.
 *       The gatherer gives you contiguous, fixed‑size slices directly in the pipeline.
 *    2) Chunked file/stream processing:
 *       Read a large stream (lines, records, bytes mapped to objects) and handle chunks (e.g., compress/write a block, compute a checksum per block, or stage per‑chunk transformation).
 *       The fixed windows make it trivial to apply per‑chunk logic.
 *    3) Micro‑batch analytics:
 *       Apply an operation per window: per‑batch aggregate, validation, dedup within batch, or rate‑limited transforms.
 *       For overlapping (moving) computations (e.g., moving average), use windowSliding instead; windowFixed is for non‑overlapping groups.
 *    4) Parallelizable staging:
 *       You can keep the pipeline parallel and then map each window independently
 *       e.g., CPU‑heavy transform per block), while retaining deterministic window contents and encounter order at the gatherer level.
 *       Parallel behavior is part of gatherers’ design; the JEP and docs clarify gatherers support parallel pipelines when a combiner exists—built‑ins provide that.
 *    5) Pagination / UI pages:
 *       Produce pages of size N from a stream (e.g., search results) and render each page separately.
 *       The unmodifiable list per window is convenient to pass to view layers.
 * */
@Slf4j
public class WindowFixedStreamGathererTest {

    @Test
    public void windowFixedGathererTest() {
        IntStream.rangeClosed(0,10)
                .boxed()
                .gather(Gatherers.windowFixed(3))
                .forEach(item -> log.info("Item: {}", item));
    }
}