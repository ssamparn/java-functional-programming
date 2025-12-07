package com.java.functional.programming.functionalinterfaces;

import com.java.functional.programming.functionalinterfaces.data.Student;
import com.java.functional.programming.functionalinterfaces.data.StudentDatabase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Supplier;

/**
 * The Supplier Interface is a part of the java.util.function package which has been introduced since Java 8, to implement functional programming in Java.
 * It represents a function which does not take in any argument but produces a value of type T.
 * The lambda expression assigned to an object of Supplier type is used to define its get() which eventually produces a value.
 * Suppliers are useful when we don’t need to supply any value and obtain a result at the same time.
 * */
@Slf4j
public class SupplierFunctionalInterfaceTest {

    @Test
    public void supplier_simple_example_test() {
        Supplier<List<Student>> studentSupplier = StudentDatabase::getAllStudents;
        log.info("All students :{}", studentSupplier.get());
    }
}
