package com.java.functional.programming.functionalinterfaces;

import com.java.functional.programming.functionalinterfaces.data.Student;
import com.java.functional.programming.functionalinterfaces.data.StudentDatabase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


@Slf4j
public class BiConsumerFunctionalInterfaceTest {

    @Test
    public void bi_consumer_functional_interface_test() {
        BiConsumer<String, String> stringBiConsumer = (strA, strB)  -> log.info("The string(s) passed to biconsumer are: {}, {}", strA, strB);
        stringBiConsumer.accept("Sashank", "Aparna");
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
