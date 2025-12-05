package com.java.functional.programming.streamgatherers.stateless;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.stream.Gatherer;
import java.util.stream.IntStream;

/**
 * Goal is to understand how integrator works with downstream
 * */
@Slf4j
public class StreamIntegratorWorkingWithDownstreamTest {

    /**
     * In Java 24+, Gatherer.of(Integrator) is a convenience factory method that builds a stateless gatherer
     * Note: Primitive streams don't have the gather() api.
     * */
    @Test
    public void integratorWorkingWithDownstreamSimpleTest() {
        IntStream.rangeClosed(1, 5)
                .boxed()
                .gather(Gatherer.of(new SimpleIntegrator()))
                .limit(2) // to simulate downstream short-circuiting, so that downstream signals it does not want anymore items.
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

            for (int i = 0; i < 5; i++) {
                log.info("before pushing. isRejecting: {}", downstream.isRejecting());
                boolean isResultForwarded = downstream.push(1); // forward element in a loop
                log.info("after pushing. isRejecting: {}, forwardedResult: {}", downstream.isRejecting(), isResultForwarded);
            }
            return true; // true: continue processing subsequent elements from upstream. false: don't continue processing subsequent elements.
        }
    }

    /**
     * V Imp Notes:
     * The default state will be null.
     * 'element' refers to the item received from the upstream in the stream pipeline.
     * The integrate() method will be invoked whenever we receive an item.
     * The integrate() method can process the item & optionally emit it downstream.
     * If integrate() method returns true, it indicates that it wants more items from upstream.
     * If integrate() method returns false, it indicates that no more items are needed from upstream (short-circuiting).
     * 'downstream.push(...)' is used to emit items. It returns a boolean to indicate if it wants more items.
     * 'downstream.isRejecting() is effectively equivalent to '!downstream.push(...)'. (there is a limitation when operators are chained)
     * There is an inverse relationship between isRejecting & forwardedResult.
     * When one is true, other has to be false & vice versa.
     * */
}
