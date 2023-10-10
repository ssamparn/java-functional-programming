package com.java.functional.programming.functionalinterfaces;

import com.java.functional.programming.functionalinterfaces.data.Student;
import com.java.functional.programming.functionalinterfaces.data.StudentDatabase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Slf4j
public class CombiningPredicateWithConsumerTest {

    @Test
    public void consumer_predicate_combination_test() {
        Predicate<Student> gradeLevelPredicate = student -> student.getGradeLevel() >= 3;
        Predicate<Student> gpaPredicate = student -> student.getGpa() >= 3.9;
        BiConsumer<String, List<String>> studentBiConsumer = (name, activities) -> log.info("Student name and activities with grade level > 3 and GPA > 3.9 are: {}, {}", name, activities);

        Consumer<Student> studentConsumer = student -> {
            if (gradeLevelPredicate.and(gpaPredicate).test(student)) {
                studentBiConsumer.accept(student.getName(), student.getActivities());
            }
        };

        StudentDatabase.getAllStudents().forEach(studentConsumer);
    }
}
