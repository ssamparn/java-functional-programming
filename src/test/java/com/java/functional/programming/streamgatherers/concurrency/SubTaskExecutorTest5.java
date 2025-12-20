package com.java.functional.programming.streamgatherers.concurrency;

import com.java.functional.programming.streamgatherers.concurrency.externalservice.RestClient;
import com.java.functional.programming.streamgatherers.concurrency.util.GatherersUtilWithExecutorService;
import com.java.functional.programming.streamgatherers.concurrency.util.GatherersUtilWithStructuredTaskScope;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.stream.IntStream;

/**
 * In aggregated concurrency example, the solution even though is a generic solution, it uses a BiFunction<R1, R2, R> hard-codes the pattern to exactly two per-element tasks.
 * If you need 3 or 3+ independent computations per element, you either duplicate code (tri/quad variants) or shoehorn extra work into the two functions, which defeats the clarity and composability of the API.
 * So we need a really generic solution which can handle multiple concurrent subtasks in an aggregated concurrent context.
 * */
@Slf4j
public class SubTaskExecutorTest5 {

    record ProductAggregate(String productName, int rating) {
    }

    /**
     * Ensure that the external service is up and running
     * */
    @Test
    public void concurrentSubTaskExecutorTest() {
        IntStream.rangeClosed(1, 100)
                .boxed()
                .gather(GatherersUtilWithExecutorService.aggregateConcurrent(10, (productId, subTaskExecutor) -> {
                    var product = subTaskExecutor.execute(() -> RestClient.getProduct(productId));
                    var rating = subTaskExecutor.execute(() -> RestClient.getRating(productId));
                    return new ProductAggregate(product.get(), rating.get());
                }))
                .forEach(aggregatedProduct -> log.info("aggregatedProduct: {}", aggregatedProduct));
    }

    /**
     * Ensure that the external service is up and running
     * */
    @Test
    public void concurrentSubTaskExecutorWithStructuredTaskScopeTest() {
        IntStream.rangeClosed(1, 100)
                .boxed()
                .gather(GatherersUtilWithStructuredTaskScope.aggregateConcurrent(10, (productId, subTaskExecutor) -> {
                    var product = subTaskExecutor.execute(() -> RestClient.getProduct(productId));
                    var rating = subTaskExecutor.execute(() -> RestClient.getRating(productId));
                    return new ProductAggregate(product.get(), rating.get());
                }))
                .forEach(aggregatedProduct -> log.info("aggregatedProduct: {}", aggregatedProduct));
    }

    /**
     * Ensure that the external service is up and running
     * */
    @Test
    public void concurrentSubTaskExecutorWithStructuredTaskScopeWithTimeoutTest() {
        IntStream.rangeClosed(1, 10)
                .boxed()
                .gather(GatherersUtilWithStructuredTaskScope.aggregateConcurrentWithTimeout(10, (productId, subTaskExecutor) -> {
                    var product = subTaskExecutor.execute(() -> RestClient.getProduct(productId));
                    var rating = subTaskExecutor.execute(() -> RestClient.getRating(productId));
                    return new ProductAggregate(product.get(), rating.get());
                }, Duration.ofMillis(10000)))
                .forEach(aggregatedProduct -> log.info("aggregatedProduct: {}", aggregatedProduct));
    }
}
