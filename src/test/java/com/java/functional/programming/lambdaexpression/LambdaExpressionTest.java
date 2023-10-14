package com.java.functional.programming.lambdaexpression;

import com.java.functional.programming.functionalinterfaces.data.Student;
import com.java.functional.programming.functionalinterfaces.data.StudentDatabase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

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

    // Lambdas and Local Variables.

    /**
     * What is a Local Variable?
     * Any variable that is declared inside a method is called a local variable.
     *
     * Lambdas have some restrictions on using the local variable.
     *      1. Not allowed to use the same local variable name as lambda parameter or inside lambda body.
     *      2. Not allowed to re-assign a value to a local variable.
     But Lambda does not have such restrictions on instance variable.
     * */

    /**
     * Variable defined by the enclosing scope of a lambda expression are accessible within the lambda expression.
     * For example, a lambda expression can use an instance or static variable defined by its enclosing class.
     *
     * However, when a lambda expression uses a local variable from its enclosing scope,
     * a special situation is created that is referred to as a variable capture.
     * In this case, a lambda expression may only use local variables that are effectively final.
     * An effectively final variable is one whose value does not change after it is first assigned.
     * There is no need to explicitly declare such a variable as final, although doing so would not be an error.
     * */
    @Test
    public void lambda_local_variable_case_1_test() {
        // case: 1. variable is already defined in scope
        int i = 0;
        // Consumer<Integer> intConsumer = i -> System.out.println("Value is: " + i);
    }

    @Test
    public void lambda_local_variable_case_2_test() {

        // case: 2. reassigning an instance variable is with a new value. As variable used in lambda expression must be final or effectively final.

        int value = 4;

        Consumer<Integer> intConsumer = i -> {
//            value++; // Not allowed
            System.out.println("Value is: " + value);
        };

//        value = 6;   // Not allowed

        intConsumer.accept(4);
    }

    static int value = 4;

    @Test
    public void lambda_local_variable_case_3_test() {
        // case: 3. reassigning a static variable is with a new value.

        Consumer<Integer> intConsumer = i -> {
            value++;
            System.out.println("Value is: " + value);
        };

        value = 6;
        intConsumer.accept(value);
    }
}
