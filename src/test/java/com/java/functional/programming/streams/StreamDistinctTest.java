package com.java.functional.programming.streams;

import com.java.functional.programming.functionalinterfaces.data.Student;
import com.java.functional.programming.functionalinterfaces.data.StudentDatabase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class StreamDistinctTest {

    /* *
     * distinct(): Returns a stream with unique elements.
     * */
    @Test
    public void streams_api_distinct_method_test() {
        List<String> stringList = Arrays.asList("A", "B", "C", "D", "A", "B", "C");
        List<String> distinctChars = stringList.stream()
                .distinct()
                .toList();
        log.info("{}", distinctChars);
    }

    @Test
    public void streams_api_distinct_all_activities_test() {
        List<Student> allStudents = StudentDatabase.getAllStudents();
        List<String> allUniqueActivities = allStudents.stream()
                .map(Student::getActivities)
                .flatMap(List::stream)
                .distinct()
                .toList();
        log.info("{}", allUniqueActivities);
    }
}
