package com.java.functional.programming.streamgatherers.combiner;

import lombok.extern.slf4j.Slf4j;

/**
 * Combiner:
 * Definition from the Jav API Doc:
 *      “Gatherer operations can be performed either sequentially, or be parallelized — if a combiner function is supplied.
 *      The library is free to partition the input, perform the integrations on the partitions, and then use the combiner function to combine the partial results.”
 *
 * So what is a Combiner?
 * A gatherer is defined by up to four functions: initializer → integrator → combiner → finisher.
 * During parallel evaluation, the stream may partition the input, run integrator on each partition with its own state, and then invoke the combiner to merge those per‑partition states into one state before optionally running the finisher.
 * Without a combiner, the library cannot safely merge partial states, so the gatherer remains sequential.
 * Oracle’s “Stream Gatherers” guide emphasizes the same: parallel processing occurs only if you specify a combiner; the default turns parallelization off even if you call parallel().
 *
 * When do I need a Combiner?
 *    1. When you want your gatherer to support parallel streams. If you call .parallel() and your gatherer lacks a combiner (e.g., created via Gatherer.ofSequential(...)), your stage is effectively sequential.
 *    Supplying a combiner via Gatherer.of(...) enables parallel merging.
 *    2. Your operation accumulates states (e.g., sets, buffers, windows, maps, counters) that must be merged across partitions.
 *    Examples: custom distinctByKey, batching with leftover carry‑over, top‑K aggregations, etc.
 *
 * How Combiner Works?
 * If we create a stateful gatherer, we can support parallel stream easily using combiner.
 * If you create a sequential gatherer, then we know that the supplier will be used to supply the state object once.
 * Then the integrate method will be invoked again and again for every element in the stream with the state object.
 * We might want to use parallel stream if we want to make use of multiple CPUs in our machine.
 * In that case, your stream source might be split into multiple smaller independent chunks so that each chunk can be assigned to a thread for the parallel processing.
 * The splitting process is handled by the Spliterator. Internally, it's an interface. There is a trySplit() which handles this.
 * If you want parallel stream support, then you should not be using the sequential gatherer.
 * Remember that when we use parallel stream, each thread will be using the supplier to create its own state object.
 * So the state object is not shared among the threads. Each thread has one state object, so we do not have to worry about locking, synchronization, etc.
 *
 * So what is the problem?
 * Why do we need the combiner since each thread has its own state object?
 * Since each thread executes on their own, they will have partial results. Each state object will have some result by processing the elements &
 * we need a mechanism to combine these states to build the final state.
 * So the combiner is a function (a binary operator) which defines how to merge two state objects into one.
 * So we will be given two state objects & we need to come up with the one merged state and return that.
 *
 * Let's say we have a list of numbers and we wanted to find the sum of these numbers. We used a parallel stream.
 * So the thread one will be finding the sum of these numbers as six. Thread two will find the sum of these numbers as 15, and so on.
 * So state one will be containing six, state two will be containing 15, so these two will be merged into one final state as 21 here.
 * Similarly these two will be merged as one state here. Then these two will be merged to bring the final state object with the sum 78.
 *
 * To quickly summarize, this is what you would want to remember via the supplier the initializer.
 * We create the new instance. Then we don't have to worry about synchronization. In that case you do not need atomic integer, you do not need concurrent HashMap, etc.
 *
 * Do you need parallel stream?
 *   - parallel() does NOT always make code fater.
 *   - It uses the common fork-join pool (shared & limited platform threads).
 *   - parallel() is best suited for CPU intensive tasks with large datasets.
 *   - For a smaller collection, the overhead of splitting the task into multiple subtasks, merging the results etc. can slow things down!
 *   - Avoid parallel() for I/O tasks (e.g: DB / API calls)
 *     - It has limited platform threads! Blocking these threads might worsen performance!
 *     - We need to use virtual threads for blocking I/O.
 *   - For most real-world business applications, sequential streams are more predictable and often sufficient.
 *
 * Is parallel stream right for you?
 *   Only use parallel stream if:
 *      - You are dealing with large datasets.
 *      - Your tasks are CPU-bound.
 *      - You have tested and have the proof that parallel() makes things faster, rather than just hoping it will!
 *
 * Parallelism: Stateless vs Stateful Operations:
 *   - Stateless operations like filter(), map(), or flatMap() are easy to parallelize.
 *   - Stateful operations often require maintaining or accumulating information across elements which makes parallel execution tricky.
 *     - We need to provide a combiner to merge intermediate results across threads.
 *   - Many stateful tasks (e.g: moving average, sliding window, breadth-first search) are inherently sequential that the result depends on strict element order.
 * */
@Slf4j
public class CombinerIntroduction {


}
