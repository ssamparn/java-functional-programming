package com.java.functional.programming.streams;

import com.java.functional.programming.functionalinterfaces.data.Student;
import com.java.functional.programming.functionalinterfaces.data.StudentDatabase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

/* *
 * Java Streams<T>:
 * Simply put, streams are wrappers around a data source, allowing us to operate with that data source and making bulk processing convenient and fast.
 * A stream does not store data and, in that sense, is not a data structure. It also never modifies the underlying data source.
 * Introduced in Java 8, the Stream API is used to process collections of objects.
 * A stream is a sequence of objects that supports various methods which can be pipelined to produce the desired result.
 *
 * Java Stream Creation: There are many ways to create a stream instance of different sources.
 * Once created, the instance will not / can not modify its source, therefore allowing the creation of multiple instances from a single source.
 *
 * Understanding a Java Stream pipeline:
 * In a Java stream pipeline, the data flows from the source to the terminal operation. In between we can have zero or more intermediate operators.
 * The Java stream will have some source. The source could be any collection like list or set or an array. We can also use a supplier etc. just by invoking supplier again and again we can create the stream of data.
 * Intermediate operators build the stream pipeline to transform the stream into another stream.
 * As we can have zero or more intermediate operators which are lazy in nature as the data will flow via this pipeline only when we add a terminal operator.
 * The terminal operator is the last operation in the stream pipeline, which might return a result like a list, or it can perform some action, for example using the for-each method, etc.
 * We call this everything together a Java Stream pipeline which contains a source, one or few intermediate & one terminal operator.
 * Only when we have the terminal operator, the data will flow from the source. This Java Stream pipeline is not a data structure.
 *
 * Short-circuiting Streams:
 * It is a way of ending the stream early, instead of processing all the items in the stream.
 * For example, your requirement could be like. Find the first number which is divisible by 3 in a stream of integers.
 * In that case, your stream source is a List<Integer>. Let's imagine like that it might have million numbers will be processed one by one.
 * As soon as we find the very first number which is divisible by 3, Just stop processing. So this is called short-circuiting.
 *
 * Stateless operators:
 * The intermediate operators could be stateless or stateful operator.
 * When we say stateless operator, a spot of that each element is processed independently.
 * It does not need to know anything about the other items in the stream since each item is handled independently, it can be parallelized easily.
 * e.g: Operators like map(), filter(), etc. these are stateless operators.
 *
 * Stateful operators:
 * The stateful operator needs to know about other elements in the stream.
 * For example, if you are trying to find unique items, how do you know an item is unique?
 * Only by comparing it with other items you have already seen in the stream, right?
 * e.g: Operators like count(), sum(), sorted(), distinct(), etc. You have to remember something as you keep processing the elements.
 * So that is what makes them stateful.
 * These operations can also be parallelized, but there might be some challenges involved.
 *
 * */
@Slf4j
public class StreamsCreationTest {

    /* *
     * Stream.empty(): We often use the empty() method upon creation to avoid returning null for streams with no element
     * */
    @Test
    public void empty_stream_creation_test() {
        Stream<String> emptyStream = Stream.empty();
    }

    /* *
     * Stream.of(): creates a stream of certain values passed to this method.
     * */
    @Test
    public void stream_from_collection_creation_test() {
        Collection<String> collection = Arrays.asList("a", "b", "c");
        Stream<String> streamOfCollection = collection.stream();

        Stream<String> streamOfArray = Stream.of("a", "b", "c");

        Stream<String> streamBuilder = Stream.<String>builder().add("a").add("b").add("c").build();
        // When builder is used, the desired type should be additionally specified in the right part of the statement,
        // otherwise the build() method will create an instance of the Stream<Object>:
    }

    /* *
     * Stream.iterate() and Stream.generate() are used to create infinite streams.
     * iterate(): One way of creating an infinite stream is by using the iterate() method.
     * */
    @Test
    public void streams_api_iterate_method_test() {
        Stream<Integer> streamIterated = Stream
                .iterate(1, n -> n + 2)
                .limit(5);

        log.info("{}", streamIterated.collect(toList())); // [1, 3, 5, 7, 9]

    }

