package org.xpen.hello.concurrent.forkjoin;

import java.util.concurrent.ForkJoinPool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class  FibonacciTaskTracesTest {

    // it makes no sense to create more threads than available cores (no speed improvement here)
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    // create thread pool
    private final ForkJoinPool pool = new ForkJoinPool(AVAILABLE_PROCESSORS);

    @Test
    public void testFibonacciArrayTraces() {

        // more test data: http://www.maths.surrey.ac.uk/hosted-sites/R.Knott/Fibonacci/fibtable.html
        long results[] = { 0L, 1L, 1L, 2L, 3L, 5L, 8L, 13L };
        for (int inputValue = 0; inputValue < results.length; inputValue++) {

            final FibonacciTaskTraces task = new FibonacciTaskTraces(inputValue, " | ");
            System.out.println("invoke Fibonacci(" + inputValue + ")  <- " + Thread.currentThread().getName());

            final long result = pool.invoke(task);
            System.out.println("result = " + result + "\n");

            Assertions.assertEquals(results[inputValue], result);
        }
    }
}
