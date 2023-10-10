package com.java.functional.programming.functionalinterfaces;

import com.java.functional.programming.functionalinterfaces.data.Student;
import com.java.functional.programming.functionalinterfaces.data.StudentDatabase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;

// In mathematical logic, a Predicate is a function that receives a value and returns a boolean value.
//The Predicate functional interface is a specialization of a Function that receives a generified value and returns a boolean.
// A typical use case of the Predicate lambda is to filter a collection of values:
@Slf4j
public class PredicateFunctionalInterfaceTest {

    @Test
    public void string_predicate_functional_interface_test() {
        Predicate<String> stringPredicate = string  -> string.startsWith("A");
        System.out.println(stringPredicate.test("Sashank"));
        System.out.println(stringPredicate.test("Aparna"));
    }

    @Test
    public void int_predicate_functional_interface_test() {
        Predicate<Integer> integerPredicate = i -> i % 2 == 0;
        System.out.println(integerPredicate.test(4));
        System.out.println(integerPredicate.test(7));
    }

    @Test
    public void simple_predicate_to_filter_name_lists_test() {
        List<Student> allStudents = StudentDatabase.getAllStudents();

        List<String> aStudentList = allStudents.stream()
                .map(Student::getName)
                .filter(name -> name.startsWith("A"))
                .toList();

        assertEquals("Adam", aStudentList.get(0));

        // In this example, we filtered our List of names to only leave names that start with “A” using the Predicate:
        // name -> name.startsWith("A"). But what if we wanted to apply multiple Predicates?
    }

    @Test
    public void multiple_predicate_to_filter_name_lists_test() {
        List<Student> allStudents = StudentDatabase.getAllStudents();

        List<String> aStudentList = allStudents.stream()
                .map(Student::getName)
                .filter(name -> name.startsWith("A"))
                .filter(name -> name.length() < 5)
                .toList();

        assertEquals("Adam", aStudentList.get(0));

        // In this example, we used two filters — one for each Predicate.
    }

    @Test
    public void complex_predicate_to_filter_name_lists_test() {
        List<Student> allStudents = StudentDatabase.getAllStudents();

        List<String> aStudentList = allStudents.stream()
                .map(Student::getName)
                .filter(name -> name.startsWith("A") && name.length() < 5)
                .toList();

        assertEquals("Adam", aStudentList.get(0));

        // In this example, instead of multiple filters we used one filter with a complex Predicate.
        // This option is more flexible than the first one, as we can use bitwise operations to build the Predicate as complex as we want.
    }

    // If we don’t want to build a complex Predicate using bitwise operations, Java 8 Predicate has useful methods that we can use to combine Predicates.
    // We’ll combine Predicates using the methods Predicate.and(), Predicate.or(), and Predicate.negate().
    @Test
    public void combining_predicate_using_and_default_method_tests() {
        List<Student> allStudents = StudentDatabase.getAllStudents();

        Predicate<String> nameStartsWithA =  name -> name.startsWith("A");
        Predicate<String> nameLengthLessThan5 =  name -> name.length() < 5;

        List<String> aStudentList = allStudents.stream()
                .map(Student::getName)
                .filter(nameStartsWithA.and(nameLengthLessThan5))
                .toList();

        assertEquals(1, aStudentList.size());
        assertEquals("Adam", aStudentList.get(0));
        // As we can see, the syntax is fairly intuitive, and the method names suggest the type of operation.
        // Using and(), we’ve filtered our List by extracting only names that fulfill both conditions.
    }

    @Test
    public void combining_predicate_using_or_default_method_tests() {
        List<Student> allStudents = StudentDatabase.getAllStudents();

        Predicate<String> nameStartsWithA =  name -> name.startsWith("A");
        Predicate<String> nameLengthLessThan5 =  name -> name.length() < 5;

        List<String> aStudentList = allStudents.stream()
                .map(Student::getName)
                .filter(nameStartsWithA.or(nameLengthLessThan5))
                .toList();

        assertEquals(2, aStudentList.size());
        assertEquals("Adam", aStudentList.get(0));
        assertEquals("Dave", aStudentList.get(1));
        // As we can see, the syntax is fairly intuitive, and the method names suggest the type of operation.
        // Using or(), we’ve filtered our List by extracting only names that fulfill either conditions.
    }

    @Test
    public void combining_predicate_using_negate_default_method_tests() {
        List<Student> allStudents = StudentDatabase.getAllStudents();

        Predicate<String> nameStartsWithA =  name -> name.startsWith("A");
        Predicate<String> nameLengthLessThan5 =  name -> name.length() < 5;

        List<String> aStudentList = allStudents.stream()
                .map(Student::getName)
                .filter(nameStartsWithA.or(nameLengthLessThan5.negate()))
                .toList();

        assertEquals(5, aStudentList.size());
        assertEquals("Adam", aStudentList.get(0));
        assertEquals("Jenny", aStudentList.get(1));
        assertEquals("Emily", aStudentList.get(2));
        assertEquals("Sophia", aStudentList.get(3));
        assertEquals("James", aStudentList.get(4));
        // As we can see, the syntax is fairly intuitive, and the method names suggest the type of operation.
        // Using negate(), we’ve filtered our List by extracting only names that fulfill either conditions.
    }

    @Test
    public void combining_predicate_using_default_method_tests() {
        List<Student> allStudents = StudentDatabase.getAllStudents();

        List<Predicate<String>> allPredicates = new ArrayList<>();
        allPredicates.add(name -> name.startsWith("A"));
        allPredicates.add(name -> name.length() < 5);

        List<String> aStudentList = allStudents.stream()
                .map(Student::getName)
                .filter(allPredicates.stream().reduce(x -> true, Predicate::and))
                .toList();

        assertEquals(1, aStudentList.size());
    }

}
