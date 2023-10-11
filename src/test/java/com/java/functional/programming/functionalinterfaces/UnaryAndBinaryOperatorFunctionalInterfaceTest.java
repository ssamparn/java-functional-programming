package com.java.functional.programming.functionalinterfaces;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class UnaryAndBinaryOperatorFunctionalInterfaceTest {

    // UnaryOperator is an extension of the Function interface.
    // UnaryOperator accepts one object and returns a value of the same type.
    @Test
    public void unary_operator_simple_test() {
        UnaryOperator<String> stringUnaryOperator = string -> string.concat(" ").concat("Java 8");

        assertEquals("Learning Java 8", stringUnaryOperator.apply("Learning"));
    }

    // The identity() method is a static method used to return a unaryOperator that always returns its input argument.
    @Test
    public void unary_operator_identity_method_simple_test() {
        UnaryOperator<Boolean> booleanUnaryOperator = UnaryOperator.identity();
        assertTrue(booleanUnaryOperator.apply(true));

        UnaryOperator<String> stringUnaryOperator = UnaryOperator.identity();
        assertEquals("Java", stringUnaryOperator.apply("Java"));
    }

    // BinaryOperator is an extension of BiFunction interface.
    // BinaryOperator accepts two objects of the same type and returns the result of the same type.
    @Test
    public void binary_operator_simple_test() {
        BinaryOperator<String> stringBinaryOperator = (string1, string2) -> string1.concat(" ").concat(string2);

        assertEquals("Learning Java 8", stringBinaryOperator.apply("Learning", "Java 8"));
    }

    @Test
    public void binary_operator_max_by_test() {
        BinaryOperator<Integer> integerBinaryOperator = BinaryOperator.maxBy(Integer::compareTo);
        assertEquals(98, integerBinaryOperator.apply(98, 65));
    }

    @Test
    public void binary_operator_min_by_test() {
        BinaryOperator<Integer> integerBinaryOperator = BinaryOperator.minBy(Integer::compareTo);
        assertEquals(65, integerBinaryOperator.apply(98, 65));
    }
}
