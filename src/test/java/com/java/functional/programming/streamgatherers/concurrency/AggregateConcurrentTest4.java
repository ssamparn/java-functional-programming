package com.java.functional.programming.streamgatherers.concurrency;

import com.java.functional.programming.streamgatherers.concurrency.externalservice.RestClient;
import com.java.functional.programming.streamgatherers.concurrency.util.GatherersUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

/**
 * Nested Concurrency - For each productId, call product-service and rating-service concurrently.
 * Why we need aggregated concurrency?
 * Our execute concurrent method, executeConcurrent(...) gives us outer (per-element) concurrency. It runs one mapper per stream element which can run many elements in parallel.
 * Where as if we need a nested concurrency, that is for each element, we need to run two independent functions in parallel and then combines their results, executeConcurrent() will fall short.
 * So aggregateConcurrent(...) will give us nested (per-element, intra-element) concurrency.
 * For each element, it will run two independent functions in parallel and then combines their results with a BiFunction.
 * This matters when each element requires multiple independent sub-tasks you want to run concurrently within the element.
 *
 * V Imp:  Example use-case: For each UserId:
 *      productName: fetch productName from Product Service
 *      rating: fetch ratings from Rating Service
 * combine: AggregatedProduct = merge(productName, rating)
 *
 * This can cut per-element latency from Latency(Product Service) + Latency(Rating Service) down to roughly
 * Max(Latency(Product Service), Latency(Rating Service)), while still letting the outer pipeline process multiple users concurrently.
 *
 * */
@Slf4j
public class AggregateConcurrentTest4 {

    record ProductAggregate(String productName, int rating) {
    }

    /**
     * Ensure that the external service is up and running.
     * */
    @Test
    public void nestedConcurrencyTest() {
        IntStream.rangeClosed(1, 100)
                .boxed()
                .gather(GatherersUtil.aggregateConcurrent(
                        10,
                        RestClient::getProduct,
                        RestClient::getRating,
                        ProductAggregate::new
                ))
                .forEach(aggregatedProduct -> log.info("aggregatedProduct: {}", aggregatedProduct));
    }

    /**
     * Ensure that the external service is up and running.
     * */
    @Test
    public void nestedConcurrentWithStructuredTaskScopeTest() {
        IntStream.rangeClosed(1, 100)
                .boxed()
                .gather(GatherersUtil.aggregateConcurrentWithStructuredConcurrency(
                        10,
                        RestClient::getProduct,
                        RestClient::getRating,
                        ProductAggregate::new
                ))
                .forEach(aggregatedProduct -> log.info("aggregatedProduct: {}", aggregatedProduct));
    }
}