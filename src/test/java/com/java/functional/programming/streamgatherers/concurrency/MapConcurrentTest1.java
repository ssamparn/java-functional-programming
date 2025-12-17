package com.java.functional.programming.streamgatherers.concurrency;

import com.java.functional.programming.streamgatherers.concurrency.externalservice.RestClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.stream.Gatherers;
import java.util.stream.IntStream;

@Slf4j
public class MapConcurrentTest1 {

    /**
     * Ensure that the external service is up and running
     * */
    @Test
    public void mapConcurrentTest() {
        IntStream.rangeClosed(1, 50)
                .boxed()
                .gather(Gatherers.mapConcurrent(
                        10, RestClient::getProduct))
                .forEach(log::info);
    }

    /**
     * As we saw, even though many items finish quickly, the stream waits to emit until the next encounter‑ordered element is ready—so one slow task “delays the execution” of everything behind it.
     * That behavior is by design for mapConcurrent, which preserves encounter order while running the mapper concurrently on virtual threads.
     *
     * Why this happens?
     * mapConcurrent(maxConcurrency, mapper) runs up to maxConcurrency mapping tasks, but pushes results downstream in the original order.
     * If element i is slow and i+1 … i+n finish earlier, those faster results are buffered until i is done.
     * On large/unbounded streams this can look like “delays” and may even escalate into memory pressure.
     *
     * Let's fix these issues with execute concurrent.
     * */
}
