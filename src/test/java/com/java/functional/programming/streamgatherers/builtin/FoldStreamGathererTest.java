package com.java.functional.programming.streamgatherers.builtin;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Gatherers;

/**
 * fold(): Fold in Java Stream Gatherers is a built-in gatherer that performs an ordered reduction over the elements of a stream, similar to a left fold in functional programming.
 * It accumulates elements into a single result using an initial value and a folding function, and then emits that result downstream as one element.
 *
 * When to use fold in a Stream Gatherer?
 * Gatherers.fold(initial, folder) is an ordered, reduction‑like intermediate operation that emits exactly one result downstream. It’s ideal when:
 *    1. Order matters (left fold):
 *       You need to process elements strictly in encounter order and each step depends on the previous accumulator (e.g., building a path string, running balance, state machines).
 *    2. A parallel combiner doesn’t make sense:
 *       Some reductions can’t be correctly combined from partial results (e.g. concatenation with a delimiter that depends on previous output, sequential validations, time‑dependent transformations).
 *       fold is explicitly designed for these order‑dependent or non‑combinable reductions.
 *    3. You want one value but keep the pipeline fluent:
 *       Unlike terminal collect(...) or reduce(...), fold keeps you in the stream so you can still map/flatMap after the fold output.
 *    4. You need a custom accumulator object.
 *       Accumulate into a DTO (e.g., Stats, Summary, Ledger) and then project it downstream.
 *    5. Stateful computation with short, clear code
 *       Prefix computations (e.g., cumulative transformations), sequential validations, and deriving a final status based on the full traversal.
 *    6. Graceful handling of empty streams
 *       It always emits one element: the initial value if upstream is empty, which often simplifies downstream logic.
 * */
@Slf4j
public class FoldStreamGathererTest {

    record BankAccount(int balance) {

    }

    @Test
    public void testFoldStreamGatherer() {
        // positive: credit or deposit. negative: debit or withdraw
        List<Integer> transactions = List.of(2500, -1500, -200, 100, -200, 900, 300);

        BankAccount bankAccount = transactions.stream()
                .gather(Gatherers.fold(
                        () -> new BankAccount(100), // Assume an initial balance of 100.
                        (account, transaction) -> new BankAccount(account.balance() + transaction)
                ))
                .findFirst()
                .orElse(new BankAccount(0));

        log.info("Bank account balance: {}", bankAccount.balance);
    }
}