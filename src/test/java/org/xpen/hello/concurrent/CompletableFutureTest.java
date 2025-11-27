package org.xpen.hello.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

public class CompletableFutureTest {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(CompletableFutureTest.class);
    
	private ExecutorService executorService = Executors.newFixedThreadPool(3, new CustomizableThreadFactory("my-executor-"));
	
    @Test
    public void testAllOf() {
    	CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
    	    simulateTask("任务①");
    	    return 2;
    	}, executorService);

    	CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
    	    simulateTask("任务②");
    	    return 3;
    	}, executorService);

    	CompletableFuture<Integer> future3 = CompletableFuture.supplyAsync(() -> {
    	    simulateTask("任务③");
    	    return 4;
    	}, executorService);
    	
    	List<CompletableFuture<Integer>> futures = new ArrayList<>();
    	futures.add(future1);
    	futures.add(future2);
    	futures.add(future3);

    	CompletableFuture<Integer> allFutures = CompletableFuture.allOf(future1, future2, future3)
    		.thenApply(t -> 
    			futures.stream()
    				.map(CompletableFuture::join)
    				.mapToInt(s -> s)
    				.sum()
    	);

    	allFutures
    		.thenAccept(this::runSuccessNotify)
        	.exceptionally(this::runFailureNotify);
    	
    	allFutures.join();
    }
    
    private void simulateTask(String message) {
        try {
            Thread.sleep((long)(Math.random() * 2000));
            LOGGER.info(message + "20%");
            Thread.sleep((long)(Math.random() * 2000));
            LOGGER.info(message + "50%");
            Thread.sleep((long)(Math.random() * 2000));
            LOGGER.info(message + "80%");
            Thread.sleep((long)(Math.random() * 2000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

	private void runSuccessNotify(Integer sum) {
		LOGGER.info("all success done, sum={}", sum);
	}

	private Void runFailureNotify(Throwable throwable) {
		LOGGER.error("Error occurred.", throwable);
		return null;
	}

}
