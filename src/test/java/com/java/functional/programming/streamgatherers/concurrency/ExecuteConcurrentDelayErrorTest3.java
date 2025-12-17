package com.java.functional.programming.streamgatherers.concurrency;

import com.java.functional.programming.streamgatherers.concurrency.externalservice.RestClient;
import com.java.functional.programming.streamgatherers.concurrency.util.GatherersUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

/* *
 * Creates a custom gatherer that sends I/O requests concurrently and emits results in the order they complete;
 * Not in the order they were received.
 * */
@Slf4j
public class ExecuteConcurrentDelayErrorTest3 {

    /**
     * Ensure that the external service is up and running.
     * To replicate error scenario, make product service call with .executeConcurrent() or .mapConcurrent() with productId: 0
     * */
    @Test
    public void executeConcurrentTest() {
        IntStream.rangeClosed(1, 50)
                .boxed()
                .gather(GatherersUtil.executeConcurrentDelayError(
                        10, RestClient::getProduct))
                .forEach(log::info);
    }
}