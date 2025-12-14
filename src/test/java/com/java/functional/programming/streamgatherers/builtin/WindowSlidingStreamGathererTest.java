package com.java.functional.programming.streamgatherers.builtin;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.stream.Gatherers;
import java.util.stream.IntStream;

/**
 * What is Gatherers.windowSliding(int windowSize):
 * Window Sliding in Java Stream Gatherers is a built‑in operation that produces overlapping, encounter‑ordered windows (lists) of a fixed size from an upstream stream.
 * Each window contains the last n elements seen so far, sliding forward by one element at a time.
 * This is ideal for moving‑window analytics like moving averages, rolling sums, or pattern detection.
 *
 * Typical use cases:
 *   - Time‑series analytics: moving average/median, rolling sum, Bollinger bands, anomaly detection.
 *   - Signal processing / logs: detect sequences (e.g., “3 consecutive errors”), rate limiting patterns.
 *   - Text/sequence processing: n‑grams over tokens/characters, substring/windowed matching.
 * */
@Slf4j
public class WindowSlidingStreamGathererTest {

    @Test
    public void windowFixedGathererTest() {
        IntStream.rangeClosed(0,10)
                .boxed()
                .gather(Gatherers.windowSliding(3))
                .forEach(item -> log.info("Item: {}", item));
    }
}