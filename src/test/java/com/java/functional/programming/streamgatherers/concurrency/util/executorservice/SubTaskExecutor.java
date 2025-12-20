package com.java.functional.programming.streamgatherers.concurrency.util.executorservice;

import java.util.concurrent.Callable;

public interface SubTaskExecutor {
    <T> SubTaskResult<T> execute(Callable<T> callable);
}
