package com.java.functional.programming.functionalinterfaces;

import com.java.functional.programming.functionalinterfaces.data.Student;
import com.java.functional.programming.functionalinterfaces.data.StudentDatabase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Slf4j
public class FunctionFunctionalInterfaceTest {

    // In Java, Function is a functional interface which takes an argument (Type T) and returns an object (Type R).
    // The input and output can be of same or different type.
    // Function functional interface is the most simple and general case of a lambda.
    // It is a functional interface with a method that receives one value and returns another.

    @Test
    public void function_used_in_standard_library() {
        // One of the common usages of the Function type in the standard library is Map.computeIfAbsent() method.
        // This method returns a value from a map by key, but calculates a value if a key is not already present in a map.
        // To calculate a value, it uses the passed Function implementation:
        Map<String, Integer> nameMap = new HashMap<>();

        Integer length = nameMap.computeIfAbsent("John", String::length);
        // In this case, we will calculate a value by applying a function to a key, put inside a map, and also returned from the method call.
        System.out.println(length);
    }

    @Test
    public void function_used_string_length_test() {
        Function<String, Integer> stringLength = String::length;
        Integer length = stringLength.apply("Sashank");
        System.out.println(length);
    }

    @Test
    public void function_and_then_compose_difference_test() {
        Function<String, String> uppercaseFunction = s -> s.toUpperCase() + " ";
        Function<String, String> concatFunction = s -> s.toUpperCase().concat(" default string");

        System.out.println(uppercaseFunction.andThen(concatFunction).apply("java8"));
        System.out.println(uppercaseFunction.compose(concatFunction).apply("java8"));
    }

    @Test
    public void list_to_map_using_function() {
        List<Student> allStudents = StudentDatabase.getAllStudents();
        Map<String, Double> studentMap = new HashMap<>();

        Function<List<Student>, Map<String, Double>> studentFunction = students -> {
            students.forEach(student -> studentMap.put(student.getName(), student.getGpa()));
            return studentMap;
        };

        System.out.println(studentFunction.apply(allStudents));
    }
}
