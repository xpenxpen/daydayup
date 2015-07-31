package org.xpen.concurrent;

import java.util.concurrent.locks.Condition;

import org.xpen.coderead.java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * AbstractQueuedSynchronizer类研究(独占锁)
 * AQS的主要方法是acquire和release，典型的模板方法， 下面这5个方法由子类去实现：
 * tryAcquire,tryRelease,tryAcquireShared,tryReleaseShared,isHeldExclusively
 * 出处
 * http://www.cnblogs.com/zhanjindong/p/java-concurrent-package-aqs-overview.html
 *
 */
class SimpleExclusiveLock extends AbstractQueuedSynchronizer {
    private static final long serialVersionUID = -7316320116933187634L;

    public SimpleExclusiveLock() {
    }

    @Override
    protected boolean tryAcquire(int unused) {
        if (compareAndSetState(0, 1)) {
            setExclusiveOwnerThread(Thread.currentThread());
            return true;
        }
        return false;
    }

    @Override
    protected boolean tryRelease(int unused) {
        setExclusiveOwnerThread(null);
        setState(0);
        return true;
    }
    
    @Override
    protected boolean isHeldExclusively() {
        return getState() == 1;
    }
    
    public void lock() {
        System.out.println(Thread.currentThread().getId() + " start acquire lock!");
        acquire(1);
    }

    public boolean tryLock() {
        return tryAcquire(1);
    }

    public void unlock() {
        System.out.println(Thread.currentThread().getId() + " release lock!");
        release(1);
    }

    public boolean isLocked() {
        return isHeldExclusively();
    }
    
    public Condition newCondition() {
        return new ConditionObject();
    }
    
    public static void main(String[] args) throws InterruptedException {
        final SimpleExclusiveLock lock = new SimpleExclusiveLock();
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
