package com.java.functional.programming.streamgatherers.concurrency.util.structuredtaskscope;

import java.util.Objects;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.Function;

public class SubTaskResultImpl<T> implements SubTaskResult<T> {

    private final SubTaskExecutorImpl owner;
    private final StructuredTaskScope.Subtask<T> subtask;

    public SubTaskResultImpl(SubTaskExecutorImpl owner, StructuredTaskScope.Subtask<T> subtask) {
        this.owner = owner;
        this.subtask = subtask;
    }

    private Function<Throwable, T> errorHandler = ex -> {
        if (ex instanceof RuntimeException re) throw re;
        throw new RuntimeException(ex);
    };

    @Override
    public T get() {
        try {
            owner.joinIfNeeded();
            return subtask.get();
        } catch (Throwable t) {
            return errorHandler.apply(t);
        }
    }

    @Override
    public SubTaskResult<T> onError(Function<Throwable, T> handler) {
        this.errorHandler = Objects.requireNonNull(handler, "errorHandler");
        return this;
    }
}
