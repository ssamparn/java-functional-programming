package com.java.functional.programming.streamgatherers.concurrency.util;

import java.util.function.Function;

public class SubTaskResultImpl<T> implements SubTaskResult<T> {

    @Override
    public T get() {
        return null;
    }

    @Override
    public SubTaskResult<T> onError(Function<Throwable, T> errorHandler) {
        return null;
    }
}
