package com.java.functional.programming.streamgatherers;

import lombok.extern.slf4j.Slf4j;

/**
 * Need for StreamGatherers:
 *
 * Java Streams API:
 *   - Java Streams API was introduced in Java 8.
 *   - It completely changed how we process collections and data.
 *   - It also inspired many other technologies like Kafka Streams API and a reactive programming libraries, etc.
 *   - It brought a new declarative functional style of programming for us Java developers.
 *
 * But it had some limitations. Java Stream was powerful, but it was not flexible. It lacked extensibility.
 * For example, when it was introduced at the time, we had map(), flatMap(), filter(), etc. which are great operators.
 * but modern applications required something like take() or batch().
 * Streams API did not have these features which was something fundamental. Later they added as part of JDK 9.
 * Then developers were looking for something like this batch(). That is when we have a large list or stream of items.
 * We might want to group items into batches of 100 so that instead of processing one by one, we could process 100 items at once, for example inserting into database.
 * But it was not supported.
 *
 * Sometimes people did not understand parallel() API of java streams properly.
 * For example, if they have a list of URLs they would use parallel to send the request and receive the response as part of the stream pipeline.
 * They thought the parallel would improve the performance, but this parallel was designed for CPU intensive task, not for I/O operations or network calls.
 * So oftentimes it degraded the performance because of thread starvation and blocking network calls.
 *
 * So the Java stream was great for simple in-memory data processing,
 * but we were 1. Unable to batch items,
 *             2. Could not control concurrency
 *             3. It lacked extensibility. We could not come up with our own custom operator.
 * Basically, we were forced to use whatever Java team provided as part of the streams API.
 *
 * Finally, Java Team has introduced a gatherer API which will allow us to extend the stream API.
 * Using this now like filter(), map() or flatMap(), we can create our own operator.
 *
 * For example, if we have a list of stock prices, we can create something called movingAverage().
 * Our domain specific operator to compute the 200-day moving average. We could not do that before.
 * Now we can have an elegant reusable solution like this using the gatherer API.
 *
 * Similarly, if we have a list of URLs, say 100 URLs or thousands of URLs, I can have an operator called executeConcurrent()
 * which will be sending hundreds of concurrent requests using virtual threads under the hood, and emit responses via the stream pipeline.
 *
 * Sometimes our requirements could be like this. That is, send hundreds of concurrent requests, but take the ten responses which came first.
 *
 * So we can write it like this:
 *  urls.stream()
 *      .gather(executeConcurrent())
 *      .limit(10)
 *      .map(this::enrich)
 *      ....

 * It will be sending hundreds of concurrent requests but will take the first 10 responses. Then it will be canceling the remaining 90 in-progress requests.
 * The cool thing here is it's reusable. Your team members might not be comfortable with the virtual threads or concurrency concepts, but they can still use these easily because all the multithreading complexities are hidden and abstracted.
 *
 * Gather - Intermediate Operator:
 * Java Stream has a new operator called gather().
 *
 * There is an interface called Gatherer. We will have to provide the implementation of this interface to have our custom behavior in the stream pipeline.
 * Gatherer interface has 4 components
 *      1. Initializer (Optional):
 *         Initializer is responsible for providing the initial state.
 *         If our custom operator is going to be a stateful operator, then we need an object to maintain the state called a state object or a container or accumulator.
 *         The initializer will supply the accumulator object.
 *
 *      2. Integrator (Mandatory):
 *         Integrator is the mandatory component of a gatherer.
 *         It is basically a method or behavior which we have to implement.
 *         It is going to be executed for each and every element we receive via the stream pipeline.
 *         As part of our integrator implementation, it's completely up to us to decide if we can pass the item downstream or not.
 *
 *      3. Finisher (Optional):
 *         Finisher is the another optional method which can be executed once all the elements are processed in the stream pipeline.
 *         We can use this to push the final result if we have any, or perform cleanup, etc.
 *
 *      4. Combiner (Optional):
 *         When we have a stateful operator, and you want to use parallel streams, then we would be dealing with the multiple threads, right?
 *         Each thread will have partial results. We have to carefully merge or combine them from different threads into a single state.
 * */
@Slf4j
public class StreamGathererIntroduction {

}
