package com.java.functional.programming.streamgatherers.stateful;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Gatherer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Moving Average:
 * A moving average is a classic example of a stateful stream operation.
 * Gatherer API is perfect for this because it allows maintaining state across elements.
 *
 * Requirements:
 *    1. At every step, look at the last N numbers which is nothing but window size.
 *    2. Compute the moving average over the last n elements.
 *    3. For each new element:
 *       - Add it to a sliding window.
 *       - Remove the oldest if the window exceeds n.
 *       - Push the current average downstream.
 *
 * Use Case: To tract the trend
 * */
@Slf4j
public class MovingAverageStatefulGathererTest {

    @Test
    public void movingAverageTest() {
        IntStream.rangeClosed(1, 10)
                .boxed()
                .gather(movingAverage(3)) // Window size = 3
                .forEach(avg -> log.info("Moving average: {}", avg));
    }

    /**
     * Custom Gatherer that computes moving average over last N elements.
     * Logs the current window and its average at each step.
     */
    private static <T extends Number> Gatherer<T, ?, Double> movingAverage(int windowSize) {
        return Gatherer.ofSequential(
                () -> new MovingAverageCalculator<>(windowSize), // Initializer
                Gatherer.Integrator.ofGreedy(
                        (calculator, element, downstream) -> {
                            calculator.add(element);
                            double avg = calculator.getAverage();
                            log.info("Current window: {} | Average: {}", calculator.getMovingWindow(), avg);
                            downstream.push(avg);
                            return true;
                        }),
                        (calculator, downstream) -> log.info("Final window: {}", calculator.getMovingWindow()) // Optional finisher
                );
    }

    /**
     * Static class to hold state for moving average calculation.
     */
    private static class MovingAverageCalculator<T extends Number> {
        private final int windowSize;
        private final Deque<T> window;
        private double sum;

        public MovingAverageCalculator(int windowSize) {
            this.windowSize = windowSize;
            this.window = new ArrayDeque<>(windowSize);
            this.sum = 0.0;
        }

        public void add(T value) {
            this.window.addLast(value);
            sum += value.doubleValue();
            if (window.size() > windowSize) {
                T removed = this.window.removeFirst();
                sum -= removed.doubleValue();
            }
        }

        public double getAverage() {
            return this.window.isEmpty() ? 0.0 : this.sum / this.window.size();
        }

        public Deque<T> getMovingWindow() {
            return this.window;
        }
    }
}
