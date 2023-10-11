package com.java.functional.programming.functionalinterfaces;

import com.java.functional.programming.functionalinterfaces.data.Student;
import com.java.functional.programming.functionalinterfaces.data.StudentDatabase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@Slf4j
public class BiFunctionFunctionalInterfaceTest {

    @Test
    public void power_of_two_integers_using_bi_function_test() {
        BiFunction<Integer, Integer, Double> biFunction = Math::pow;

        System.out.println(biFunction.apply(2, 3));
    }

    @Test
    public void bi_function_simple_test() {
        BiFunction<List<Student>, Predicate<Student>, Map<String, Double>> biFunction = ((students, studentPredicate) -> {
            Map<String, Double> studentGardeMap = new HashMap<>();

            students.forEach(student -> {
                if (studentPredicate.test(student)) {
                    studentGardeMap.put(student.getName(), student.getGpa());
                }
            });
            return studentGardeMap;
        });

        System.out.println(biFunction.apply(StudentDatabase.getAllStudents(), student -> student.getName().startsWith("A")));
    }

}