    /* *
     * generate(): The generate() method accepts a Supplier<T> for element generation.
     * As the resulting stream is infinite, the developer should specify the desired size, or the generate() method will work until it reaches the memory limit.
     * */
    @Test
    public void streams_api_generate_method_test() {
        Stream<String> streamGenerated = Stream.generate(() -> "element").limit(10);

        log.info("{}", streamGenerated.collect(toList()));
    }

    @Test
    public void streams_api_test() {
        List<Student> allStudents = StudentDatabase.getAllStudents();

        Map<String, List<String>> studentNameActivitiesMap = allStudents.stream()
                .filter(student -> student.getGradeLevel() >= 3)
                .filter(student -> student.getGpa() >= 3.9)
                .collect(Collectors.toMap(Student::getName, Student::getActivities));

        log.info("{}", studentNameActivitiesMap);
    }

    /* *
     * Stream.peek()
     * */
    @Test
    public void debug_streams_api_test() {
        List<Student> allStudents = StudentDatabase.getAllStudents();

        Map<String, List<String>> studentNameActivitiesMap = allStudents.stream()
                .filter(student -> student.getGradeLevel() >= 3)
                .peek(student -> log.info("Students with grade level greater than 3: {}", student)) // peek() is used to debug a stream amid intermediate operations.
                .filter(student -> student.getGpa() >= 3.9)
                .collect(Collectors.toMap(Student::getName, Student::getActivities));

        log.info("{}", studentNameActivitiesMap);
    }




    // count(): Returns a long with total number of elements in the stream.
    @Test
    public void streams_api_count_method_test() {
        List<String> stringList = Arrays.asList("A", "B", "C", "D", "A", "B", "C");
        long uniqueCharsLength = stringList.stream()
                .distinct()
                .count();
        System.out.println(uniqueCharsLength);


        List<Student> allStudents = StudentDatabase.getAllStudents();
        long uniqueActivitiesLength = allStudents.stream()
                .map(Student::getActivities)
                .flatMap(List::stream)
                .distinct()
                .count();
        System.out.println(uniqueActivitiesLength);
    }

    // sorted(): Sort the elements in the stream .
    @Test
    public void streams_api_sorted_method_test() {
        List<Student> allStudents = StudentDatabase.getAllStudents();
        List<String> uniqueActivitiesSorted = allStudents.stream()
                .map(Student::getActivities)
                .flatMap(List::stream)
                .distinct()
                .sorted()
                .collect(toList());
        System.out.println(uniqueActivitiesSorted);
    }

    // Customized sort using comparator
    @Test
    public void streams_api_customized_sorted_with_comparator_test() {
        List<Student> allStudents = StudentDatabase.getAllStudents();
        List<String> sortedStudents = allStudents.stream()
                .sorted(Comparator.comparing(Student::getName))
                .map(Student::getName)
                .collect(toList());
        System.out.println(sortedStudents);

        List<String> sortedStudentsAsPerGrades = allStudents.stream()
                .sorted(Comparator.comparing(Student::getGradeLevel).reversed())
                .map(Student::getName)
                .collect(toList());
        System.out.println(sortedStudentsAsPerGrades);
    }

    // filter(): filters the elements in the stream.
    // Input to the filter is a predicate functional interface
    @Test
    public void streams_api_filter_test() {
        List<Student> allStudents = StudentDatabase.getAllStudents();

        List<String> femaleStudents = allStudents.stream()
                .filter(student -> student.getGender().equals("Female"))
                .map(Student::getName)
                .collect(toList());
        System.out.println(femaleStudents);
    }

    // reduce(): This is a terminal operation used to reduce the contents of a stream to a single value.
    @Test
    public void streams_api_reduce_test() {
        List<Student> allStudents = StudentDatabase.getAllStudents();

        Optional<String> studentWithHighestGpa = allStudents.stream()
                .reduce((s1, s2) -> s1.getGpa() > s2.getGpa() ? s1 : s2)
                .map(Student::getName);

        studentWithHighestGpa.ifPresent(System.out::println);
    }

