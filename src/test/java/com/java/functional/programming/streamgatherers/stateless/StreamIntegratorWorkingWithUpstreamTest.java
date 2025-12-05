package com.java.functional.programming.streamgatherers.stateless;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.stream.Gatherer;
import java.util.stream.IntStream;

/**
 * Goal is to understand how integrator works with upstream
 * */
@Slf4j
public class StreamIntegratorWorkingWithUpstreamTest {

    /**
     * In Java 24+, Gatherer.of(Integrator) is a convenience factory method that builds a stateless gatherer
     * Note: Primitive streams don't have the gather() api.
     * */
    @Test
    public void integratorWorkingWithUpstreamSimpleTest() {
        IntStream.rangeClosed(1, 5)
                .boxed()
                .gather(Gatherer.of(new SimpleIntegrator()))
                .forEach(item -> log.info("received: {}", item));
    }

    /**
     * Example of a stateless gatherer with:
     * A = Void (no state as it is a stateless gatherer)
     * A no-op initializer (state is always null)
     * An identity finisher
     * A trivial combiner (unused since Void)
     * Provided Integrator for the per-element behavior which simply forwards upstream elements downstream while logging them.
     *
     * Return value of Integrator:
     * Returning boolean true from the integrator tells the pipeline to keep processing.
     * If you return false, it short-circuits the upstream (no more elements are pulled). That’s handy for “take N” or “stop on condition” logic.
     *
     * Downstream control:
     * downstream.push(element) forwards a result element.
     * You can choose to push zero, one, or many outputs per input, enabling transforms like filtering, flat-mapping, batching, etc.
     *
     * Parallel streams:
     * With Gatherer.of(Integrator), state is Void, so the combiner is trivial.
     * If you need shared state or parallel-friendly behavior, use the fuller factory overload and provide initializer, combiner, and finisher.
     * */
    private static class SimpleIntegrator implements Gatherer.Integrator<Void, Integer, Integer> {

        @Override
        public boolean integrate(Void state, Integer element, Gatherer.Downstream<? super Integer> downstream) {
            log.info("state: {}, element: {}", state, element); // state is always null (Void return type) as we are creating a stateless gatherer
            downstream.push(element); // forward element unchanged
            return true; // true: continue processing subsequent elements from upstream. false: don't continue processing subsequent elements.
        }
    }
}
