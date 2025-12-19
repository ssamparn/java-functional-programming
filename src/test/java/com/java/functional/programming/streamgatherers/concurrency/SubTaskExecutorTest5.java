package com.java.functional.programming.streamgatherers.concurrency;

import lombok.extern.slf4j.Slf4j;

/**
 * In aggregated concurrency example, the solution even though is a generic solution, it uses a BiFunction<R1, R2, R> hard-codes the pattern to exactly two per-element tasks.
 * If you need 3 or 3+ independent computations per element, you either duplicate code (tri/quad variants) or shoehorn extra work into the two functions, which defeats the clarity and composability of the API.
 * So we need a really generic solution which can handle multiple concurrent subtasks in an aggregated concurrent context.
 * */
@Slf4j
public class SubTaskExecutorTest5 {


}