    @Test
    public void streams_api_find_the_longest_string_using_reduce_test() {
        List<String> words = Arrays.asList("GFG", "Geeks", "for", "GeeksQuiz", "GeeksforGeeks");

        Optional<String> longestString = words.stream()
                .reduce((word1, word2) -> word1.length() > word2.length() ? word1 : word2);

        longestString.ifPresent(System.out::println);
    }

    @Test
    public void streams_api_find_max_value_using_reduce_test() {
        List<Integer> integerList = Arrays.asList(-9, -18, 0, 25, 4);

        Integer maxInteger = integerList.stream()
                .reduce((i1, i2) -> i1 > i2 ? i1 : i2)
                .get();

//        Integer maxInteger = integerList.stream()
//                .max(Integer::compareTo)
//                .get();

        System.out.println(maxInteger);
    }

    @Test
    public void streams_api_find_min_value_using_reduce_test() {
        List<Integer> integerList = Arrays.asList(-9, -18, 0, 25, 4);

        Integer minInteger = integerList.stream()
                .reduce((i1, i2) -> i1 < i2 ? i1 : i2)
                .get();

//        Integer minInteger = integerList.stream()
//                .min(Integer::compareTo)
//                .get();

        System.out.println(minInteger);
    }

    @Test
    public void streams_api_find_the_sum_of_all_elemetns_using_reduce_test() {
        List<Integer> integers = Arrays.asList(-2, 0, 4, 6, 8);

        int sum = integers.stream()
                .reduce(0, Integer::sum);

        System.out.println("The sum of all elements is " + sum);
    }

    @Test
    public void streams_api_implement_filter_using_reduce_test() {
        List<String> stringArrayOfIntegers = List.of("1", "2", "3", "4", "5", "6", "7");
        List<Integer> evenNumbers = stringArrayOfIntegers.stream()
                .map(Integer::parseInt)
                .reduce(new ArrayList<>(), (a, b) -> {
                            if (b % 2 == 0) a.add(b);
                            return a;
                        },
                        (a, b) -> {
                            a.addAll(b);
                            return a;
                        });
        System.out.println(evenNumbers);
    }

    // classic map() + filter() + reduce() pattern: The three methods, map, filter, and reduce, are the cornerstone of any functional programming.
    // Usually, our data pipelines consist of one or more intermediate operations, transforming (aka mapping) and/or filtering elements, and a terminal operation to gather the data again (aka reducing).
    @Test
    public void classic_map_filter_reduce_pattern_test() {
        List<Student> allStudents = StudentDatabase.getAllStudents();

        Optional<Integer> numberOfNoteBooks = allStudents.stream()
                .filter(student -> student.getGradeLevel() >= 3)
                .filter(student -> student.getGender().equals("Female"))
                .map(Student::getNoteBooks)
                .reduce(Integer::sum);
        numberOfNoteBooks.ifPresent(System.out::println);
    }

    // limit(): limits the "n" number of elements to be processed in the stream.
    // skip(): skips the "n" number of elements from the stream.
    @Test
    public void streams_api_limit_test() {
        int intSumLimit = IntStream.range(1, Integer.MAX_VALUE)
                .limit(10)
                .reduce(Integer::sum)
                .getAsInt();

        System.out.println(intSumLimit);
    }

    @Test
    public void streams_api_skip_test() {
        int intSumSkipped = IntStream.range(1, 4) // Stream.of(1, 2, 3)
                .skip(1) // Stream.of(2, 3)
                .reduce(Integer::sum) // 5
                .getAsInt();

        System.out.println(intSumSkipped);
    }

    // anyMatch(), allMatch() and noneMatch() takes in a predicate as an input and returns a boolean as an output.

