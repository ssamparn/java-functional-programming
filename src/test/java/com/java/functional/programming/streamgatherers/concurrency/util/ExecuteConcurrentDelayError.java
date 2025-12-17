package com.java.functional.programming.streamgatherers.concurrency.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Gatherer;

/**
 * Executes the given tasks concurrently using virtual threads.
 * It emits successful results first and then (delay the errors) throws exception at the end!
 * */
public class ExecuteConcurrentDelayError<T, R> {

    private int taskCount;
    private final int maxConcurrency;
    private final Function<T, R> mapperFunction;
    private final ExecutorService executorService;
    private final ExecutorCompletionService<R> completionService;
    private final List<Throwable> errors;

    public ExecuteConcurrentDelayError(final int maxConcurrency,
                                       final Function<T, R> mapperFunction,
                                       final ExecutorService executorService) {
        this.maxConcurrency = maxConcurrency;
        this.mapperFunction = mapperFunction;
        this.executorService = executorService;
        this.completionService = new ExecutorCompletionService<>(executorService);
        this.errors = new ArrayList<>();;
    }

    boolean integrate(T element, Gatherer.Downstream<? super R> downstream) {
        this.completionService.submit(() -> mapperFunction.apply(element));
        taskCount++;
        if (taskCount < maxConcurrency) {
            return true; // we can accept more
        }
        // capacity is full. we must emit at least 1 more result to accept 1 more item from upstream.
        taskCount--;
        return this.emitNextCompletedResult(downstream);
    }

    void finish(Gatherer.Downstream<? super R> downstream) {
        var shouldContinue = !downstream.isRejecting();
        for (int i = 0; i < taskCount && shouldContinue; i++) {
            shouldContinue = this.emitNextCompletedResult(downstream);
        }
        this.executorService.shutdownNow(); // it will cancel if there are any pending tasks
        if (shouldContinue && !errors.isEmpty()){
            this.throwExecutionException();
        }
    }

    private boolean emitNextCompletedResult(Gatherer.Downstream<? super R> downstream) {
        try {
            var future = this.completionService.take();
            return switch (future.state()){
                case SUCCESS -> downstream.push(future.resultNow());
                case FAILED -> {
                    errors.add(future.exceptionNow());
                    yield true;
                }
                default -> true;
            };
        } catch (Exception e) {
            this.executorService.shutdownNow();
            throw new RuntimeException(e);
        }
    }

    private void throwExecutionException() {
        var exception = new RuntimeException("Delayed Execution Exception");
        errors.forEach(exception::addSuppressed);
        throw exception;
    }
}
