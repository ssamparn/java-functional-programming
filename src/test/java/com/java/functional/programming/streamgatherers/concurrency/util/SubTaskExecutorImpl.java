package com.java.functional.programming.streamgatherers.concurrency.util;

import java.util.concurrent.Callable;

public class SubTaskExecutorImpl implements SubTaskExecutor {

    @Override
    public <T> SubTaskResult<T> execute(Callable<T> callable) {
        return null;
    }
}
