package com.java.functional.programming.streamgatherers.concurrency.util;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.BiFunction;
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

    public static <T, R> Gatherer<T, ?, R> executeConcurrentDelayError(int maxConcurrency, Function<T, R> mapperFunction) {
        return Gatherer.ofSequential(
                () -> new ExecuteConcurrentDelayError<>(maxConcurrency, mapperFunction, Executors.newVirtualThreadPerTaskExecutor()),
                Gatherer.Integrator.ofGreedy(ExecuteConcurrentDelayError::integrate),
                ExecuteConcurrentDelayError::finish
        );
    }

    /**
     * Nested Concurrency: Perform concurrent tasks inside a concurrent context.
     * T: Request
     * R1: Response from Product Service
     * R2: Response from Rating Service
     * R: Aggregated Response to form complete response ProductAggregate.
     * */
    public static <T, R1, R2, R> Gatherer<T, ?, R> aggregateConcurrent(int maxConcurrency,
                                                                       Function<T, R1> mapperFunction1,
                                                                       Function<T, R2> mapperFunction2,
                                                                       BiFunction<R1, R2, R> biFunction) {
        return Gatherer.ofSequential(
                () -> {
                    var executor = Executors.newVirtualThreadPerTaskExecutor();
                    Function<T, R> function = t -> {
                        var future1 = executor.submit(() -> mapperFunction1.apply(t));
                        var future2 = executor.submit(() -> mapperFunction2.apply(t));
                        return biFunction.apply(getResult(future1), getResult(future2));
                    };
                    return new ExecuteConcurrent<>(maxConcurrency, function, executor);
                },
                Gatherer.Integrator.ofGreedy(ExecuteConcurrent::integrate),
                ExecuteConcurrent::finish
        );
    }

    public static <T, R> Gatherer<T, ?, R> aggregateConcurrent(int maxConcurrency, BiFunction<T, SubTaskExecutor, R> biFunction) {
        return Gatherer.ofSequential(
                () -> {
                    var executor = Executors.newVirtualThreadPerTaskExecutor();
                    var subTaskExecutor = new SubTaskExecutorImpl(executor);
                    Function<T, R> function = t -> biFunction.apply(t, subTaskExecutor);
                    return new ExecuteConcurrent<>(maxConcurrency, function, executor);
                },
                Gatherer.Integrator.ofGreedy(ExecuteConcurrent::integrate),
                ExecuteConcurrent::finish
        );
    }

    public static <T, R> Gatherer<T, ?, R> aggregateConcurrent(BiFunction<T, SubTaskExecutor, R> biFunction) {
        return aggregateConcurrent(1000, biFunction);
    }

    private static <R> R getResult(Future<R> future){
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Nested Concurrency: Perform concurrent tasks inside a concurrent context.
     * T: Request
     * R1: Response from Product Service
     * R2: Response from Rating Service
     * R: Aggregated Response to form complete response ProductAggregate.
     * */
    public static <T, R1, R2, R> Gatherer<T, ?, R> aggregateConcurrentWithStructuredConcurrency(int maxConcurrency,
                                                                                                Function<T, R1> mapperFunction1,
                                                                                                Function<T, R2> mapperFunction2,
                                                                                                BiFunction<R1, R2, R> biFunction) {
        return Gatherer.ofSequential(
                () -> {
                    var executor = Executors.newVirtualThreadPerTaskExecutor();
                    Function<T, R> function = t -> {
                        try (var scope = StructuredTaskScope.open(StructuredTaskScope.Joiner.awaitAllSuccessfulOrThrow())) {
                            var subtask1 = scope.fork(() -> mapperFunction1.apply(t));
                            var subtask2 = scope.fork(() -> mapperFunction2.apply(t));
                            joinAndHandle(scope);
                            return biFunction.apply(subtask1.get(), subtask2.get());
                        }
                    };
                    return new ExecuteConcurrent<>(maxConcurrency, function, executor);
                },
                Gatherer.Integrator.ofGreedy(ExecuteConcurrent::integrate),
                ExecuteConcurrent::finish
        );
    }

    private static void joinAndHandle(StructuredTaskScope<Object, Void> scope) {
        try {
            scope.join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