    // anyMatch(): Returns true if any one of the element in the stream matches the predicate, otherwise false.
    @Test
    public void streams_api_any_match_test() {
        List<String> stringList = List.of("Sashank", "Aparna", "Monalisa", "Sushant", "Nalini");

        for (String names : stringList) {
            if (names.contains("Aparna")) {
                System.out.println(true);
            }
        }

        // This code can be changed just with one line of Java 8 code.
        boolean nameExist = stringList.stream()
                .anyMatch(name -> name.contains("Aparna"));
        System.out.println(nameExist);

    }

    // allMatch(): Returns true if any all the elements in the stream matches the predicate, otherwise false.
    @Test
    public void streams_api_all_match_test() {
        List<Student> allStudents = StudentDatabase.getAllStudents();
        boolean allMatchWithRespectToGpa = allStudents.stream()
                .allMatch(student -> student.getGpa() >= 3.9); // false

        System.out.println(allMatchWithRespectToGpa);

        boolean allMatchWithRespectToNames = allStudents.stream()
                .allMatch(student -> student.getName().length() > 1); // true

        System.out.println(allMatchWithRespectToNames);
    }

    // noneMatch(): Just opposite to allMatch(). Returns true if none of the elements in the stream matches the predicate, otherwise false.
    @Test
    public void streams_api_none_match_test() {
        // check if no element in the Stream contains any numeric/digit character.
        Stream<String> stream = Stream.of("one", "two", "three", "four");
        boolean match = stream.noneMatch(s -> s.contains("\\d+")); // true
        System.out.println(match);
    }

    // Both findFirst() and findAny() used to find an element in the stream.
    // Both the functions returns the result of type Optional.

    // findFirst(): The findFirst() method finds the first element in a Stream.
    // So, we use this method when we specifically want the first element from a sequence.
    @Test
    public void streams_api_find_first_test() {
        List<String> list = Arrays.asList("A", "B", "C", "D");

        // The behavior of the findFirst method does not change in the parallel scenario.
        // If the encounter order exists, it will always behave deterministically.
        Optional<String> result = list.stream().findFirst();
        assertThat(result.get(), is("A"));

        Optional<String> resultWithParallelStream = list.stream().findFirst();
        assertThat(resultWithParallelStream.get(), is("A"));
    }

    // findAny(): As the name suggests, the findAny() method allows us to find any element from a Stream.
    // We use it when we’re looking for an element without paying attention to the encounter order.
    @Test
    public void streams_api_find_any_test() {
        List<String> list = Arrays.asList("A","B","C","D");

        Optional<String> result = list.stream()
                .findAny(); // In a non-parallel operation, it will most likely return the first element in the Stream, but there is no guarantee for this.
        System.out.println(result.get());
        assertThat(result.get(), is("A"));

        // For maximum performance when processing the parallel operation, the result cannot be reliably determined:
        Optional<String> resultWithParallelStream = list.parallelStream()
                .findAny();
        System.out.println(resultWithParallelStream.get());
        assertThat(resultWithParallelStream.get(), anyOf(is("A"), is("B"), is("C"), is("D")));
    }

    /**
     * Streams API: Short Circuiting Operations: Operation which helps us to optimize the computation and improve performance by avoiding unnecessary processing of elements.
     * e.g: We all know how logical AND(&&), logical OR(||) operations are performed.
     *  1. If the left operand of && is false, the right operand is not evaluated because the overall result will always be false.
     *  2. If the left operand of || is true, the right operand is not evaluated because the overall result will always be true.
     *
     *  So this is nothing but a “Short-Circuit Operation”
     * */

    /**
     * There are certain stream functions which does not have to iterate the whole stream to evaluate the result.
     *  1. limit().
     *  2. findFirst() and findAny().
     *  3. anyMatch(), allMatch() and noneMatch().
     * */

