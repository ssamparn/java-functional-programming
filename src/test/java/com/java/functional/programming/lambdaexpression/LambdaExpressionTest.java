package com.java.functional.programming.lambdaexpression;

import com.java.functional.programming.functionalinterfaces.data.Student;
import com.java.functional.programming.functionalinterfaces.data.StudentDatabase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

@Slf4j
public class LambdaExpressionTest {

    // Lambda is equivalent to a function (method) without a name hence also referred to as Anonymous functions.
    // In Java, Lambda expressions basically express instances of functional interfaces (An interface with a single abstract method is called a functional interface).
    // Lambda Expressions in Java are the same as lambda functions which are the short block of code that accepts input as parameters and returns a resultant value.

    // Functionalities of Lambda Expression in Java:
    // Lambda Expressions implement the only abstract function and therefore implement functional interfaces. Lambda expressions provide the below functionalities.

    //   1. Enable to treat functionality as a method argument, or code as data.
    //   2. A function that can be created without belonging to any class.
    //   3. A lambda expression can be passed around as if it was an object and executed on demand.
    @Test
    public void simple_lambda_expression_for_runnable_test() {
        /**
         * Prior to Java 8
         * */
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("Inside Runnable");
            }
        };
        new Thread(runnable).start();

        /**
         * Using to Java 8 Lambda
         * */
        Runnable lambdaRunnable = () -> System.out.println("Inside Lambda Runnable");
        new Thread(lambdaRunnable).start();
    }

    @Test
    public void simple_lambda_expression_with_comparator_test() {
        Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2); //  0 -> o1 == o2
                                         //  1 -> o1 > o2
                                         // -1 -> o1 < o2
            }
        };

        System.out.println(comparator.compare(22, 22));
        System.out.println(comparator.compare(23, 22));
        System.out.println(comparator.compare(22, 23));

        // Comparator<Integer> lambdaComparator = ((o1, o2) -> o1.compareTo(o2)); // using lambda expression
        Comparator<Integer> lambdaComparator = (Integer::compareTo); // using method reference
        System.out.println(lambdaComparator.compare(22, 22));
        System.out.println(lambdaComparator.compare(23, 22));
        System.out.println(lambdaComparator.compare(22, 23));
    }

    @Test
    public void simple_lambda_expression_for_comparator_test() {
        List<Student> allStudents = StudentDatabase.getAllStudents();

        allStudents.sort(Comparator.comparing(Student::getName));

        System.out.println(allStudents);
    }

}
