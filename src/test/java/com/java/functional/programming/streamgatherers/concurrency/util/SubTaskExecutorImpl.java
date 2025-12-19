package com.java.functional.programming.streamgatherers.concurrency.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class SubTaskExecutorImpl implements SubTaskExecutor {

    private final ExecutorService executorService;

    public SubTaskExecutorImpl(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public <T> SubTaskResult<T> execute(Callable<T> callable) {
        var future = this.executorService.submit(callable);
        return new SubTaskResultImpl<>(future);
    }
}
