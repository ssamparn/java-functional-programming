package com.java.functional.programming.streamgatherers.hierarchicaldatastructure;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

/**
 * In Java, we can easily create a stream from a collection object like a List, Map or an Array. But what if the data is hierarchical.
 * What is a Hierarchical Data Structure?
 * A hierarchical data structure organizes data in a tree-like form, where elements (called nodes) are arranged in levels, and each node can have parent-child relationships. The topmost node is the root, and nodes below it are children, forming a hierarchy.
 *
 * Key Characteristics:
 *    - Parent-Child Relationship: Each node (except root) has exactly one parent.
 *    - Levels: Data is organized in levels (root → intermediate → leaf).
 *  Examples:
 *    - File System: Directories and subdirectories.
 *    - Organization Chart: CEO → Managers → Employees.
 *    - XML/HTML DOM: Nested tags.
 *    - Tree Data Structures: Binary trees, N-ary trees.
 *
 *  Why hierarchical?
 *    - Efficient for representing nested relationships.
 *    - Common in data modeling, UI components, and structured documents.
 *
 *  How can Stream Gatherers be useful?
 *    Stream Gatherers (introduced in Java 21 as part of the new Stream API enhancements) allow custom collection logic during streaming operations.
 *    They are more powerful than Collectors because they:
 *       - Support multiphase processing.
 *       - Can transform, accumulate, and finalize data in flexible ways.
 *       - Work well for complex aggregations, including hierarchical structures.
 *  Use Case: Building Hierarchical Structures from Streams
 *   Imagine you have a flat list of items with id and parentId and want to build a tree using streams. A gatherer can:
 *       - Group items by parent.
 *       - Recursively assemble children under their parent.
 *       - Produce a hierarchical structure in one streaming pipeline.
 *  Why Gatherers help here:
 *     - You can accumulate data in a custom structure (like a map).
 *     - Then finalize it into a hierarchical tree in one pass.
 *     - Avoids multiple intermediate collections or imperative loops.
 *
 * How does it work?
 * By Breadth First Expansion.
 *   1. Start with a root item (a web page, a top folder, CEO)
 *   2. Get its children (linked pages, subfolders, direct reports)
 *   3. For each child, get their children.
 *   4. Repeat Step 3 again and again.
 *   5. Continue until there is no more items to explore.
 * */
@Slf4j
public class FlattenHierarchicalDataStructure {

    record Employee(String title, List<Employee> directReports) {

    }

    @Test
    public void flattenEmployeeHierarchicalDataStructureTest() {
        Stream.of(getAllEmployees())
               .gather(expand(e -> e.directReports.stream())) // function should return a Stream of Child element for a given element.
               .map(Employee::title)
               .forEach(employee -> log.info("employee :{}", employee));
    }

    /**
     * We’ll create a custom Gatherer<Employee, Deque<Employee>, Employee> to flatten hierarchy
     *  - Uses a stack (Deque) to traverse the hierarchy.
     *  - Pushes each employee’s title downstream.
     *  - Adds direct reports to the stack for further processing.
     * */
    private static <T> Gatherer<T, ?, T> expand(Function<T, Stream<T>> function) {
        return Gatherer.ofSequential(
                () -> new BreadthFirstExpander<>(function),
                Gatherer.Integrator.ofGreedy(BreadthFirstExpander::integrate),
                BreadthFirstExpander::finish
        );
    }

    /**
     * Static inner class to build a generic flattenHierarchy gatherer.
     */
    private static class BreadthFirstExpander<T> {
        private final Deque<T> deque;
        private final Function<T, Stream<T>> function;

        public BreadthFirstExpander(Function<T, Stream<T>> function) {
            this.function = function;
            this.deque = new ArrayDeque<>();
        }

        boolean integrate(T element, Gatherer.Downstream<? super T> downstream) {
            return this.process(element, downstream);
        }

