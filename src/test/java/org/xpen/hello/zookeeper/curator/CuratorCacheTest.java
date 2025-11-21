package org.xpen.hello.zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 演示Curator缓存
 * 请手工用zkui改变节点值，观察本程序的监听效果
 * 注意:
 * 1)监听的根节点下的所有子孙节点都会被监听
 * 2)程序一开始如果原来就存在节点,会依次触发所有节点的forCreates
 * 3)本身确实是缓存,在内存中就能得到值
 *
 */
public class CuratorCacheTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CuratorCacheTest.class);
	private static final String ROOT = "/example/cache";
	
    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework zkClient = CuratorFrameworkFactory.newClient(
        		"localhost:2182", 5000, 3000, retryPolicy);
        zkClient.start();
        System.out.println("已经启动Curator客户端");
        
        CuratorCache cache = CuratorCache.build(zkClient, ROOT);
        CuratorCacheListener listener = CuratorCacheListener.builder()
            .forCreates(node -> LOGGER.info("Node created: {}", node))
            .forChanges((oldNode, node) -> LOGGER.info("Node changed. Old: {} New: {}", oldNode, node))
            .forDeletes(oldNode -> LOGGER.info("Node deleted. Old value: {}", oldNode))
            .forInitialized(() -> LOGGER.info("Cache initialized"))
            .build();
        
        cache.listenable().addListener(listener);
        cache.start();
        
        Thread.sleep(30000);
        
        byte[] data = cache.get(ROOT).get().getData();
        LOGGER.info("{}:{}", ROOT, new String(data));
        
        cache.close();
    }

}
