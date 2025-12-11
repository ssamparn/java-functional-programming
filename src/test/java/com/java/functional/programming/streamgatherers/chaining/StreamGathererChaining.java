package com.java.functional.programming.streamgatherers.chaining;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Gatherer;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

/**
 * Stream Gatherer Chaining:
 * Gatherer chaining means composing multiple Gatherers so that the output of one becomes the input of the next, typically via the andThen(...) method.
 * It lets you build reusable, custom intermediate operations for Java Streams and fuse them into a single, readable, and often more efficient pipeline stage.
 *
 * Chaining uses composition:
 * You take g1 and attach g2 with g1.andThen(g2).
 * The composed gatherer behaves like one intermediate operation—first applying g1, then feeding its outputs into g2.
 * This is explicitly supported by the API (“Composing Gatherers”) and by the default andThen() implementation.
 * */
@Slf4j
public class StreamGathererChaining {

    /**
     * A real‑life example that chains multiple Java Stream Gatherers to process an e‑commerce order stream.
     *    1. Remove duplication by orderId: custom distinctByKeyGatherer which maintains state (a set of keys) and emits one output per unique key (orderId).
     *    2. Batch orders into fixed windows of 50 (built-in many-to-many gatherer) for a bulk shipping API (built‑in windowFixed()), which emits List<Order>
     *    3. Call the bulk API concurrently with bounded concurrency (built‑in one-to-one gatherer mapConcurrent()). Up to 4 concurrent mapper calls while preserving order.
     *    4. Running totals after each batch (built-in scan). Emits a Stats snapshot per batch processed
     *    5. Final summary (built-in fold). many-to-one; reduces all Stats snapshots to one.
     * */

    @Test
    public void streamGathererChainingMultipleGatherersTest() {
        List<Order> orders = generateOrders(100);
        ShippingClient shippingClient = new ShippingClient();

        OrderStatistics finalStatus = orders.stream()
                .gather(distinctByKeyGatherer(Order::orderId))
                .gather(Gatherers.windowFixed(50))
                .gather(Gatherers.mapConcurrent(4, shippingClient::bulkCreateShipments))
                .gather(Gatherers.scan(OrderStatistics::new, OrderStatistics::accumulate))
                .gather(Gatherers.fold(OrderStatistics::new, OrderStatistics::merge))
                .findFirst() // Extract the single result from the stream
                .orElseThrow();

        log.info("Final order status: {}", finalStatus);
    }

    @Test
    public void streamGathererChainingMultipleGatherersWithAndThenTest() {
        List<Order> orders = generateOrders(100);
        ShippingClient shippingClient = new ShippingClient();

        OrderStatistics finalStatus = orders.stream()
                .gather(distinctByKeyGatherer(Order::orderId))
                .gather(orderStatus(shippingClient))
                .findFirst()
                .orElseThrow();

        log.info("Final order status: {}", finalStatus);
    }

    private static Gatherer<Order, ?, OrderStatistics> orderStatus(ShippingClient shippingClient) {
        Gatherer<Order, ?, List<Order>> batcher = Gatherers.windowFixed(50);

        Gatherer<List<Order>, ?, ShipmentBatchResult> bulkApi = Gatherers.mapConcurrent(4, shippingClient::bulkCreateShipments);

        Gatherer<ShipmentBatchResult, ?, OrderStatistics> running = Gatherers.scan(OrderStatistics::new, OrderStatistics::accumulate);

        Gatherer<OrderStatistics, ?, OrderStatistics> finalize = Gatherers.fold(OrderStatistics::new, OrderStatistics::merge);

        return batcher.andThen(bulkApi)
                .andThen(running)
                .andThen(finalize);
    }

    /**
     * Custom gatherer: distinctByKey.
     * A sequential gatherer with internal state (Set<K>) that emits each element only once per stream based on a key (here orderId).
     * Using ofSequential(...) is appropriate when we don’t need parallel state combination
     * */
    private static <T, K> Gatherer<T, Set<K>, T> distinctByKeyGatherer(Function<T, K> keyExtractor) {
        return Gatherer.ofSequential(
                HashSet::new,
                Gatherer.Integrator.ofGreedy((seen, element, downstream) -> {
                    K key = keyExtractor.apply(element);
                    if (seen.add(key) && !downstream.isRejecting()) {
                        downstream.push(element);
                    }
                    return true;
                }),
                ((seen, downstream) -> {

                })
        );
    }

    private record Order(
            String orderId,
            String customerId,
            BigDecimal amount,
            String destinationCountry) {
    }

    private record ShipmentBatchResult(
            int successCount,
            int failureCount,
            BigDecimal batchRevenue) {
    }


    /**
     * Aggregated stats across all processed batches
     */
    private static final class OrderStatistics {
        private int shipped;
        private int failed;
        private BigDecimal revenue = BigDecimal.ZERO;

        public OrderStatistics accumulate(ShipmentBatchResult batch) {
            this.shipped += batch.successCount();
            this.failed  += batch.failureCount();
            this.revenue = this.revenue.add(batch.batchRevenue());
            return this;
        }

        // Merge two Stats (used by fold)
        public OrderStatistics merge(OrderStatistics other) {
            this.shipped += other.shipped;
            this.failed  += other.failed;
            this.revenue = this.revenue.add(other.revenue);
            return this;
        }

        @Override
        public String toString() {
            return "Stats{shipped=" + shipped +
                    ", failed=" + failed +
                    ", revenue=" + revenue + "}";
        }
    }


    private static final class ShippingClient {
        public ShipmentBatchResult bulkCreateShipments(List<Order> orders) {
            // Simulate the call: succeed all non-negative amounts; failures otherwise
            int success = 0, failure = 0;
            BigDecimal total = BigDecimal.ZERO;

            for (var order : orders) {
                if (order.amount().compareTo(BigDecimal.ZERO) >= 0) {
                    success++;
                    total = total.add(order.amount());
                } else {
                    failure++;
                }
            }

            // simulate latency per batch
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {

            }
            return new ShipmentBatchResult(success, failure, total);
        }
    }

    private static List<Order> generateOrders(int n) {
        var rnd = new Random(0);
        return Stream.iterate(0, i -> i + 1).limit(n)
                .map(i -> {
                    // some duplicate ids to demonstrate distinctByKey
                    String id = "Order-" + (i % 900);
                    String customer = "Customer-" + rnd.nextInt(100);
                    BigDecimal amount = BigDecimal.valueOf(rnd.nextInt(200) - 5); // some negatives -> failures
                    String dest = switch (rnd.nextInt(5)) {
                        case 0 -> "NL"; case 1 -> "DE"; case 2 -> "BE"; case 3 -> "FR"; default -> "UK";
                    };
                    return new Order(id, customer, amount, dest);
                })
                .toList();
    }
}
