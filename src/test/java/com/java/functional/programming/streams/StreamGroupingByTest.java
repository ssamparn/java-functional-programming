package com.java.functional.programming.streams;

import com.java.functional.programming.functionalinterfaces.data.Student;
import com.java.functional.programming.functionalinterfaces.data.StudentDatabase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.toSet;

/* *
   * groupingBy() groupingByConcurrent() : These 2 static factory methods Collectors.groupingBy() and Collectors.groupingByConcurrent()
   * provide us with functionality similar to the ‘GROUP BY’ clause in the SQL language.
   * We use them for grouping objects by some property and storing results in a Map instance.
   * There are 3 different versions of groupingBy().
      1. groupingBy(classifier)
      2. groupingBy(classifier, downstream)
      3. groupingBy(classifier, supplier, downstream)
* */

@Slf4j
public class StreamGroupingByTest {

    @Test
    public void streams_api_grouping_by_simple_test() {
        Map<String, List<Student>> studentsGroupedByGender = StudentDatabase.getAllStudents()
                .stream()
                .collect(Collectors.groupingBy(Student::getGender));

        log.info("{}", studentsGroupedByGender); // This map will have 2 entries. Male and Female
        log.info("{}", studentsGroupedByGender.get("Male"));
        log.info("{}", studentsGroupedByGender.get("Female"));
    }

    @Test
    public void streams_api_grouping_by_students_based_on_gpa_while_assigning_custom_key_test() {
        Map<String, List<Student>> studentsGroupedByGpa = StudentDatabase.getAllStudents()
                .stream()
                .collect(Collectors.groupingBy(student -> student.getGpa() >= 3.8 ? "OUTSTANDING" : "AVERAGE"));

        log.info("{}", studentsGroupedByGpa); // This map will have 2 entries. OUTSTANDING and AVERAGE
        log.info("{}", studentsGroupedByGpa.get("OUTSTANDING"));
        log.info("{}", studentsGroupedByGpa.get("AVERAGE"));
    }

    @Test
    public void streams_api_grouping_by_students_based_on_name_and_student_gpa_test() {
        Map<String, Map<String, List<Student>>> studentsMap = StudentDatabase.getAllStudents()
                .stream()
                .collect(Collectors.groupingBy(Student::getName,
                        Collectors.groupingBy(student -> student.getGpa() >= 3.8 ? "OUTSTANDING" : "AVERAGE")));

        log.info("{}", studentsMap);
    }

    @Test
    public void streams_api_grouping_by_student_names_and_count_their_notebooks_and_sort_them_based_on_their_name_test() {
        Map<String, Integer> studentNoteBooks = StudentDatabase.getAllStudents()
                .stream()
                .collect(Collectors.groupingBy(Student::getName, Collectors.summingInt(Student::getNoteBooks)))
                .entrySet()
                .stream().sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        log.info("{}", studentNoteBooks);
    }

    @Test
    public void streams_api_grouping_by_student_names_and_put_them_in_a_set_test() {
        LinkedHashMap<String, Set<Student>> studentSetinAMap = StudentDatabase.getAllStudents()
                .stream()
                .collect(groupingBy(Student::getName, LinkedHashMap::new, toSet()));
        log.info("{}", studentSetinAMap);
    }

    // maxBy() and minBy() using groupingBy() and collectingAndThen()
    @Test
    public void streams_api_grouping_by_calculate_top_gpa_wrt_grades_test() {
        Map<Integer, Optional<Student>> studentMapOptional = StudentDatabase.getAllStudents()
                .stream()
                .collect(groupingBy(Student::getGradeLevel, maxBy(Comparator.comparing(Student::getGpa))));
        log.info("{}", studentMapOptional); // returns student as an optional object

        Map<Integer, Student> studentMap = StudentDatabase.getAllStudents()
                .stream()
                .collect(groupingBy(Student::getGradeLevel, collectingAndThen(maxBy(Comparator.comparing(Student::getGpa)), Optional::get)));
        log.info("{}", studentMap); // // returns student as an absolute object
    }

}
