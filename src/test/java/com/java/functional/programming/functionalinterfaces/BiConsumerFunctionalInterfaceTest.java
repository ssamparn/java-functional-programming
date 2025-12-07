package com.java.functional.programming.functionalinterfaces;

import com.java.functional.programming.functionalinterfaces.data.Student;
import com.java.functional.programming.functionalinterfaces.data.StudentDatabase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * In Java, BiConsumer<T, U> is a functional interface that represents an operation that takes two input arguments and returns no result.
 * It’s part of the java.util.function package and is often used in scenarios where you want to perform an action on two values without producing a return value.
 *
 * Signature:
 *   @FunctionalInterface
 *   public interface BiConsumer<T, U> {
 *      void accept(T t, U u);
 *      default BiConsumer<T, U> andThen(BiConsumer<T, U> after) { ... }
 *   }
 *   It has a single abstract method.
 *    - void accept(T t, U u);
 *  It does not return anything (void).
 *  Commonly used in streams, maps, or any place where you need to process two related values.
 * */
@Slf4j
public class BiConsumerFunctionalInterfaceTest {

    @Test
    public void biConsumerFunctionalInterfaceSimpleTest() {
        BiConsumer<String, String> stringBiConsumer = (strA, strB)  -> log.info("The string(s) passed to biconsumer are: {}, {}", strA, strB);
        stringBiConsumer.accept("Sashank", "Aparna");
    }

    /**
     * Logging Key-Value Pairs in a Map.
     * You have a Map<String, Integer> representing product names and their stock quantities, and you want to print them in a formatted way,
     * you can use BiConsumer
     * */
    @Test
    public void biConsumerFunctionalInterfaceRealisticTest() {
        Map<String, Integer> stocks = new HashMap<>();
        stocks.put("Laptop", 10);
        stocks.put("Phone", 25);
        stocks.put("Tablet", 15);

        stocks.forEach((product, quantity) -> {
            log.info("The product {} has {} items in stock", product, quantity);
        });
    }

    /**
     * Another realistic example of using BiConsumer to update values in a map by applying a discount to product prices
     * */
    @Test
    public void biConsumerFunctionalInterfaceApplyingDiscountTest() {
        Map<String, Double> products = new HashMap<>();
        products.put("Laptop", 1200.0);
        products.put("Phone", 800.0);
        products.put("Tablet", 400.0);

        BiConsumer<String, Double> applyingDiscount = (product, actualPrice) -> {
            double discountedPrice = actualPrice * 0.5; // 50% discount
            products.put(product, discountedPrice);
        };

        // Apply discount to each entry
        products.forEach(applyingDiscount);

        BiConsumer<String, Double> loggingBiConsumer = (product, finalPrice) -> log.info("The product {}'s new price is {} after discount", product, finalPrice);
        products.forEach(loggingBiConsumer);
    }

    @Test
    public void bi_consumer_multiplication_and_division_of_integers() {
        BiConsumer<Integer, Integer> multiply = (a, b) -> log.info("Multiplication is: {}", a * b);
        BiConsumer<Integer, Integer> division = (a, b) -> log.info("Multiplication is: {}", a / b);

        multiply.andThen(division).accept(10, 5);
    }

    @Test
    public void get_all_students_test() {
        List<Student> studentList = StudentDatabase.getAllStudents();
        Consumer<Student> studentConsumer = student -> log.info(student.toString());
        studentList.forEach(studentConsumer); // for-each accepts a consumer
    }

    @Test
    public void get_studentnames_and_activies_test1() {
        List<Student> studentList = StudentDatabase.getAllStudents();

        BiConsumer<String, List<String>> studentBiConsumer = (studentName, studentActivities) -> log.info("Student name is: {} and his / her activities are: {}", studentName, studentActivities);

        studentList.forEach(student -> studentBiConsumer.accept(student.getName(), student.getActivities()));
    }

    // Another set of specialized BiConsumer versions comprises
    // ObjDoubleConsumer,
    // ObjIntConsumer,
    // ObjLongConsumer
    // which receive two arguments; one of the arguments is generified, and the other is a primitive type.
}
