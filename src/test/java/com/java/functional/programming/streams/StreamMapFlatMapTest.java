package com.java.functional.programming.streams;

import com.java.functional.programming.functionalinterfaces.data.Student;
import com.java.functional.programming.functionalinterfaces.data.StudentDatabase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class StreamMapFlatMapTest {

    /* *
     * Stream API Operations with map() & flatMap():
     * Both map() and flatMap() are used for transformation and mapping operations.
     * map() function produces one output for one input value, whereas flatMap() function produces an arbitrary no of values as output (ie zero or more than zero) for each input value.
     * One-to-one mapping occurs in map(), whereas One-to-many mapping occurs in flatMap().
     * map() is used only for transformation, where as flatMap() is used both for transformation and mapping.
     * */
    @Test
    public void streams_api_map_method_test() {
        List<Student> allStudents = StudentDatabase.getAllStudents();

        Set<String> studentNames = allStudents.stream()
                .map(Student::getName)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        log.info("{}", studentNames);
    }

    @Test
    public void streams_api_flatmap_method_test() {
        List<Student> allStudents = StudentDatabase.getAllStudents();

        List<String> studentNameLenghts = allStudents.stream()
                .map(Student::getActivities)
                .flatMap(List::stream)
                .toList();

        log.info("{}", studentNameLenghts);
    }

}
