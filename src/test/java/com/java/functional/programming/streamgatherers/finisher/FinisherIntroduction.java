package com.java.functional.programming.streamgatherers.finisher;

import lombok.extern.slf4j.Slf4j;

/**
 * Finisher in Gatherer API:
 * In the Java25 Stream Gatherer API, a Finisher is the optional fourth function of a Gatherer<T, A, R> interface,
 * which is called once the upstream has no more elements, or it short-circuits.
 * It plays a crucial role in finalizing the processing of accumulated state when the end of the upstream stream is reached.
 *
 * Signature: finisher() – BiConsumer<A, Downstream<R>>
 *    Performs a final action when no more input elements remain.
 *
 * Formally, a gatherer is defined by four cooperating functions:
 *    - initializer(): Creates mutable state
 *    - integrator(): Processes each input element and may emit output (mandatory)
 *    - combiner(): Merges states in parallel streams (optional)
 *    - finisher(): Performs the final action at end-of-input (optional)
 *
 * The JDK docs describe the canonical execution as: Initialize state → Integrate each element → Call finisher().accept(state, downstream) when done.
 *
 * Why the finisher matters:
 *   - Flush buffered output: If your gatherer batches items (e.g., windowing), the last, not-full batch can be emitted from finisher().
 *   - Emit a final result: State-heavy operations (e.g., accumulations, scans) may need to push one last value or summary.
 *   - Close out state safely: Ensures deterministic completion of stateful transformations even when no element triggers the final emission.
 * */
@Slf4j
public class FinisherIntroduction {

}
