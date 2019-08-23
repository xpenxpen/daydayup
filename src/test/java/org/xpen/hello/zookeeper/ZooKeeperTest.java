package org.xpen.hello.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 演示用Embedded ZooKeeper做单元测试
 *
 */
public class ZooKeeperTest {
    private CuratorFramework zkClient;
    private TestingServer server;

    @Before
    public void setup() throws Exception {
        server = new TestingServer();
        zkClient = CuratorFrameworkFactory.newClient(server.getConnectString(),
                new ExponentialBackoffRetry(1000, 3));
        zkClient.start();
    }
    
    @After
    public void tearDown() throws Exception {
      server.stop();
    }
    
    @Test
    public void testSimpleCreate() throws Exception {
        byte[] testBytes1 = new byte[]{0, 1, 2};
        zkClient.create().forPath("/head", testBytes1);
        byte[] bytes = zkClient.getData().forPath("/head");
        Assert.assertArrayEquals(testBytes1, bytes);
        
    }
}
