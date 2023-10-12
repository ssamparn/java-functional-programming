package com.java.functional.programming.methodreference;

import com.java.functional.programming.functionalinterfaces.data.Bicycle;
import com.java.functional.programming.functionalinterfaces.data.BicycleComparator;
import com.java.functional.programming.functionalinterfaces.data.BicycleDatabase;
import com.java.functional.programming.functionalinterfaces.data.Student;
import com.java.functional.programming.functionalinterfaces.data.StudentDatabase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Method references are a special type of lambda expressions.
// They’re often used to create simple lambda expressions by referencing existing methods.

// There are four kinds of method references:
//   1. Static methods. (Classname::staticMethod)
//   2. Instance methods of particular objects. (Classname::instanceMethod)
//   3. Instance methods of an arbitrary object of a particular type (Instance::methodName)
//   4. Constructor

@Slf4j
public class MethodReferenceTest {

    @Test
    public void method_reference_using_function_test() {
        Function<String, String> stringFunction = String::toUpperCase;

        assertEquals("JAVA", stringFunction.apply("java"));
    }

    @Test
    public void method_reference_using_consumer_test() {
        Consumer<Student> studentConsumer = System.out::println;
        StudentDatabase.getAllStudents().forEach(studentConsumer);
    }

    @Test
    public void method_reference_using_static_method_test() {
        List<String> studentNames = StudentDatabase.getAllStudents().stream()
                .map(Student::getName)
                .map(StringUtils::capitalize) // static method reference
                .toList();

        System.out.println(studentNames);
    }

    @Test
    public void method_reference_using_instance_method_of_a_particular_object_type_test() {
        BicycleComparator bikeFrameSizeComparator = new BicycleComparator();

        List<Bicycle> sortedList = BicycleDatabase.getAllBicycles().stream()
                .sorted(bikeFrameSizeComparator::compare)
                .toList();

        System.out.println(sortedList);
    }

    @Test
    public void method_reference_using_instance_method_of_an_arbitrary_object_of_a_particular_type_test() {
        List<Integer> numbers = Arrays.asList(5, 3, 50, 24, 40, 2, 9, 18);

        List<Integer> sortedList = numbers.stream()
                .sorted(Integer::compareTo)
                .toList();

        System.out.println(sortedList);
    }

    @Test
    public void method_reference_using_constructor_test() {
        List<String> bikeBrands = Arrays.asList("Giant", "Scott", "Trek", "GT");

        List<Bicycle> bicycles = Arrays.stream(bikeBrands.stream()
                        .map(Bicycle::new)
                        .toArray(Bicycle[]::new))
                .toList();

        System.out.println(bicycles);
    }

}
