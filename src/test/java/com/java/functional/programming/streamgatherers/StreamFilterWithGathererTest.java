package com.java.functional.programming.streamgatherers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

/**
 * Goal is to implement a custom Gatherer to simulate filter behaviour.
 * */
@Slf4j
public class StreamFilterWithGathererTest {

    @Test
    public void test() {
        IntStream.rangeClosed(1, 10)
                .boxed()
                .filter(i -> i % 2 == 0)
                .forEach(item -> log.info("received: {}", item));
    }
}
