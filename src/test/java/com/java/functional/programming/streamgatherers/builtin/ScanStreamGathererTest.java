package com.java.functional.programming.streamgatherers.builtin;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Gatherers;

/**
 * scan(): scan in Java Stream Gatherers is a built‑in gatherer that performs a prefix scan
 * (a.k.a. cumulative accumulation): starting from an initial state, it updates the accumulator for each upstream element and emits the intermediate results downstream as the stream advances.
 * It’s ideal when you want a running result at every step, not just a single summary at the end.
 * */
@Slf4j
public class ScanStreamGathererTest {

    record BankAccount(int balance) {

    }

    @Test
    public void testFoldStreamGatherer() {
        // positive: credit or deposit. negative: debit or withdraw
        List<Integer> transactions = List.of(1000, -300, 200, -100);

        List<Integer> balances = transactions.stream()
                .gather(Gatherers.scan(
                        () -> new BankAccount(100), // Assume an initial balance of 100.
                        (account, transaction) -> new BankAccount(account.balance() + transaction)
                ))
                .map(BankAccount::balance)
                .toList();
        log.info("Bank account balance after every transactions: {}", balances);
    }
}