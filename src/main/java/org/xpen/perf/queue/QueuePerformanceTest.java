package org.xpen.perf.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 阻塞队列性能对比
 * 比较了ArrayBlockingQueue，LinkedBlockingQueue，还有自己实现的2个简易队列
 * 
 * ArrayBlockingQueue cost : 5698
 * LinkedBlockingQueue cost : 6622
 * MyBlockingQueue cost : 6650
 * MyBlockingQueue2 cost : 7714
 * 比下来差不多，看来性能这东西不能轻易下结论，只能具体场景具体分析了
 * 
 * 出处
 * http://lazy2009.iteye.com/blog/1892559
 *
 */
public class QueuePerformanceTest {
	// 队列最大容量
	public static final int Q_SIZE = 10240000;
	// 生产者/消费者线程数
	public static final int THREAD_NUM_PRODUCER = 1;
	public static final int THREAD_NUM_CONSUMER = 1;

	// 产品
	class Product {
		String name;

		Product(String name) {
			this.name = name;
		}
	}

	public void test(final BlockingQueue<Product> q)
			throws InterruptedException {
		
		// 生产者线程
		class Producer implements Runnable {
			@Override
			public void run() {
				for (int i = 0; i < Q_SIZE; i++) {
					try {
						q.put(new Product("Lee"));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}
		
		// 消费者线程
		class Consumer implements Runnable {
			@Override
			public void run() {
				for (int i = 0; i < Q_SIZE; i++) {
					try {
						q.take();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		// 创建生产者
		Thread[] arrProducerThread = new Thread[THREAD_NUM_PRODUCER];
		for (int i = 0; i < THREAD_NUM_PRODUCER; i++) {
			arrProducerThread[i] = new Thread(new Producer());
		}
		// 创建消费者
		Thread[] arrConsumerThread = new Thread[THREAD_NUM_CONSUMER];
		for (int i = 0; i < THREAD_NUM_CONSUMER; i++) {
			arrConsumerThread[i] = new Thread(new Consumer());
		}
		
		// go!
		long t1 = System.currentTimeMillis();
		for (int i = 0; i < THREAD_NUM_PRODUCER; i++) {
			arrProducerThread[i].start();
		}
		for (int i = 0; i < THREAD_NUM_CONSUMER; i++) {
			arrConsumerThread[i].start();
		}
		for (int i = 0; i < THREAD_NUM_PRODUCER; i++) {
			arrProducerThread[i].join();
		}
		for (int i = 0; i < THREAD_NUM_CONSUMER; i++) {
			arrConsumerThread[i].join();
		}
		long t2 = System.currentTimeMillis();
		System.out.println(q.getClass().getSimpleName() + " cost : "
				+ (t2 - t1));
	}

	public static void main(String[] args) throws InterruptedException {
		final BlockingQueue<Product> q1 = new ArrayBlockingQueue<Product>(Q_SIZE);
		final BlockingQueue<Product> q2 = new LinkedBlockingQueue<Product>(Q_SIZE);
		final BlockingQueue<Product> q3 = new MyBlockingQueue<Product>(Q_SIZE);
		final BlockingQueue<Product> q4 = new MyBlockingQueue2<Product>(Q_SIZE);
		new QueuePerformanceTest().test(q1);
		new QueuePerformanceTest().test(q2);
		new QueuePerformanceTest().test(q3);
		new QueuePerformanceTest().test(q4);
	}
}
