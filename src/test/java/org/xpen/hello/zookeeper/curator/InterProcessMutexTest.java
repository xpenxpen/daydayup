package org.xpen.hello.zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * 演示分布式锁
 * 用zkui观察节点，
 * 可以发现多个程序会按序列号创建节点，排队等待锁
 * 本程序用多线程目的是方便查看获取锁的控制台打印,非实际使用案例
 * 实际使用是多进程
 *
 */
public class InterProcessMutexTest {
	
    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework zkClient = CuratorFrameworkFactory.newClient(
        		"localhost:2182", 5000, 3000, retryPolicy);
        zkClient.start();
        System.out.println("已经启动Curator客户端");
        
        for (int i = 0; i < 6; i++) {
        	new Thread(new InterProcessMutexLock(i, zkClient)).start();
        }
        
    }
    
    public static class InterProcessMutexLock implements Runnable {
    	
    	private CuratorFramework zkClient;
    	private int id;

		public InterProcessMutexLock(int id, CuratorFramework zkClient) {
			this.zkClient = zkClient;
			this.id = id;
		}

		@Override
		public void run() {
	        //获取分布式锁
	        InterProcessMutex lock = new InterProcessMutex(zkClient, "/locks/myLock");
	        try {
		        lock.acquire();
		        System.out.println(id + "已经获取锁");
		        Thread.sleep(20000);
	        } catch (Exception e) {
	        } finally {
	        	try {
	        		lock.release();
			        System.out.println(id + "已经释放锁");
	        	} catch (Exception e) {
	        	}
	        }
		}
    }
}
