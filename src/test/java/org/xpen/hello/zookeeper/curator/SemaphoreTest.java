package org.xpen.hello.zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreV2;
import org.apache.curator.framework.recipes.locks.Lease;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * 演示分布式信号量
 * 同时只能有3个程序干活
 * 用zkui观察节点，
 * 可以发现多个程序会按序列号创建节点，排队等待锁
 * 本程序用多线程目的是方便查看获取锁的控制台打印,非实际使用案例
 * 实际使用是多进程
 *
 */
public class SemaphoreTest {
	
    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework zkClient = CuratorFrameworkFactory.newClient(
        		"localhost:2182", 5000, 3000, retryPolicy);
        zkClient.start();
        System.out.println("已经启动Curator客户端");
        
        for (int i = 0; i < 8; i++) {
        	new Thread(new SemaphoreLock(i, zkClient)).start();
        }
        
    }
    
    public static class SemaphoreLock implements Runnable {
    	
    	private CuratorFramework zkClient;
    	private int id;

		public SemaphoreLock(int id, CuratorFramework zkClient) {
			this.zkClient = zkClient;
			this.id = id;
		}

		@Override
		public void run() {
	        //获取信号量锁
			InterProcessSemaphoreV2 semaphore = new InterProcessSemaphoreV2(zkClient, "/locks/mySemaphore", 3);
			semaphore.setNodeData(("my_node_" + String.valueOf(id)).getBytes());
			Lease lease = null;
			try {
	        	lease = semaphore.acquire();
		        System.out.println(id + "已经获取锁");
		        Thread.sleep(20000);
	        } catch (Exception e) {
	        } finally {
	        	try {
	        		if (lease != null) {
		        		semaphore.returnLease(lease);
				        System.out.println(id + "已经释放锁");
	        		}
	        	} catch (Exception e) {
	        	}
	        }
		}
    }
}