    @Test
    public void streams_api_short_circuit_operations() {
        Integer[] numbers = {11, 17, 56, 0, 10, 15};
        Stream<Integer> limitNumberStream = Arrays.stream(numbers);
        // limit() -> one of the short-circuit intermediate operation method.
        // Intermediate operation is operation which is applied on stream to transform, filter etc. And doesn't produce final result.
        List<Integer> numberList = limitNumberStream
                .limit(2) // Returns the stream of maxSize 2, and doesn't perform any further operation on stream elements.
                .collect(Collectors.toList()); //Collecting the stream into the List.
        System.out.println(numberList); //prints [2, 1]


        Stream<Integer> findFirstNumberStream = Arrays.stream(numbers);
        int num = findFirstNumberStream.filter(n -> n % 2 == 0)
                .findFirst()
                .orElse(100);
        System.out.println(num); //prints 56

        Stream<Integer> noneMatchNumberStream = Arrays.stream(numbers);
        boolean isNonNegativeNumber = noneMatchNumberStream.noneMatch(n -> n < 1);
        System.out.println(isNonNegativeNumber); //prints false.
    }

    /**
     * Primitive streams are limited mainly because of boxing overhead and because creating specialized streams for other primitives isn’t that useful in many cases.
     * Primitive Streams or Numeric Streams: Represents the primitive values in a stream.
     * 1. IntStream
     * 2. LongStream
     * 3. DoubleStream
     * */

    @Test
    public void streams_api_int_stream_test() {
        int sum1 = IntStream.range(1, 10)
                .reduce(Integer::sum)
                .getAsInt();

        int sum2 = IntStream.rangeClosed(1, 10)
                .reduce(Integer::sum)
                .getAsInt();

        System.out.println(sum1); // 10 is not included in the sum
        System.out.println(sum2); // 10 is included in the sum

        System.out.println(IntStream.range(1, 10).count()); // 10 is not included in the count
        System.out.println(IntStream.rangeClosed(1, 10).count()); // 10 is included in the count
    }

    // boxing and unboxing in IntStream
    // boxing: primitive type to wrapper type
    // unboxing: wrapper type to primitive type
    @Test
    public void streams_api_boxing_unboxing_test() {

        List<Integer> integerList = IntStream.rangeClosed(1, 10)
                .filter(i -> i % 2 == 0)
                .boxed()
                .toList();
        System.out.println(integerList);

        // unboxing
        List<Integer> list = List.of(1, 2, 3, 4, 5);

        int sum = list.stream()
                .mapToInt(Integer::intValue)
                .sum();
        System.out.println(sum);
    }

    // mapToObj(), mapToLong() and mapToDouble()
    @Test
    public void streams_api_map_to_object_test() {
        IntStream.range(3, 8)
                .mapToObj(Integer::toBinaryString)
                .forEach(System.out::print);

        IntStream.rangeClosed(1, 5)
                .mapToLong(i -> i)
                .forEach(System.out::print);

        IntStream.rangeClosed(1, 5)
                .mapToDouble(i -> i)
                .forEach(System.out::println);
    }

    /**
     * Streams API Terminal Operations
     * */

    // joining(): The joining() method of Collectors class in Java, is used to join various elements of a character or string array into a single string object.
    @Test
    public void streams_api_joining_with_an_array_of_characters_test() {
        char[] charArray = { 'G', 'e', 'e', 'k', 's', 'f', 'o',
                'r', 'G', 'e', 'e', 'k', 's' };

        String joinedString = Stream.of(charArray)
                .map(String::new)
                .collect(Collectors.joining());

        System.out.println(joinedString);
    }

    @Test
    public void streams_api_joining_with_list_of_characters_test() {
        List<Character> characters = Arrays.asList(
                'G', 'e', 'e', 'k', 's', 'f', 'o', 'r', 'G',
                'e', 'e', 'k', 's');

        String joinedString = characters.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());

