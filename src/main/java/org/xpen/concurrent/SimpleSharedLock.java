package org.xpen.concurrent;

import java.util.concurrent.locks.Condition;

import org.xpen.coderead.java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * AbstractQueuedSynchronizer类研究(共享锁)
 * AQS的主要方法是acquire和release，典型的模板方法， 下面这5个方法由子类去实现：
 * tryAcquire,tryRelease,tryAcquireShared,tryReleaseShared,isHeldExclusively
 * 出处
 * http://www.cnblogs.com/zhanjindong/p/java-concurrent-package-aqs-overview.html
 *
 */
class SimpleSharedLock extends AbstractQueuedSynchronizer {
    private static final long serialVersionUID = -7316320116933187634L;

    public SimpleSharedLock() {
    }

    @Override
    protected int tryAcquireShared(int unused) {
        return 1;
    }

    @Override
    protected boolean tryReleaseShared(int unused) {
        return true;
    }
    
    public void lock() {
        System.out.println(Thread.currentThread().getId() + " start acquire lock!");
        acquireShared(1);
    }

    public void unlock() {
        System.out.println(Thread.currentThread().getId() + " release lock!");
        releaseShared(1);
    }
    
    public Condition newCondition() {
        return new ConditionObject();
    }
    
    public static void main(String[] args) throws InterruptedException {
        final SimpleSharedLock lock = new SimpleSharedLock();
        //lock.lock();

        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    lock.lock();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    lock.unlock();
                }
            }).start();
        }
        Thread.sleep(100);

        //System.out.println("main thread unlock!");
        //lock.unlock();
    }
}
