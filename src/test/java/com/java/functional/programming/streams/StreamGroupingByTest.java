package com.java.functional.programming.streams;

import com.java.functional.programming.functionalinterfaces.data.BlogPost;
import com.java.functional.programming.functionalinterfaces.data.BlogPostType;
import com.java.functional.programming.functionalinterfaces.data.BlogTuple;
import com.java.functional.programming.functionalinterfaces.data.Student;
import com.java.functional.programming.functionalinterfaces.data.StudentDatabase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.java.functional.programming.functionalinterfaces.data.BlogPost.createBlogPost;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.summarizingInt;
import static java.util.stream.Collectors.summingInt;
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

    /* *
     * Find all the blog posts into a list grouped by their type
     * */
    @Test
    public void streams_api_simple_grouping_to_list_by_a_single_column_test() {
        List<BlogPost> blogPosts = IntStream.rangeClosed(1, 20)
                .mapToObj(i -> createBlogPost())
                .toList();

        Map<BlogPostType, List<BlogPost>> blogPostsPerType = blogPosts.stream()
                .collect(groupingBy(BlogPost::type));

        log.info("{}", blogPostsPerType);
    }

    /* *
     * Find all the blog posts into a set grouped by their type
     * */
    @Test
    public void streams_api_simple_grouping_to_set_by_a_single_column_test() {
        List<BlogPost> blogPosts = IntStream.rangeClosed(1, 20)
                .mapToObj(i -> createBlogPost())
                .toList();

        Map<BlogPostType, Set<BlogPost>> blogPostsPerType = blogPosts.stream()
                .collect(groupingBy(BlogPost::type, toSet()));

        log.info("{}", blogPostsPerType);
    }

    /* *
     * Find all the blog posts into a list grouped by their type and calculate the count of likes each blog posts get
     * */
    @Test
    public void streams_api_simple_grouping_sum_test() {
        List<BlogPost> blogPosts = IntStream.rangeClosed(1, 20)
                .mapToObj(i -> createBlogPost())
                .toList();

        Map<BlogPostType, Integer> blogPostsLikesCount = blogPosts.stream()
                .collect(groupingBy(BlogPost::type, summingInt(BlogPost::likes)));

        log.info("{}", blogPostsLikesCount);
    }

    /* *
     * Find the blog post into a map grouped by their type and get the blogpost with maximum number of likes
     * */

    @Test
    public void max_likes_per_post_type_test() {
        List<BlogPost> blogPosts = IntStream.rangeClosed(1, 20)
                .mapToObj(i -> createBlogPost())
                .toList();

        Map<BlogPostType, Optional<BlogPost>> maxLikesPerPostType = blogPosts.stream()
                .collect(groupingBy(BlogPost::type, maxBy(Comparator.comparingInt(BlogPost::likes))));

        log.info("{}", maxLikesPerPostType);
    }

    /* *
     * The classification function is not limited to returning only a scalar or String value. The key of the resulting map could be any object as long as we make sure that we implement the necessary equals and hashcode methods.
     * To group using two fields as keys, we can use the Pair class provided in the javafx.util or org.apache.commons.lang3.tuple packages.
     * e.g: Group the blog posts in the list, by the type and author combined in an Apache Commons Pair instance.
     * */
    @Test
    public void streams_api_grouping_by_type_author_using_immutable_pair_test() {
        List<BlogPost> blogPosts = IntStream.rangeClosed(1, 20)
                .mapToObj(i -> createBlogPost())
                .toList();

        Map<ImmutablePair<BlogPostType, String>, List<BlogPost>> postsPerTypeAndAuthor = blogPosts.stream()
                .collect(groupingBy(blogPost -> new ImmutablePair<>(blogPost.type(), blogPost.author())));

        log.info("{}", postsPerTypeAndAuthor);
    }

    @Test
    public void streams_api_grouping_by_type_author_using_tuple_pair_test() {
        List<BlogPost> blogPosts = IntStream.rangeClosed(1, 20)
                .mapToObj(i -> createBlogPost())
                .toList();

        Map<BlogTuple, List<BlogPost>> postsPerTypeAndAuthor = blogPosts.stream()
                .collect(groupingBy(blogPost -> new BlogTuple(blogPost.type(), blogPost.author())));

        log.info("{}", postsPerTypeAndAuthor);
    }

    /* *
     * Group the list of BlogPosts first by author and then by type:
     * */
    @Test
    public void streams_api_grouping_by_multiple_fields_test() {
        List<BlogPost> blogPosts = IntStream.rangeClosed(1, 20)
                .mapToObj(i -> createBlogPost())
                .toList();

        Map<String, Map<BlogPostType, List<BlogPost>>> blogPostsGroupedFirstByAuthorThenType = blogPosts.stream()
                .collect(groupingBy(BlogPost::author, groupingBy(BlogPost::type)));

        log.info("{}", blogPostsGroupedFirstByAuthorThenType);
    }

    /* *
     * Getting a Summary for an Attribute of Grouped Results.
     * The Collectors API offers a summarizing collector that we can use in cases when we need to calculate the count, sum, minimum, maximum and average of a numerical attribute at the same time.
     * */
    @Test
    public void streams_api_grouping_by_types_summary_test() {
        List<BlogPost> blogPosts = IntStream.rangeClosed(1, 20)
                .mapToObj(i -> createBlogPost())
                .toList();

        Map<BlogPostType, IntSummaryStatistics> likeStatisticsPerType = blogPosts.stream()
                .collect(groupingBy(BlogPost::type, summarizingInt(BlogPost::likes)));

        log.info("{}", likeStatisticsPerType);
    }
}
