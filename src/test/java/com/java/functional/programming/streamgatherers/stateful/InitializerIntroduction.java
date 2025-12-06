package com.java.functional.programming.streamgatherers.stateful;

import lombok.extern.slf4j.Slf4j;

/**
 * Initializer:
 * In the Java Stream Gatherer API, the Initializer is the component responsible for creating the initial state for the gatherer before processing any elements.
 * Basically it is used to create stateful custom operators.
 * We have many stateful operators like for example
 *   - limit() operator: It tracks how many items it has emitted so far so that it can stop once the count is reached.
 *   - distinct() operator: It tracks the items it has seen so far so that it can emit only the unique items.
 * So to maintain the state & to track that information for stateful processing, we need a container, an object which is basically initializer.
 * An object initializer is the component responsible for providing that container.
 * Initializer is simply a supplier Initializer(Supplier<T>).
 * It creates the initial empty object for the gathering operation.
 * It is a mutable object because we might want to update the state as we process items in the stream.
 *
 * We will be creating sequential gatherers because we are dealing with stateful object.
 * Now, supporting parallel execution is not really hard, but we also have to define a combiner which will be seen later.
 * */
@Slf4j
public class InitializerIntroduction {

}
