package com.java.functional.programming.functionalinterfaces;

import com.java.functional.programming.functionalinterfaces.data.Student;
import com.java.functional.programming.functionalinterfaces.data.StudentDatabase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@Slf4j
public class BiPredicateFunctionalInterfaceTest {

    // BiPredicate is a functional interface in Java that accepts two inputs and can return a boolean value.
    // It is similar to the Predicate interface. The only difference is that it takes two inputs instead of one.
    @Test
    public void bipredicate_simple_test() {
        BiPredicate<Integer, Integer> isDivisible = (a, b) -> a % b == 0;

        System.out.println("10 divisible by 2: " + isDivisible.test(10, 2));
        System.out.println("5 divisible by 3: " + isDivisible.test(5, 3));
        System.out.println("8 divisible by 4: " + isDivisible.test(8, 4));
    }

    @Test
    public void bipredicate_as_a_function_argument() {
        BiPredicate<String, Double> biPredicate = (name, gpa) -> name.startsWith("A") || gpa >= 3.9;

        List<Student> students = StudentDatabase.getAllStudents()
                .stream()
                .filter(student -> biPredicate.test(student.getName(), student.getGpa()))
                .toList();

        Assertions.assertEquals(4, students.size());
        Assertions.assertEquals("Adam", students.get(0).getName());
        Assertions.assertEquals("Emily", students.get(1).getName());
        Assertions.assertEquals("Dave", students.get(2).getName());
        Assertions.assertEquals("James", students.get(3).getName());
    }
}
