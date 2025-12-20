package com.java.functional.programming.streamgatherers.concurrency.util.structuredtaskscope;

import java.util.concurrent.Callable;

/**
 * Alternative implementation of SubTaskExecutor in com.java.functional.programming.streamgatherers.concurrency.util.executorservice
 *
 * public interface SubTaskExecutor {
 *     <T> SubTaskResult<T> execute(Callable<T> callable);
 * }
 * */
public interface SubTaskExecutor extends AutoCloseable {
    <T> SubTaskResult<T> execute(Callable<T> callable);
}
