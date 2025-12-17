package com.java.functional.programming.streamgatherers.concurrency.util;

import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Gatherer;

public class GatherersUtil {

    public static <T, R> Gatherer<T, ?, R> executeConcurrent(Function<T, R> mapperFunction) {
        return executeConcurrent(1000, mapperFunction);
    }

    public static <T, R> Gatherer<T, ?, R> executeConcurrent(int maxConcurrency, Function<T, R> mapperFunction) {
        return Gatherer.ofSequential(
                () -> new ExecuteConcurrent<>(maxConcurrency, mapperFunction, Executors.newVirtualThreadPerTaskExecutor()),
                Gatherer.Integrator.ofGreedy(ExecuteConcurrent::integrate),
                ExecuteConcurrent::finish
        );
    }

    public static <T, R> Gatherer<T, ?, R> executeConcurrentDelayError(Function<T, R> mapperFunction) {
        return executeConcurrentDelayError(1000, mapperFunction);
    }

    public static <T, R> Gatherer<T, ?, R> executeConcurrentDelayError(int maxConcurrency, Function<T, R> mapperFunction){
        return Gatherer.ofSequential(
                () -> new ExecuteConcurrentDelayError<>(maxConcurrency, mapperFunction, Executors.newVirtualThreadPerTaskExecutor()),
                Gatherer.Integrator.ofGreedy(ExecuteConcurrentDelayError::integrate),
                ExecuteConcurrentDelayError::finish
        );
    }
}
