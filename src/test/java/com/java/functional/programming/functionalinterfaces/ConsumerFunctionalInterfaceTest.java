package com.java.functional.programming.functionalinterfaces;

import com.java.functional.programming.functionalinterfaces.data.Student;
import com.java.functional.programming.functionalinterfaces.data.StudentDatabase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class ConsumerFunctionalInterfaceTest {

    @Test
    public void consumer_functional_interface_test() {
        Consumer<String> stringConsumer = string -> log.info("The string passed in consumer is: {}", string.length());
        stringConsumer.accept("Sashank");
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
        studentList.forEach(student -> log.info("Student name is: {} and his / her activities are: {}", student.getName(), student.getActivities()));
    }

    @Test
    public void get_studentnames_and_activies_test2() {
        List<Student> studentList = StudentDatabase.getAllStudents();
        Consumer<Student> studentNameConsumer = student -> log.info(student.getName());
        Consumer<Student> studentActivitiesConsumer = student -> log.info(student.getActivities().toString());
        studentList.forEach(studentNameConsumer.andThen(studentActivitiesConsumer)); // consumer chaining
    }

    @Test
    public void get_studentnames_and_activies_using_condition_test() {
        List<Student> studentList = StudentDatabase.getAllStudents();

        studentList.forEach(student -> {
            if (student.getGradeLevel() >= 3 && student.getGpa() >= 3.9) {
                log.info("Students with Grade Level >= 3 and Gpa Level >= 3.9 are: {}", student.getName());
            }
        });
    }
}
