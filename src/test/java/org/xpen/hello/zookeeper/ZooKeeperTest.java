package org.xpen.hello.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * 演示用Embedded ZooKeeper做单元测试
 *
 */
public class ZooKeeperTest {
    private CuratorFramework zkClient;
    private TestingServer server;

    @BeforeEach
    public void setup() throws Exception {
        server = new TestingServer();
        zkClient = CuratorFrameworkFactory.newClient(server.getConnectString(),
                new ExponentialBackoffRetry(1000, 3));
        zkClient.start();
    }
    
    @AfterEach
    public void tearDown() throws Exception {
      server.stop();
    }
    
    @Test
    public void testSimpleCreate() throws Exception {
        byte[] testBytes1 = new byte[]{0, 1, 2};
        zkClient.create().forPath("/head", testBytes1);
        byte[] bytes = zkClient.getData().forPath("/head");
        Assertions.assertArrayEquals(testBytes1, bytes);
        
    }
}
