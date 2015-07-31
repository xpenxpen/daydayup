package org.xpen.hello.concurrent.forkjoin;

import java.util.concurrent.RecursiveTask;

/**
 * JDK7 fork join研究
 * 出处
 * http://www.javacodegeeks.com/2012/04/fork-and-join-in-java-7-jsr-166.html
 *
 */
public class FibonacciTask extends RecursiveTask<Long> {

    private static final long serialVersionUID = 1L;

    private final long inputValue;

    public FibonacciTask(long inputValue) {
        this.inputValue = inputValue;
    }

    @Override
    public Long compute() {

        if (inputValue == 0L) {
            return 0L;
        } else if (inputValue <= 2L) {
            return 1L;
        } else {
            final FibonacciTask firstWorker = new FibonacciTask(inputValue - 1L);
            firstWorker.fork();
            
            final FibonacciTask secondWorker = new FibonacciTask(inputValue - 2L);
            return secondWorker.compute() + firstWorker.join();
        }
    }
}