        private boolean process(T element, Gatherer.Downstream<? super T> downstream) {
            boolean shouldContinue = downstream.push(element);
            if (shouldContinue) {
                List<T> children = this.function.apply(element).toList();
                this.deque.addAll(children);
            }
            return shouldContinue;
        }

        void finish(Gatherer.Downstream<? super T> downstream) {
            while (!deque.isEmpty() && !downstream.isRejecting()) {
                this.process(this.deque.removeFirst(), downstream);
            }
        }
    }

    /**
     * BFS (Breadth-First Search): Flattens a hierarchy using Breadth-First Search (level-order).
     * */
    @Test
    public void flattenEmployeeHierarchicalDataStructureWithBFSTest() {
        Stream.of(getAllEmployees())
                .gather(flattenBfs(Employee::directReports)) // function should return a Stream of Child element for a given element.
                .map(Employee::title)
                .forEach(employee -> log.info("employee :{}", employee));
    }

    private static Employee getAllEmployees() {
        // Build hierarchy
        Employee accountant = new Employee("Accountant", List.of());
        Employee financeLead = new Employee("Finance Lead", List.of(accountant));
        Employee cfo = new Employee("CFO", List.of(financeLead));

        Employee srDeveloper = new Employee("Senior Developer", List.of());
        Employee jrDeveloper = new Employee("Junior Developer", List.of());
        Employee devManager = new Employee("Dev Manager", List.of(srDeveloper, jrDeveloper));

        Employee qaAnalyst = new Employee("QA analyst", List.of());
        Employee qaManager = new Employee("QA Manager", List.of(qaAnalyst));
        Employee cto = new Employee("CTO", List.of(devManager, qaManager));

        // root element
        return new Employee("CEO", List.of(cto, cfo));
    }

    private static <T> Gatherer<T, Queue<T>, T> flattenBfs(Function<? super T, ? extends List<? extends T>> children) {
        return Gatherer.ofSequential(
                ArrayDeque::new,      // sequential; no combiner => no parallelization.
                // integrator: emit nodes level-by-level
                Gatherer.Integrator.ofGreedy((queue, root, downstream) -> {  // greedy consumption.
                    queue.add(root);
                    while (!queue.isEmpty() && !downstream.isRejecting()) {
                        var current = queue.remove();
                        downstream.push(current);
                        children.apply(current).forEach(queue::add);
                    }
                    return true;
                }),
                // finisher: nothing buffered to flush
                (queue, downstream) -> {

                }
        );
    }

    /**
     * DFS (Depth-First Search): Flattens a hierarchy using Depth-First Search (pre-order)
     * */
    @Test
    public void flattenEmployeeHierarchicalDataStructureWithDFSTest() {
        Stream.of(getAllEmployees())
                .gather(flattenDfs(Employee::directReports)) // function should return a Stream of Child element for a given element.
                .map(Employee::title)
                .forEach(employee -> log.info("employee :{}", employee));
    }

    private static <T> Gatherer<T, Deque<T>, T> flattenDfs(Function<? super T, ? extends List<? extends T>> children) {
        return Gatherer.ofSequential(
                ArrayDeque::new,
                // integrator: walk the whole subtree and emit nodes downstream
                Gatherer.Integrator.ofGreedy((stack, root, downstream) -> {  // using ofGreedy for non-short-circuiting consumption
                    stack.addFirst(root);
                    while (!stack.isEmpty() && !downstream.isRejecting()) {  // downstream may stop accepting (e.g. limit())
                        var current = stack.removeFirst();
                        downstream.push(current);                             // emit to next pipeline stage.
                        List<? extends T> kids = children.apply(current);
                        // Reverse-push so the first child is visited first (pre-order)
                        for (int i = kids.size() - 1; i >= 0; i--) {
                            stack.addFirst(kids.get(i));
                        }
                    }
                    return true;
                }),
                // finisher: nothing buffered to flush; integrator already emitted everything
                (stack, downstream) -> {

                }
        );
    }
}
