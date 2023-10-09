package com.java.functional.programming.functionalinterfaces;

// Exists since Java 1.0
// A Functional Interface is an interface that has exactly one abstract method. (SAM: Single Abstract Method)
// and its implementation may be treated as lambda expressions.

//Java 8’s default methods are not abstract and do not count;
// A functional interface may still have multiple default methods
public class FunctionalInterfaceDocumentation {

    // New functional interfaces those got introduced in Java 8:
    // 1. Consumer
    // 2. Predicate
    // 3. Function
    // 4. Supplier

    // Extensions of these functional interfaces:
    // Consumer -- BiConsumer
    // Predicate -- BiPredicate
    // Function -- BiFunction, UnaryOperator, BinaryOperator
}
