package com.java.functional.programming.streamgatherers.concurrency.util.structuredtaskscope;

import java.util.function.Function;

public interface SubTaskResult<T> {
    T get();

    SubTaskResult<T> onError(Function<Throwable, T> errorHandler);
}
