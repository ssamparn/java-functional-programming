package com.java.functional.programming.streamgatherers.concurrency.util;

import com.java.functional.programming.streamgatherers.concurrency.util.structuredtaskscope.SubTaskExecutor;
import com.java.functional.programming.streamgatherers.concurrency.util.structuredtaskscope.SubTaskExecutorImpl;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Gatherer;

public class GatherersUtilWithStructuredTaskScope {

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

    public static <T, R> Gatherer<T, ?, R> aggregateConcurrent(int maxConcurrency, BiFunction<T, SubTaskExecutor, R> biFunction) {
        return Gatherer.ofSequential(
                () -> {
                    ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
                    Function<T, R> function = t -> {
                        try (SubTaskExecutor subTaskExecutor = SubTaskExecutorImpl.openAwaitAllSuccessfulOrThrow()) {
                            return biFunction.apply(t, subTaskExecutor);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    };
                    return new ExecuteConcurrent<>(maxConcurrency, function, executorService);
                },
                Gatherer.Integrator.ofGreedy(ExecuteConcurrent::integrate),
                ExecuteConcurrent::finish
        );
    }

    public static <T, R> Gatherer<T, ?, R> aggregateConcurrentWithTimeout(int maxConcurrency,
                                                                          BiFunction<T, SubTaskExecutor, R> biFunction,
                                                                          Duration timeout) {
        return Gatherer.ofSequential(
                () -> {
                    ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
                    Function<T, R> function = t -> {
                        try (SubTaskExecutor subTaskExecutor = SubTaskExecutorImpl.openAwaitAllSuccessfulOrThrow(timeout)) {
                            return biFunction.apply(t, subTaskExecutor);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    };
                    return new ExecuteConcurrent<>(maxConcurrency, function, executorService);
                },
                Gatherer.Integrator.ofGreedy(ExecuteConcurrent::integrate),
                ExecuteConcurrent::finish
        );
    }
}
