package com.java.functional.programming.streamgatherers.stateless;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Gatherer;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Goal is to understand how greedy integrator works.
 */
@Slf4j
public class GreedyIntegratorTest {

    @Test
    public void streamFilterWithGenericGathererTest() {
        List<Integer> evens = IntStream.rangeClosed(1, 10)
                .boxed()
                .gather(genericFilter(i -> i % 2 == 0))
                .toList();

        assertThat(evens).containsExactly(2, 4, 6, 8, 10);
    }

    private static <T> Gatherer<T, Void, T> genericFilter(Predicate<T> predicate) {
        return Gatherer.of((state, element, downstream) -> {
            if (predicate.test(element)) {
                log.info("element: {}", element);
                downstream.push(element);
            }
            return true; // return false if we want to short-circuit
        });
    }

    /**
     * What is a Greedy Integrator?
     * A Greedy Integrator guarantees that it will consume all elements and never short-circuit early.
     * This allows the Stream engine to optimize execution.
     * If you check the gatherer api, it accepts an integrator. Integrator is an interface. It has one integrate() method.
     * The Integrator interface has one more interface called Greedy, which simply extends the Integrator.
     * It does not have any other special methods to implement. So as part of Greedy interface we have to implement this integrate method.
     * <p>
     * Because it simply extends the integrator as you see. So what is why is that? Why do we have a separate interface?
     * What is the need for this Java team has created this special Greedy interface?
     * <p>
     * Just to give them some hint. That is, when you implement your integrator, if it's going to short circuit or if you have some logic to short circuit, then create regular integrator.
     * If you think that your integrator will not short circuit, that is, you do not have any special logic to end the stream early, then use Greedy integrator.
     * Why? It's mainly because they use this information for some internal optimization. Nothing else.
     * To better understand,
     * 1. Greedy extends the Integrator interface. So in a way Greedy is an Integrator.
     * 2. Greedy does not short circuit. That is, it does not have any special logic to return false on its own. Remember that.
     * 3. However, if the downstream operator returns  false (short circuits) that time Greedy will propagate the result to the upstream operator.
     * <p>
     * Let's take a look at this example.
     * Let's imagine we have one greedy or integrator implementation.
     * <p>
     * // Greedy
     * return true;
     * If you are going to return true like hard coded boolean true, then you can call this greedy integrator, because we are not short-circuiting.
     * We are always returning true. So it's a greedy integrator.
     * <p>
     * // Greedy
     * return downstream.push(element);
     * Instead of returning hard coded boolean True, if we are returning like this, return downstream.push(), whatever the result you are getting, that is what you are going to return.
     * Even in this case, you can call your integrator as a greedy integrator because you are not returning false on your own.
     * Instead, whatever the downstream provides, that is what you are returning. So this is also greedy.
     * <p>
     * // Not Greedy
     * return false;
     * But if you are going to return false, then you cannot call your integrator as greedy integrator because you have some special logic here.
     * To return false. So this is not greedy.
     * <p>
     * // Not Greedy
     * return random() < 10;
     * Similarly, if you are going to generate some random number, and if you are going to check if it's less than 10, you cannot call this as a greedy integrator, because at some point you will be returning false here.
     * So this is also not greedy.
     * <p>
     * Now the confusion is both the regular integrator and the greedy integrator has one abstract method integrate().
     * So when we use the lambda expressions, is this a regular integrator or greedy integrator.
     * This is why Java team has one factory method Gatherer.Integrator.ofGreedy()
     * <p>
     * 1. If you pass your lambda expression into Gatherer.Integrator.of() then you are creating regular integrator.
     * 2. If you pass your lambda expression into Gatherer.Integrator.ofGreedy() then you are creating a greedy integrator.
     * In some cases we return whatever the downstream provides. So it's a greedy integrator.
     * V Imp Note: The greedy integrator will never short circuit.
     *
     */

    @Test
    public void streamFilterWithGreedyIntegratorTest() {
        List<Integer> evens = IntStream.rangeClosed(1, 10)
                .boxed()
                .gather(genericFilterWithGreedyIntegrator(i -> i % 2 == 0))
                .toList();

        assertThat(evens).containsExactly(2, 4, 6, 8, 10);
    }

    private static <T> Gatherer<T, Void, T> genericFilterWithGreedyIntegrator(Predicate<T> predicate) {
        return Gatherer.of(
                Gatherer.Integrator.ofGreedy(
                        (state, element, downstream) -> {
                            if (predicate.test(element)) {
                                log.info("element: {}", element);
                                downstream.push(element);
                            }
                            return true; // returning false will not short-circuit, as this is a greedy integrator. we can not short-circuit with a greedy integrator
                        }
                )
        );
    }
}