        System.out.println(joinedString);
    }

    @Test
    public void streams_api_joining_with_list_of_strings_test() {
        String joinedString = Arrays.asList("Geeks", "for", "Geeks")
                .stream()
                .collect(Collectors.joining());

        System.out.println(joinedString);
    }

    @Test
    public void streams_api_joining_with_list_of_characters_with_delimiter_test() {
        List<Character> characters = Arrays.asList(
                'G', 'e', 'e', 'k', 's', 'f', 'o', 'r', 'G',
                'e', 'e', 'k', 's');

        String joinedString = characters.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        System.out.println(joinedString);
    }

    @Test
    public void streams_api_joining_with_list_of_strings_with_prefix_and_suffix_test() {
        String joinedString = Arrays.asList("Geeks", "for", "Geeks")
                .stream()
                .collect(Collectors.joining(" ", "{", "}"));

        System.out.println(joinedString);
    }

    @Test
    public void streams_api_print_all_student_names_with_a_proper_format_test() {
        List<Student> allStudents = StudentDatabase.getAllStudents();

        String formattedStudentNames = allStudents.stream()
                .map(Student::getName)
                .collect(Collectors.joining(" - ", "( ", " )"));

        System.out.println(formattedStudentNames);
    }

    // counting():
    @Test
    public void streams_api_counting_test() {
        // find the total number of students received from the database
        Long totalStudents = StudentDatabase.getAllStudents()
                .stream()
                .collect(counting()); // can be replaced with count()

        System.out.println(totalStudents);
    }

    // mapping(): mapping() of collector applies a transformation function first and then collects the data in a collection (could be any type of collection)
    @Test
    public void streams_api_mapping_test() {
        List<String> studentNamesWithMapping = StudentDatabase.getAllStudents()
                .stream()
                .collect(Collectors.mapping(Student::getName, toList())); // collect(mapping())' can be replaced with 'map().collect()
        System.out.println(studentNamesWithMapping);

        List<String> studentNames = StudentDatabase.getAllStudents()
                .stream()
                .map(Student::getName)
                .collect(toList());
        System.out.println(studentNames);
    }

    // maxBy() and minBy(): maxBy() and minBy() receives a Comparator as an input parameter and Optional as an Output.
    // maxBy() and minBy() returns the largest and smallest element according to a given Comparator.

    @Test
    public void streams_api_max_by_min_by_test() {
        Optional<Student> studentWithHighestGpa = StudentDatabase.getAllStudents()
                .stream()
                .collect(Collectors.maxBy(Comparator.comparing(Student::getGpa))); // collect(maxBy())' can be replaced with 'max()
        studentWithHighestGpa.ifPresent(System.out::println);


        Optional<Student> studentWithHighestGpa1 = StudentDatabase.getAllStudents()
                .stream()
                .max(Comparator.comparing(Student::getGpa));
        studentWithHighestGpa1.ifPresent(System.out::println);


        Optional<Student> studentWithLowestGpa = StudentDatabase.getAllStudents()
                .stream()
                .collect(Collectors.minBy(Comparator.comparing(Student::getGpa))); // collect(maxBy())' can be replaced with 'max()
        studentWithLowestGpa.ifPresent(System.out::println);


        Optional<Student> studentWithLowestGpa1 = StudentDatabase.getAllStudents()
                .stream()
                .min(Comparator.comparing(Student::getGpa));
        studentWithLowestGpa1.ifPresent(System.out::println);
    }

    // summingInt(): summingInt() method of Collector is going to return the sum as a result.
    // avearagingInt(): averagingInt() method of Collector is going to return the average as a result.

    @Test
    public void streams_api_summing_averaging_int_test() {
        Integer sumOfAllNoteBooks = StudentDatabase.getAllStudents()
                .stream()
                .collect(Collectors.summingInt(Student::getNoteBooks)); // collect(summingInt())' can be replaced with 'mapToInt().sum()
        System.out.println(sumOfAllNoteBooks);

        Integer sumOfAllNoteBooks1 = StudentDatabase.getAllStudents()
                .stream()
                .mapToInt(Student::getNoteBooks).sum();
        System.out.println(sumOfAllNoteBooks1);

        Double averageOfAllNoteBooks = StudentDatabase.getAllStudents()
                .stream()
                .collect(Collectors.averagingInt(Student::getNoteBooks));
        System.out.println(averageOfAllNoteBooks);
    }


}
