package com.java.functional.programming.streamgatherers.concurrency.util;

import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Gatherer;

/**
 * Executes the given tasks concurrently using virtual threads. It does NOT delay the errors.
 * */
public class ExecuteConcurrent<T, R> {

    private int taskCount;
    private final int maxConcurrency;
    private final Function<T, R> mapperFunction;
    private final ExecutorService executorService;
    private final ExecutorCompletionService<R> completionService;

    public ExecuteConcurrent(final int maxConcurrency,
                             final Function<T, R> mapperFunction,
                             final ExecutorService executorService) {
        this.maxConcurrency = maxConcurrency;
        this.mapperFunction = mapperFunction;
        this.executorService = executorService;
        this.completionService = new ExecutorCompletionService<>(executorService);
    }

    /**
     * Notes about ExecutorCompletionService:
     * ExecutorCompletionService is a utility that helps you submit a bunch of Callable tasks to an Executor and then retrieve their results as they complete, rather than in the order they were submitted.
     * It essentially combines:
     *   - an Executor (e.g., a thread pool) that runs your tasks, and
     *   - a completion queue (BlockingQueue<Future<V>>) where task results are placed, as and when the tasks are completed.
     * This pattern is great when task durations vary, and you want to process fast results immediately without waiting for slow ones.
     * */

    boolean integrate(T element, Gatherer.Downstream<? super R> downstream) {
        this.completionService.submit(() -> mapperFunction.apply(element));
        taskCount++;
        if (taskCount < maxConcurrency) {
            return true; // we can accept more
        }
        // capacity is full. we must emit at least 1 more result to accept 1 more item from upstream.
        taskCount--;
        return downstream.push(this.takeNextCompletedResult());
    }

    void finish(Gatherer.Downstream<? super R> downstream) {
        boolean shouldContinue = !downstream.isRejecting();

        for (int i = 0; i < taskCount && shouldContinue; i++) {
            shouldContinue = downstream.push(this.takeNextCompletedResult());
        }
        this.executorService.shutdownNow(); // it will cancel if there are any pending tasks
    }

    /**
     * get the item (completed result) from the completion queue
     * */
    private R takeNextCompletedResult() {
        try {
            return this.completionService.take().get(); // take() is blocking
        } catch (Exception e) {
            this.executorService.shutdownNow();
            throw new RuntimeException(e);
        }
    }
}
