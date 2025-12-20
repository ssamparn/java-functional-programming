package com.java.functional.programming.streamgatherers.concurrency.util.structuredtaskscope;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.atomic.AtomicBoolean;

public class SubTaskExecutorImpl implements SubTaskExecutor, AutoCloseable {

    private final StructuredTaskScope scope;
    private final AtomicBoolean joined = new AtomicBoolean(false);

    public SubTaskExecutorImpl(StructuredTaskScope scope) {
        this.scope = scope;
    }

    /**
     * Open a scope that waits for all subtasks, cancels on first failure, throws on failure.
     * */
    public static SubTaskExecutor openAwaitAllSuccessfulOrThrow() {
        var scope = StructuredTaskScope.open(StructuredTaskScope.Joiner.awaitAllSuccessfulOrThrow());
        return new SubTaskExecutorImpl(scope);
    }

    /**
     * Same policy but with a timeout budget for all subtasks in this scope.
     * */
    public static SubTaskExecutor openAwaitAllSuccessfulOrThrow(Duration timeout) {
        var scope = StructuredTaskScope.open(StructuredTaskScope.Joiner.awaitAllSuccessfulOrThrow(), cfg -> cfg.withTimeout(timeout));
        return new SubTaskExecutorImpl(scope);
    }

    @Override
    public <T> SubTaskResult<T> execute(Callable<T> callable) {
        StructuredTaskScope.Subtask<T> subtask = scope.fork(callable); // forks a virtual thread by default
        return new SubTaskResultImpl<>(this, subtask);
    }

    void joinIfNeeded() {
        if (joined.compareAndSet(false, true)) {
            try {
                scope.join();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while joining subtasks", ie);
            } catch (StructuredTaskScope.FailedException fe) {
                var cause = fe.getCause();
                throw (cause instanceof RuntimeException re) ? re : new RuntimeException("Subtask failed", cause);
            } catch (StructuredTaskScope.TimeoutException te) {
                throw new RuntimeException("Timeout while joining subtasks", te);
            }
        }
    }

    @Override
    public void close() {
        scope.close();
    }
}
