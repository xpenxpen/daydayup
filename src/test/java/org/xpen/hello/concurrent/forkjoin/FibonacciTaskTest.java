package org.xpen.hello.concurrent.forkjoin;

import java.util.concurrent.ForkJoinPool;

import org.junit.Assert;
import org.junit.Test;

public class FibonacciTaskTest {

    // it makes no sense to create more threads than available cores (no speed improvement here)
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    // create thread pool
    private final ForkJoinPool pool = new ForkJoinPool(AVAILABLE_PROCESSORS);

    @Test
    public void testFibonacciArray() {

        // more test data: http://www.maths.surrey.ac.uk/hosted-sites/R.Knott/Fibonacci/fibtable.html
        long results[] = { 0L, 1L, 1L, 2L, 3L, 5L, 8L, 13L, 21L, 34L, 55L, 89L, 144L, 233L, 377L, 610L, 987L, 1597L,
                2584L, 4181L, 6765L };
        for (int inputValue = 0; inputValue < results.length; inputValue++) {

            final FibonacciTask task = new FibonacciTask(inputValue);
            System.out.print("Fibonacci(" + inputValue + ") = ");

            final long result = pool.invoke(task);
            System.out.println(result);

            Assert.assertEquals(results[inputValue], result);
        }
    }
}
