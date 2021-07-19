package org.xpen.coderead.java.util.concurrent.locks;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractOwnableSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReadWriteLock;

import sun.misc.Unsafe;

/**
 * Provides a framework for implementing blocking locks and related
 * synchronizers (semaphores, events, etc) that rely on first-in-first-out
 * (FIFO) wait queues. This class is designed to be a useful basis for most
 * kinds of synchronizers that rely on a single atomic <tt>int</tt> value to
 * represent state. Subclasses must define the protected methods that change
 * this state, and which define what that state means in terms of this object
 * being acquired or released. Given these, the other methods in this class
 * carry out all queuing and blocking mechanics. Subclasses can maintain other
 * state fields, but only the atomically updated <tt>int</tt> value manipulated
 * using methods {@link #getState}, {@link #setState} and
 * {@link #compareAndSetState} is tracked with respect to synchronization.
 * 
 * <p>
 * Subclasses should be defined as non-public internal helper classes that are
 * used to implement the synchronization properties of their enclosing class.
 * Class <tt>AbstractQueuedSynchronizer</tt> does not implement any
 * synchronization interface. Instead it defines methods such as
 * {@link #acquireInterruptibly} that can be invoked as appropriate by concrete
 * locks and related synchronizers to implement their public methods.
 * 
 * <p>
 * This class supports either or both a default <em>exclusive</em> mode and a
 * <em>shared</em> mode. When acquired in exclusive mode, attempted acquires by
 * other threads cannot succeed. Shared mode acquires by multiple threads may
 * (but need not) succeed. This class does not &quot;understand&quot; these
 * differences except in the mechanical sense that when a shared mode acquire
 * succeeds, the next waiting thread (if one exists) must also determine whether
 * it can acquire as well. Threads waiting in the different modes share the same
 * FIFO queue. Usually, implementation subclasses support only one of these
 * modes, but both can come into play for example in a {@link ReadWriteLock}.
 * Subclasses that support only exclusive or only shared modes need not define
 * the methods supporting the unused mode.
 * 
 * <p>
 * This class defines a nested {@link ConditionObject} class that can be used as
 * a {@link Condition} implementation by subclasses supporting exclusive mode
 * for which method {@link #isHeldExclusively} reports whether synchronization
 * is exclusively held with respect to the current thread, method
 * {@link #release} invoked with the current {@link #getState} value fully
 * releases this object, and {@link #acquire}, given this saved state value,
 * eventually restores this object to its previous acquired state. No
 * <tt>AbstractQueuedSynchronizer</tt> method otherwise creates such a
 * condition, so if this constraint cannot be met, do not use it. The behavior
 * of {@link ConditionObject} depends of course on the semantics of its
 * synchronizer implementation.
 * 
 * <p>
 * This class provides inspection, instrumentation, and monitoring methods for
 * the internal queue, as well as similar methods for condition objects. These
 * can be exported as desired into classes using an
 * <tt>AbstractQueuedSynchronizer</tt> for their synchronization mechanics.
 * 
 * <p>
 * Serialization of this class stores only the underlying atomic integer
 * maintaining state, so deserialized objects have empty thread queues. Typical
 * subclasses requiring serializability will define a <tt>readObject</tt> method
 * that restores this to a known initial state upon deserialization.
 * 
 * <h3>Usage</h3>
 * 
 * <p>
 * To use this class as the basis of a synchronizer, redefine the following
 * methods, as applicable, by inspecting and/or modifying the synchronization
 * state using {@link #getState}, {@link #setState} and/or
 * {@link #compareAndSetState}:
 * 
 * <ul>
 * <li> {@link #tryAcquire}
 * <li> {@link #tryRelease}
 * <li> {@link #tryAcquireShared}
 * <li> {@link #tryReleaseShared}
 * <li> {@link #isHeldExclusively}
 * </ul>
 * 
 * Each of these methods by default throws {@link UnsupportedOperationException}
 * . Implementations of these methods must be internally thread-safe, and should
 * in general be short and not block. Defining these methods is the
 * <em>only</em> supported means of using this class. All other methods are
 * declared <tt>final</tt> because they cannot be independently varied.
 * 
 * <p>
 * You may also find the inherited methods from
 * {@link AbstractOwnableSynchronizer} useful to keep track of the thread owning
 * an exclusive synchronizer. You are encouraged to use them -- this enables
 * monitoring and diagnostic tools to assist users in determining which threads
 * hold locks.
 * 
 * <p>
 * Even though this class is based on an internal FIFO queue, it does not
 * automatically enforce FIFO acquisition policies. The core of exclusive
 * synchronization takes the form:
 * 
 * <pre>
 * Acquire:
 *     while (!tryAcquire(arg)) {
 *        <em>enqueue thread if it is not already queued</em>;
 *        <em>possibly block current thread</em>;
 *     }
 * 
 * Release:
 *     if (tryRelease(arg))
 *        <em>unblock the first queued thread</em>;
 * </pre>
 * 
 * (Shared mode is similar but may involve cascading signals.)
 * 
 * <p>
 * <a name="barging">Because checks in acquire are invoked before enqueuing, a
 * newly acquiring thread may <em>barge</em> ahead of others that are blocked
 * and queued. However, you can, if desired, define <tt>tryAcquire</tt> and/or
 * <tt>tryAcquireShared</tt> to disable barging by internally invoking one or
 * more of the inspection methods, thereby providing a <em>fair</em> FIFO
 * acquisition order. In particular, most fair synchronizers can define
 * <tt>tryAcquire</tt> to return <tt>false</tt> if
 * {@link #hasQueuedPredecessors} (a method specifically designed to be used by
 * fair synchronizers) returns <tt>true</tt>. Other variations are possible.
 * 
 * <p>
 * Throughput and scalability are generally highest for the default barging
 * (also known as <em>greedy</em>, <em>renouncement</em>, and
 * <em>convoy-avoidance</em>) strategy. While this is not guaranteed to be fair
 * or starvation-free, earlier queued threads are allowed to recontend before
 * later queued threads, and each recontention has an unbiased chance to succeed
 * against incoming threads. Also, while acquires do not &quot;spin&quot; in the
 * usual sense, they may perform multiple invocations of <tt>tryAcquire</tt>
 * interspersed with other computations before blocking. This gives most of the
 * benefits of spins when exclusive synchronization is only briefly held,
 * without most of the liabilities when it isn't. If so desired, you can augment
 * this by preceding calls to acquire methods with "fast-path" checks, possibly
 * prechecking {@link #hasContended} and/or {@link #hasQueuedThreads} to only do
 * so if the synchronizer is likely not to be contended.
 * 
 * <p>
 * This class provides an efficient and scalable basis for synchronization in
 * part by specializing its range of use to synchronizers that can rely on
 * <tt>int</tt> state, acquire, and release parameters, and an internal FIFO
 * wait queue. When this does not suffice, you can build synchronizers from a
 * lower level using {@link vjava.util.concurrent.atomic atomic} classes, your
 * own custom {@link java.util.Queue} classes, and {@link LockSupport} blocking
 * support.
 * 
 * <h3>Usage Examples</h3>
 * 
 * <p>
 * Here is a non-reentrant mutual exclusion lock class that uses the value zero
 * to represent the unlocked state, and one to represent the locked state. While
 * a non-reentrant lock does not strictly require recording of the current owner
 * thread, this class does so anyway to make usage easier to monitor. It also
 * supports conditions and exposes one of the instrumentation methods:
 * 
 * <pre>
 * class Mutex implements Lock, java.io.Serializable {
 * 
 *  // Our internal helper class
 *  private static class Sync extends AbstractQueuedSynchronizer {
 *      // Report whether in locked state
 *      protected boolean isHeldExclusively() {
 *          return getState() == 1;
 *      }
 * 
 *      // Acquire the lock if state is zero
 *      public boolean tryAcquire(int acquires) {
 *          assert acquires == 1; // Otherwise unused
 *          if (compareAndSetState(0, 1)) {
 *              setExclusiveOwnerThread(Thread.currentThread());
 *              return true;
 *          }
 *          return false;
 *      }
 * 
 *      // Release the lock by setting state to zero
 *      protected boolean tryRelease(int releases) {
 *          assert releases == 1; // Otherwise unused
 *          if (getState() == 0)
 *              throw new IllegalMonitorStateException();
 *          setExclusiveOwnerThread(null);
 *          setState(0);
 *          return true;
 *      }
 * 
 *      // Provide a Condition
 *      Condition newCondition() {
 *          return new ConditionObject();
 *      }
 * 
 *      // Deserialize properly
 *      private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
 *          s.defaultReadObject();
 *          setState(0); // reset to unlocked state
 *      }
 *  }
 * 
 *  // The sync object does all the hard work. We just forward to it.
 *  private final Sync sync = new Sync();
 * 
 *  public void lock() {
 *      sync.acquire(1);
 *  }
 * 
 *  public boolean tryLock() {
 *      return sync.tryAcquire(1);
 *  }
 * 
 *  public void unlock() {
 *      sync.release(1);
 *  }
 * 
 *  public Condition newCondition() {
 *      return sync.newCondition();
 *  }
 * 
 *  public boolean isLocked() {
 *      return sync.isHeldExclusively();
 *  }
 * 
 *  public boolean hasQueuedThreads() {
 *      return sync.hasQueuedThreads();
 *  }
 * 
 *  public void lockInterruptibly() throws InterruptedException {
 *      sync.acquireInterruptibly(1);
 *  }
 * 
 *  public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
 *      return sync.tryAcquireNanos(1, unit.toNanos(timeout));
 *  }
 * }
 * </pre>
 * 
 * <p>
 * Here is a latch class that is like a {@link CountDownLatch} except that it
 * only requires a single <tt>signal</tt> to fire. Because a latch is
 * non-exclusive, it uses the <tt>shared</tt> acquire and release methods.
 * 
 * <pre>
 * class BooleanLatch {
 * 
 *  private static class Sync extends AbstractQueuedSynchronizer {
 *      boolean isSignalled() {
 *          return getState() != 0;
 *      }
 * 
 *      protected int tryAcquireShared(int ignore) {
 *          return isSignalled() ? 1 : -1;
 *      }
 * 
 *      protected boolean tryReleaseShared(int ignore) {
 *          setState(1);
 *          return true;
 *      }
 *  }
 * 
 *  private final Sync sync = new Sync();
 * 
 *  public boolean isSignalled() {
 *      return sync.isSignalled();
 *  }
 * 
 *  public void signal() {
 *      sync.releaseShared(1);
 *  }
 * 
 *  public void await() throws InterruptedException {
 *      sync.acquireSharedInterruptibly(1);
 *  }
 * }
 * </pre>
 * 
 * @since 1.5
 * @author Doug Lea
 */
public abstract class AbstractQueuedSynchronizer extends AbstractOwnableSynchronizer implements java.io.Serializable {

    private static final long serialVersionUID = 7373984972572414691L;

    /**
     * Creates a new <tt>AbstractQueuedSynchronizer</tt> instance with initial
     * synchronization state of zero.
     */
    protected AbstractQueuedSynchronizer() {
    }

    /**
     * Wait queue node class.
     * 等待队列节点
     * 
     * <p>
     * The wait queue is a variant of a "CLH" (Craig, Landin, and Hagersten)
     * lock queue. CLH locks are normally used for spinlocks. We instead use
     * them for blocking synchronizers, but use the same basic tactic of holding
     * some of the control information about a thread in the predecessor of its
     * node. A "status" field in each node keeps track of whether a thread
     * should block. A node is signalled when its predecessor releases. Each
     * node of the queue otherwise serves as a specific-notification-style
     * monitor holding a single waiting thread. The status field does NOT
     * control whether threads are granted locks etc though. A thread may try to
     * acquire if it is first in the queue. But being first does not guarantee
     * success; it only gives the right to contend. So the currently released
     * contender thread may need to rewait.
     * CLH的一个变种（自旋锁）.
     * CLH lock queue其实就是一个FIFO的队列，队列中的每个结点（线程）只要等待其前继释放锁就可以了。
     * 所谓自旋锁简单来说就是线程通过循环来等待而不是睡眠。
     * 
     * <p>
     * To enqueue into a CLH lock, you atomically splice it in as new tail. To
     * dequeue, you just set the head field.
     * 
     * <pre>
     *      +------+  prev +-----+       +-----+
     * head |      | <---- |     | <---- |     |  tail
     *      +------+       +-----+       +-----+
     * </pre>
     * 
     * <p>
     * Insertion into a CLH queue requires only a single atomic operation on
     * "tail", so there is a simple atomic point of demarcation from unqueued to
     * queued. Similarly, dequeing involves only updating the "head". However,
     * it takes a bit more work for nodes to determine who their successors are,
     * in part to deal with possible cancellation due to timeouts and
     * interrupts.
     * 
     * <p>
     * The "prev" links (not used in original CLH locks), are mainly needed to
     * handle cancellation. If a node is cancelled, its successor is (normally)
     * relinked to a non-cancelled predecessor. For explanation of similar
     * mechanics in the case of spin locks, see the papers by Scott and Scherer
     * at http://www.cs.rochester.edu/u/scott/synchronization/
     * 
     * <p>
     * We also use "next" links to implement blocking mechanics. The thread id
     * for each node is kept in its own node, so a predecessor signals the next
     * node to wake up by traversing next link to determine which thread it is.
     * Determination of successor must avoid races with newly queued nodes to
     * set the "next" fields of their predecessors. This is solved when
     * necessary by checking backwards from the atomically updated "tail" when a
     * node's successor appears to be null. (Or, said differently, the
     * next-links are an optimization so that we don't usually need a backward
     * scan.)
     * 
     * <p>
     * Cancellation introduces some conservatism to the basic algorithms. Since
     * we must poll for cancellation of other nodes, we can miss noticing
     * whether a cancelled node is ahead or behind us. This is dealt with by
     * always unparking successors upon cancellation, allowing them to stabilize
     * on a new predecessor, unless we can identify an uncancelled predecessor
     * who will carry this responsibility.
     * 
     * <p>
     * CLH queues need a dummy header node to get started. But we don't create
     * them on construction, because it would be wasted effort if there is never
     * contention. Instead, the node is constructed and head and tail pointers
     * are set upon first contention.
     * 
     * <p>
     * Threads waiting on Conditions use the same nodes, but use an additional
     * link. Conditions only need to link nodes in simple (non-concurrent)
     * linked queues because they are only accessed when exclusively held. Upon
     * await, a node is inserted into a condition queue. Upon signal, the node
     * is transferred to the main queue. A special value of status field is used
     * to mark which queue a node is on.
     * 
     * <p>
     * Thanks go to Dave Dice, Mark Moir, Victor Luchangco, Bill Scherer and
     * Michael Scott, along with members of JSR-166 expert group, for helpful
     * ideas, discussions, and critiques on the design of this class.
     */
    static final class Node {
        /** 节点正在共享模式下等待的标记 */
        static final Node SHARED = new Node();
        /** 节点正在以独占模式等待的标记 */
        static final Node EXCLUSIVE = null;

        /** 代表线程(因为超时或者或者被中断)已经被取消*/
        static final int CANCELLED = 1;
        /** 代表后继节点需要唤醒*/
        static final int SIGNAL = -1;
        /** 表示节点处于condition队列中，正在等待被唤醒*/
        static final int CONDITION = -2;
        /** 下一次acquireShared应该无条件传播*/
        static final int PROPAGATE = -3;

        /**
         * Status field, taking on only the values: 
         * <ul>
         * <li>SIGNAL: The successor of
         * this node is (or will soon be) blocked (via park), so the current
         * node must unpark its successor when it releases or cancels. To avoid
         * races, acquire methods must first indicate they need a signal, then
         * retry the atomic acquire, and then, on failure, block. </li>
         * <li>CANCELLED:
         * This node is cancelled due to timeout or interrupt. Nodes never leave
         * this state. In particular, a thread with cancelled node never again
         * blocks. </li>
         * <li>CONDITION: This node is currently on a condition queue. It
         * will not be used as a sync queue node until transferred, at which
         * time the status will be set to 0. (Use of this value here has nothing
         * to do with the other uses of the field, but simplifies mechanics.)</li>
         * <li>PROPAGATE: A releaseShared should be propagated to other nodes. This
         * is set (for head node only) in doReleaseShared to ensure propagation
         * continues, even if other operations have since intervened. 0: None of
         * the above</li>
         * </ul>
         * 
         * The values are arranged numerically to simplify use. Non-negative
         * values mean that a node doesn't need to signal. So, most code doesn't
         * need to check for particular values, just for sign.
         * 
         * The field is initialized to 0 for normal sync nodes, and CONDITION
         * for condition nodes. It is modified using CAS (or when possible,
         * unconditional volatile writes).
         * <p>
         * 结点的等待状态。
         * <p>
         * 用于控制线程的阻塞/唤醒，以及避免不必要的调用LockSupport的park/unpark方法。
         * CANCELLED=1、初始化=0、SIGNAL=-1、CONDITION=-2或PROPAGATE=-3。
         * <ul>
         * <li>SIGNAL：后继的结点处于blocked或即将处于blocked状态，需要当前结点release或cancel的时候唤醒它。</li>
         * <li>CANCELLED：当前的线程被取消</li>
         * <li>CONDITION: 当前节点在等待condition，也就是在condition队列</li>
         * <li>PROPAGATE: 当前场景下后续的acquireShared能够得以执行</li>
         * </ul>
         */
        volatile int waitStatus;

        /**
         * Link to predecessor node that current node/thread relies on for
         * checking waitStatus. Assigned during enqueing, and nulled out (for
         * sake of GC) only upon dequeuing. Also, upon cancellation of a
         * predecessor, we short-circuit while finding a non-cancelled one,
         * which will always exist because the head node is never cancelled: A
         * node becomes head only as a result of successful acquire. A cancelled
         * thread never succeeds in acquiring, and a thread only cancels itself,
         * not any other node.
         */
        volatile Node prev;

        /**
         * Link to the successor node that the current node/thread unparks upon
         * release. Assigned during enqueuing, adjusted when bypassing cancelled
         * predecessors, and nulled out (for sake of GC) when dequeued. The enq
         * operation does not assign next field of a predecessor until after
         * attachment, so seeing a null next field does not necessarily mean
         * that node is at end of queue. However, if a next field appears to be
         * null, we can scan prev's from the tail to double-check. The next
         * field of cancelled nodes is set to point to the node itself instead
         * of null, to make life easier for isOnSyncQueue.
         */
        volatile Node next;

        /**
         * The thread that enqueued this node. Initialized on construction and
         * nulled out after use.
         */
        volatile Thread thread;

        /**
         * Link to next node waiting on condition, or the special value SHARED.
         * Because condition queues are accessed only when holding in exclusive
         * mode, we just need a simple linked queue to hold nodes while they are
         * waiting on conditions. They are then transferred to the queue to
         * re-acquire. And because conditions can only be exclusive, we save a
         * field by using special value to indicate shared mode.
         * 下一个condition队列等待节点
         */
        Node nextWaiter;

        /**
         * Returns true if node is waiting in shared mode
         * 是否是共享模式
         */
        final boolean isShared() {
            return nextWaiter == SHARED;
        }

        /**
         * Returns previous node, or throws NullPointerException if null. Use
         * when predecessor cannot be null. The null check could be elided, but
         * is present to help the VM.
         * 返回前驱节点或者抛出异常
         * 
         * @return the predecessor of this node
         */
        final Node predecessor() throws NullPointerException {
            Node p = prev;
            if (p == null)
                throw new NullPointerException();
            else
                return p;
        }

        Node() { // Used to establish initial head or SHARED marker
        }

        Node(Thread thread, Node mode) { // Used by addWaiter
            this.nextWaiter = mode;
            this.thread = thread;
        }

        Node(Thread thread, int waitStatus) { // Used by Condition
            this.waitStatus = waitStatus;
            this.thread = thread;
        }
    }

    /**
     * 等待队列的头节点，只能通过setHead方法修改
     * 如果head存在，能保证waitStatus状态不为CANCELLED
     */
    private transient volatile Node head;

    /**
     * 等待队列的尾结点，只能通过enq方法来添加新的等待节点
     */
    private transient volatile Node tail;

    /**
     * 锁的状态。0表示未锁定,大于0表示已锁定.
     * 这个值可以用来实现锁的[可重入性]，例如 state=3 就表示锁被同一个线程获取了3次，想要完全解锁，必须要对应的解锁3次
     * 同时这个变量还是用volatile关键字修饰的，保证可见性
     */
    private volatile int state;

    /**
     * Returns the current value of synchronization state. This operation has
     * memory semantics of a <tt>volatile</tt> read.
     * 
     * @return current state value
     */
    protected final int getState() {
        return state;
    }

    /**
     * Sets the value of synchronization state. This operation has memory
     * semantics of a <tt>volatile</tt> write.
     * 
     * @param newState
     *            the new state value
     */
    protected final void setState(int newState) {
        state = newState;
    }

    /**
     * Atomically sets synchronization state to the given updated value if the
     * current state value equals the expected value. This operation has memory
     * semantics of a <tt>volatile</tt> read and write.
     * CAS设置state
     * 
     * @param expect
     *            the expected value
     * @param update
     *            the new value
     * @return true if successful. False return indicates that the actual value
     *         was not equal to the expected value.
     */
    protected final boolean compareAndSetState(int expect, int update) {
        // See below for intrinsics setup to support this
        return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
    }

    // Queuing utilities

    /**
     * The number of nanoseconds for which it is faster to spin rather than to
     * use timed park. A rough estimate suffices to improve responsiveness with
     * very short timeouts.
     * 小于1秒的话采用自旋锁
     */
    static final long spinForTimeoutThreshold = 1000L;

    /**
     * (1) Acquires in exclusive mode, ignoring interrupts. Implemented by invoking
     * at least once {@link #tryAcquire}, returning on success. Otherwise the
     * thread is queued, possibly repeatedly blocking and unblocking, invoking
     * {@link #tryAcquire} until success. This method can be used to implement
     * method {@link Lock#lock}.
     * <p>
     * 独占获取。
     * <p>
     * 一旦tryAcquire成功则立即返回，否则线程会加入队列。
     * 线程可能会反复的被阻塞和唤醒直到tryAcquire成功，这是因为线程可能被中断，
     * 而acquireQueued方法中会保证忽视中断，只有tryAcquire成功了才返回。
     * <p>
     * 可以中断的版本{@link #acquireInterruptibly} ，中断时会抛出InterruptedException异常。
     * 
     * 
     * 
     * @param arg
     *            the acquire argument. This value is conveyed to
     *            {@link #tryAcquire} but is otherwise uninterpreted and can
     *            represent anything you like.
     */
    public final void acquire(int arg) {
        //首先调用tryAcquire方法来再一次尝试获取锁，如果成功则返回，否则执行acquireQueued方法
        // tryAcquire 由子类实现本身不会阻塞线程，如果返回 true,则线程继续，
        // 如果返回 false 那么就 加入阻塞队列阻塞线程，并等待前继结点释放锁。
        if (!tryAcquire(arg)) {
            Node newNode = addWaiter(Node.EXCLUSIVE);
            if (acquireQueued(newNode, arg)) {
                // acquireQueued返回true，说明当前线程被中断唤醒后获取到锁，
                // 重置其interrupt status为true。
                System.out.println(Thread.currentThread().getId() + " acquire(): Interrupt:" + arg);
                selfInterrupt();
            }
        }
        System.out.println(Thread.currentThread().getId() + " acquire(): success");
    }
    
    /**
     * (2)Creates and enqueues node for current thread and given mode.
     * <p>
     * 获取锁失败后，新增等待节点
     *  1. 尝试将新节点以最快的方式设置为尾节点，如果CAS设置尾节点成功，返回附加着当前线程的节点。
     *  2. 如果CAS操作失败，则调用enq方法，循环入队直到成功。
     * 
     * @param mode Node.EXCLUSIVE for exclusive, Node.SHARED for shared
     * @return the new node
     */
    private Node addWaiter(Node mode) {
        System.out.println(Thread.currentThread().getId() + " addWaiter() ");
        //使用当前线程构造出新的节点  
        Node node = new Node(Thread.currentThread(), mode);
        // Try the fast path of enq; backup to full enq on failure
        Node pred = tail;
        // 这个if分支其实是一种优化：CAS操作失败的话才进入enq中的循环。
        if (pred != null) {
            node.prev = pred;
            if (compareAndSetTail(pred, node)) {
                pred.next = node;
                return node;
            }
        }
        enq(node);
        return node;
    }

    /**
     * (3)Inserts node into queue, initializing if necessary. See picture above.
     * <p>
     * 自旋插入队尾直到CAS成功
     * 
     * @param node the node to insert
     * @return node's predecessor
     */
    private Node enq(final Node node) {
        //乐观等待(自旋)
        for (;;) {
            Node t = tail;
            if (t == null) { // Must initialize
                //当第一次产生竞争的时候初始化虚拟头结点，节省空间 
                if (compareAndSetHead(new Node()))
                    tail = head;
            } else {
                //注意到第一次第二轮会走到这里，所以第一次会创建2个节点
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    return t;
                }
            }
        }
    }
    
    /**
     * (4)Acquires in exclusive uninterruptible mode for thread already in queue.
     * Used by condition wait methods as well as acquire.
     * <p>
     * 等待前继结点释放锁，忽视线程的中断，直到tryAcquire成功。
     * 
     * 自旋获取锁，直至异常退出或获取锁成功，
     * 但是并不是busy acquire，因为当获取失败后会被挂起，由前驱节点释放锁时将其唤醒。
     * 同时由于唤醒的时候可能有其他线程竞争，所以还需要进行尝试获取锁，体现的非公平锁的精髓。
     * @param node the node
     * @param arg the acquire argument
     * @return {@code true} if interrupted while waiting
     */
    final boolean acquireQueued(final Node node, int arg) {
        boolean failed = true;
        try {
            boolean interrupted = false;
            // 等待前继结点释放锁
            // 自旋
            for (;;) {
                // 获取前继
                final Node p = node.predecessor();
                //如果前驱节点是head节点，说明next就是自己了，尝试获取锁，
                //如果获取锁成功，说明head节点已经释放锁了，将node设为head开始运行
                if (p == head && tryAcquire(arg)) {
                    // 前继出队，node成为head
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return interrupted;
                }

                // p != head 或者 p == head但是tryAcquire失败了，那么
                // 应该阻塞当前线程等待前继唤醒。阻塞之前会再重试一次，还需要设置前继的waitStaus为SIGNAL。

                // 线程会阻塞在parkAndCheckInterrupt方法中。
                // parkAndCheckInterrupt返回可能是前继unpark或线程被中断。
                //判断当前node在获取锁失败后是否可以挂起，通过pred的状态判断
                if (shouldParkAfterFailedAcquire(p, node)) {
                    //挂起线程，等待node的前驱节点唤醒。
                    if (parkAndCheckInterrupt()) {
                        // 说明当前线程是被中断唤醒的。
                        // 注意：线程被中断之后会继续走到if处去判断，也就是会忽视中断。
                        // 除非碰巧线程中断后acquire成功了，那么根据Java的最佳实践，
                        // 需要重新设置线程的中断状态（acquire.selfInterrupt）。
                        interrupted = true;
                    }
                }
            }
        } finally {
            // 自旋异常退出，取消正在进行锁争抢
            if (failed)
                cancelAcquire(node);
        }
    }

    /**
     * (5)Checks and updates status for a node that failed to acquire. Returns true
     * if thread should block. This is the main signal control in all acquire
     * loops. Requires that pred == node.prev
     * <p>
     * 1.确定后继是否需要park;<br />
     * 2.跳过被取消的结点;<br />
     * 3.设置前继的waitStatus为SIGNAL.
     * 
     * @param pred node's predecessor holding status
     * @param node the node
     * @return {@code true} if thread should block
     */
    private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
        int ws = pred.waitStatus;
        if (ws == Node.SIGNAL)// 前继结点已经准备好unpark其后继了，所以后继可以安全的park
            /*
             * This node has already set status asking a release to signal it,
             * so it can safely park.
             */
            return true;
        if (ws > 0) {// CANCELLED
            /*
             * Predecessor was cancelled. Skip over predecessors and indicate
             * retry. 跳过被取消的结点。
             */
            do {
                node.prev = pred = pred.prev;
            } while (pred.waitStatus > 0);
            pred.next = node;
        } else {// 0 或 PROPAGATE (CONDITION用在ConditonObject，这里不会是这个值)
            /**
             * waitStatus must be 0 or PROPAGATE. Indicate that we need a
             * signal, but don't park yet. Caller will need to retry to make
             * sure it cannot acquire before parking.
             * <p>
             * waitStatus 等于0（初始化）或PROPAGATE。说明线程还没有park，会先重试 确定无法acquire到再park。
             */

            // 更新pred结点waitStatus为SIGNAL
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
        }
        return false;
    }

    /**
     * (6)Convenience method to park and then check if interrupted
     * <p>
     * 该方法返回有3种情况：
     * <ul>
     * <li>线程被中断</li>
     * <li>线程被unpark</li>
     * <li>伪唤醒</li>
     * </ul>
     * 
     * @return {@code true} if interrupted
     */
    private final boolean parkAndCheckInterrupt() {
        //阻塞当前线程
        LockSupport.park(this);
        return Thread.interrupted();
    }

    /**
     * (7)处理异常退出的node
     * Cancels an ongoing attempt to acquire.
     * 
     * @param node
     *            the node
     */
    private void cancelAcquire(Node node) {
        // Ignore if node doesn't exist
        if (node == null)
            return;

        node.thread = null;

        // Skip cancelled predecessors
        Node pred = node.prev;
        while (pred.waitStatus > 0)
            node.prev = pred = pred.prev;

        // predNext is the apparent node to unsplice. CASes below will
        // fail if not, in which case, we lost race vs another cancel
        // or signal, so no further action is necessary.
        Node predNext = pred.next;

        // Can use unconditional write instead of CAS here.
        // After this atomic step, other Nodes can skip past us.
        // Before, we are free of interference from other threads.
        node.waitStatus = Node.CANCELLED;

        // If we are the tail, remove ourselves.
        if (node == tail && compareAndSetTail(node, pred)) {
            compareAndSetNext(pred, predNext, null);
        } else {
            // If successor needs signal, try to set pred's next-link
            // so it will get one. Otherwise wake it up to propagate.
            int ws;
            if (pred != head
                    && ((ws = pred.waitStatus) == Node.SIGNAL || (ws <= 0 && compareAndSetWaitStatus(pred, ws,
                            Node.SIGNAL))) && pred.thread != null) {
                //如果前驱节点是头结点，且状态标记为node是待运行的状态或者未标记且标记为后续节点待运行的状态，
                // 线程不为空，则直接跳过node节点，将后续节点指向node的后续节点。
                Node next = node.next;
                if (next != null && next.waitStatus <= 0)
                    compareAndSetNext(pred, predNext, next);
            } else {
                //否则说明为头节点，唤醒node的后续节点运行
                unparkSuccessor(node);
            }

            node.next = node; // help GC
        }
    }

    /**
     * (8)Wakes up node's successor, if one exists.
     * <p>
     * 唤醒后续的结点
     * 
     * @param node
     *            the node 其实 node == head
     */
    private void unparkSuccessor(Node node) {
        /*
         * If status is negative (i.e., possibly needing signal) try to clear in
         * anticipation of signalling. It is OK if this fails or if status is
         * changed by waiting thread.
         */
        int ws = node.waitStatus;
        if (ws < 0)
            compareAndSetWaitStatus(node, ws, 0);

        /**
         * Thread to unpark is held in successor, which is normally just the
         * next node. But if cancelled or apparently null, traverse backwards
         * from tail to find the actual non-cancelled successor.
         * <p>
         * 正常来说需要唤醒结点就是next，但如果next被取消了或者为null,
         * 则需要通过tail指针从后往前遍历找到没被取消的结点。
         */
        Node s = node.next;
        // s == null || s.waitStatus == CANCELLED
        if (s == null || s.waitStatus > 0) {
            s = null;
            for (Node t = tail; t != null && t != node; t = t.prev)
                if (t.waitStatus <= 0)
                    s = t;
        }
        if (s != null)
            // 唤醒后继
            LockSupport.unpark(s.thread);
    }

    /**
     * (9)Releases in exclusive mode. Implemented by unblocking one or more threads
     * if {@link #tryRelease} returns true. This method can be used to implement
     * method {@link Lock#unlock}.
     * <p>
     * 独占模式释放：释放成功会唤醒后续节点。
     * 
     * @param arg
     *            the release argument. This value is conveyed to
     *            {@link #tryRelease} but is otherwise uninterpreted and can
     *            represent anything you like.
     * @return the value returned from {@link #tryRelease}
     */
    public final boolean release(int arg) {
        // tryReease由子类实现，通过设置state值来达到同步的效果。
        if (tryRelease(arg)) {
            Node h = head;
            // waitStatus为0说明是初始化的空队列
            if (h != null) {
                if (h.waitStatus != 0) {
                    // 唤醒后续的结点
                    unparkSuccessor(h);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Sets head of queue to be node, thus dequeuing. Called only by acquire
     * methods. Also nulls out unused fields for sake of GC and to suppress
     * unnecessary signals and traversals.
     * <p>
     * 出队操作。
     * 
     * @param node
     *            the node
     */
    private void setHead(Node node) {
        head = node;
        node.thread = null;
        node.prev = null;
    }

    /**
     * Release action for shared mode -- signal successor and ensure
     * propagation. (Note: For exclusive mode, release just amounts to calling
     * unparkSuccessor of head if it needs signal.)
     */
    private void doReleaseShared() {
        /**
         * Ensure that a release propagates, even if there are other in-progress
         * acquires/releases. This proceeds in the usual way of trying to
         * unparkSuccessor of head if it needs signal. But if it does not,
         * status is set to PROPAGATE to ensure that upon release, propagation
         * continues. Additionally, we must loop in case a new node is added
         * while we are doing this. Also, unlike other uses of unparkSuccessor,
         * we need to know if CAS to reset status fails, if so rechecking.
         * <p>
         */
        for (;;) {
            Node h = head;
            // 队列不为空且有后继结点
            if (h != null && h != tail) {
                int ws = h.waitStatus;
                // 不管是共享还是独占只有结点状态为SIGNAL才尝试唤醒后继结点
                if (ws == Node.SIGNAL) {
                    // 将waitStatus设置为0
                    if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0))
                        continue; // loop to recheck cases
                    unparkSuccessor(h);// 唤醒后继结点
                    // 如果状态为0则更新状态为PROPAGATE，更新失败则重试
                } else if (ws == 0 && !compareAndSetWaitStatus(h, 0, Node.PROPAGATE))
                    continue; // loop on failed CAS
            }
            // 如果过程中head被修改了则重试。
            if (h == head) // loop if head changed
                break;
        }
    }

    /**
     * Sets head of queue, and checks if successor may be waiting in shared
     * mode, if so propagating if either propagate > 0 or PROPAGATE status was
     * set.
     * <p>
     * 将node设置为head。如果当前结点acquire到了之后发现还有许可可以被获取，则继续释放自己的后继，
     * 后继会将这个操作传递下去。这就是PROPAGATE状态的含义。
     * 
     * @param node
     *            the node
     * @param propagate
     *            the return value from a tryAcquireShared
     */
    private void setHeadAndPropagate(Node node, int propagate) {
        Node h = head; // Record old head for check below
        setHead(node);
        /**
         * Try to signal next queued node if: Propagation was indicated by
         * caller, or was recorded (as h.waitStatus) by a previous operation
         * (note: this uses sign-check of waitStatus because PROPAGATE status
         * may transition to SIGNAL.) and The next node is waiting in shared
         * mode, or we don't know, because it appears null
         * 
         * The conservatism in both of these checks may cause unnecessary
         * wake-ups, but only when there are multiple racing acquires/releases,
         * so most need signals now or soon anyway.
         * <p>
         * 尝试唤醒后继的结点：<br />
         * propagate > 0说明许可还有能够继续被线程acquire;<br />
         * 或者 之前的head被设置为PROPAGATE(PROPAGATE可以被转换为SIGNAL)说明需要往后传递;<br />
         * 或者为null,我们还不确定什么情况。 <br />
         * 并且 后继结点是共享模式或者为如上为null。
         * <p>
         * 上面的检查有点保守，在有多个线程竞争获取/释放的时候可能会导致不必要的唤醒。<br />
         * 
         */
        if (propagate > 0 || h == null || h.waitStatus < 0) {
            Node s = node.next;
            // 后继结是共享模式或者s == null（不知道什么情况）
            // 如果后继是独占模式，那么即使剩下的许可大于0也不会继续往后传递唤醒操作
            // 即使后面有结点是共享模式。
            if (s == null || s.isShared())
                // 唤醒后继结点
                doReleaseShared();
        }
    }

    // Utilities for various versions of acquire

    /**
     * Convenience method to interrupt current thread.
     */
    private static void selfInterrupt() {
        Thread.currentThread().interrupt();
    }

    /*
     * Various flavors of acquire, varying in exclusive/shared and control
     * modes. Each is mostly the same, but annoyingly different. Only a little
     * bit of factoring is possible due to interactions of exception mechanics
     * (including ensuring that we cancel if tryAcquire throws exception) and
     * other control, at least not without hurting performance too much.
     */


    /**
     * Acquires in exclusive interruptible mode.
     * 
     * @param arg
     *            the acquire argument
     */
    private void doAcquireInterruptibly(int arg) throws InterruptedException {
        final Node node = addWaiter(Node.EXCLUSIVE);
        boolean failed = true;
        try {
            for (;;) {
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return;
                }
                if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt())
                    throw new InterruptedException();
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }

    /**
     * Acquires in exclusive timed mode.
     * <p>
     * 代码整体上跟{@link #acquireQueued}差不多。
     * 
     * @param arg
     *            the acquire argument
     * @param nanosTimeout
     *            max wait time
     * @return {@code true} if acquired
     */
    private boolean doAcquireNanos(int arg, long nanosTimeout) throws InterruptedException {
        long lastTime = System.nanoTime();
        final Node node = addWaiter(Node.EXCLUSIVE);
        boolean failed = true;
        try {
            for (;;) {
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return true;
                }
                if (nanosTimeout <= 0)// 超时
                    return false;
                // nanosTimeout > spinForTimeoutThreshold
                // 如果超时时间很短的话，自旋效率会更高。
                if (shouldParkAfterFailedAcquire(p, node) && nanosTimeout > spinForTimeoutThreshold)
                    LockSupport.parkNanos(this, nanosTimeout);
                long now = System.nanoTime();
                nanosTimeout -= now - lastTime;
                lastTime = now;
                if (Thread.interrupted())
                    throw new InterruptedException();
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }

    /**
     * Acquires in shared uninterruptible mode.
     * 
     * @param arg
     *            the acquire argument
     */
    private void doAcquireShared(int arg) {
        // 添加队列
        final Node node = addWaiter(Node.SHARED);
        boolean failed = true;
        try {
            boolean interrupted = false;
            // 等待前继释放并传递
            for (;;) {
                final Node p = node.predecessor();
                if (p == head) {
                    int r = tryAcquireShared(arg);// 尝试获取
                    if (r >= 0) {
                        // 获取成功则前继出队，跟独占不同的是
                        // 会往后面结点传播唤醒的操作，保证剩下等待的线程能够尽快获取到剩下的许可。
                        setHeadAndPropagate(node, r);
                        p.next = null; // help GC
                        if (interrupted)
                            selfInterrupt();
                        failed = false;
                        return;
                    }
                }

                // p != head || r < 0
                if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt())
                    interrupted = true;
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }

    /**
     * Acquires in shared interruptible mode.
     * 
     * @param arg
     *            the acquire argument
     */
    private void doAcquireSharedInterruptibly(int arg) throws InterruptedException {
        final Node node = addWaiter(Node.SHARED);
        boolean failed = true;
        try {
            for (;;) {
                final Node p = node.predecessor();
                if (p == head) {
                    int r = tryAcquireShared(arg);
                    if (r >= 0) {
                        setHeadAndPropagate(node, r);
                        p.next = null; // help GC
                        failed = false;
                        return;
                    }
                }
                if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt())
                    throw new InterruptedException();
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }

    /**
     * Acquires in shared timed mode.
     * 
     * @param arg
     *            the acquire argument
     * @param nanosTimeout
     *            max wait time
     * @return {@code true} if acquired
     */
    private boolean doAcquireSharedNanos(int arg, long nanosTimeout) throws InterruptedException {

        long lastTime = System.nanoTime();
        final Node node = addWaiter(Node.SHARED);
        boolean failed = true;
        try {
            for (;;) {
                final Node p = node.predecessor();
                if (p == head) {
                    int r = tryAcquireShared(arg);
                    if (r >= 0) {
                        setHeadAndPropagate(node, r);
                        p.next = null; // help GC
                        failed = false;
                        return true;
                    }
                }
                if (nanosTimeout <= 0)
                    return false;
                if (shouldParkAfterFailedAcquire(p, node) && nanosTimeout > spinForTimeoutThreshold)
                    LockSupport.parkNanos(this, nanosTimeout);
                long now = System.nanoTime();
                nanosTimeout -= now - lastTime;
                lastTime = now;
                if (Thread.interrupted())
                    throw new InterruptedException();
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }

    // Main exported methods

    /**
     * Attempts to acquire in exclusive mode. This method should query if the
     * state of the object permits it to be acquired in the exclusive mode, and
     * if so to acquire it.
     * 
     * <p>
     * This method is always invoked by the thread performing acquire. If this
     * method reports failure, the acquire method may queue the thread, if it is
     * not already queued, until it is signalled by a release from some other
     * thread. This can be used to implement method {@link Lock#tryLock()}.
     * 
     * <p>
     * The default implementation throws {@link UnsupportedOperationException}.
     * 
     * @param arg
     *            the acquire argument. This value is always the one passed to
     *            an acquire method, or is the value saved on entry to a
     *            condition wait. The value is otherwise uninterpreted and can
     *            represent anything you like.
     * @return {@code true} if successful. Upon success, this object has been
     *         acquired.
     * @throws IllegalMonitorStateException
     *             if acquiring would place this synchronizer in an illegal
     *             state. This exception must be thrown in a consistent fashion
     *             for synchronization to work correctly.
     * @throws UnsupportedOperationException
     *             if exclusive mode is not supported
     */
    protected boolean tryAcquire(int arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * Attempts to set the state to reflect a release in exclusive mode.
     * 
     * <p>
     * This method is always invoked by the thread performing release.
     * 
     * <p>
     * The default implementation throws {@link UnsupportedOperationException}.
     * 
     * @param arg
     *            the release argument. This value is always the one passed to a
     *            release method, or the current state value upon entry to a
     *            condition wait. The value is otherwise uninterpreted and can
     *            represent anything you like.
     * @return {@code true} if this object is now in a fully released state, so
     *         that any waiting threads may attempt to acquire; and
     *         {@code false} otherwise.
     * @throws IllegalMonitorStateException
     *             if releasing would place this synchronizer in an illegal
     *             state. This exception must be thrown in a consistent fashion
     *             for synchronization to work correctly.
     * @throws UnsupportedOperationException
     *             if exclusive mode is not supported
     */
    protected boolean tryRelease(int arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * Attempts to acquire in shared mode. This method should query if the state
     * of the object permits it to be acquired in the shared mode, and if so to
     * acquire it.
     * 
     * <p>
     * This method is always invoked by the thread performing acquire. If this
     * method reports failure, the acquire method may queue the thread, if it is
     * not already queued, until it is signalled by a release from some other
     * thread.
     * 
     * <p>
     * The default implementation throws {@link UnsupportedOperationException}.
     * 
     * @param arg
     *            the acquire argument. This value is always the one passed to
     *            an acquire method, or is the value saved on entry to a
     *            condition wait. The value is otherwise uninterpreted and can
     *            represent anything you like.
     * @return a negative value on failure; zero if acquisition in shared mode
     *         succeeded but no subsequent shared-mode acquire can succeed; and
     *         a positive value if acquisition in shared mode succeeded and
     *         subsequent shared-mode acquires might also succeed, in which case
     *         a subsequent waiting thread must check availability. (Support for
     *         three different return values enables this method to be used in
     *         contexts where acquires only sometimes act exclusively.) Upon
     *         success, this object has been acquired.
     * @throws IllegalMonitorStateException
     *             if acquiring would place this synchronizer in an illegal
     *             state. This exception must be thrown in a consistent fashion
     *             for synchronization to work correctly.
     * @throws UnsupportedOperationException
     *             if shared mode is not supported
     */
    protected int tryAcquireShared(int arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * Attempts to set the state to reflect a release in shared mode.
     * 
     * <p>
     * This method is always invoked by the thread performing release.
     * 
     * <p>
     * The default implementation throws {@link UnsupportedOperationException}.
     * 
     * @param arg
     *            the release argument. This value is always the one passed to a
     *            release method, or the current state value upon entry to a
     *            condition wait. The value is otherwise uninterpreted and can
     *            represent anything you like.
     * @return {@code true} if this release of shared mode may permit a waiting
     *         acquire (shared or exclusive) to succeed; and {@code false}
     *         otherwise
     * @throws IllegalMonitorStateException
     *             if releasing would place this synchronizer in an illegal
     *             state. This exception must be thrown in a consistent fashion
     *             for synchronization to work correctly.
     * @throws UnsupportedOperationException
     *             if shared mode is not supported
     */
    protected boolean tryReleaseShared(int arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns {@code true} if synchronization is held exclusively with respect
     * to the current (calling) thread. This method is invoked upon each call to
     * a non-waiting {@link ConditionObject} method. (Waiting methods instead
     * invoke {@link #release}.)
     * 
     * <p>
     * The default implementation throws {@link UnsupportedOperationException}.
     * This method is invoked internally only within {@link ConditionObject}
     * methods, so need not be defined if conditions are not used.
     * 
     * @return {@code true} if synchronization is held exclusively;
     *         {@code false} otherwise
     * @throws UnsupportedOperationException
     *             if conditions are not supported
     */
    protected boolean isHeldExclusively() {
        throw new UnsupportedOperationException();
    }

    /**
     * Acquires in exclusive mode, aborting if interrupted. Implemented by first
     * checking interrupt status, then invoking at least once
     * {@link #tryAcquire}, returning on success. Otherwise the thread is
     * queued, possibly repeatedly blocking and unblocking, invoking
     * {@link #tryAcquire} until success or the thread is interrupted. This
     * method can be used to implement method {@link Lock#lockInterruptibly}.
     * <p>
     * 独占且可中断模式获取：支持中断取消。
     * 
     * @param arg
     *            the acquire argument. This value is conveyed to
     *            {@link #tryAcquire} but is otherwise uninterpreted and can
     *            represent anything you like.
     * @throws InterruptedException
     *             if the current thread is interrupted
     */
    public final void acquireInterruptibly(int arg) throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        if (!tryAcquire(arg))
            doAcquireInterruptibly(arg);
    }

    /**
     * Attempts to acquire in exclusive mode, aborting if interrupted, and
     * failing if the given timeout elapses. Implemented by first checking
     * interrupt status, then invoking at least once {@link #tryAcquire},
     * returning on success. Otherwise, the thread is queued, possibly
     * repeatedly blocking and unblocking, invoking {@link #tryAcquire} until
     * success or the thread is interrupted or the timeout elapses. This method
     * can be used to implement method {@link Lock#tryLock(long, TimeUnit)}.
     * <p>
     * 独占且支持超时模式获取： 带有超时时间，如果超过超时时间则会退出。
     * 
     * @param arg
     *            the acquire argument. This value is conveyed to
     *            {@link #tryAcquire} but is otherwise uninterpreted and can
     *            represent anything you like.
     * @param nanosTimeout
     *            the maximum number of nanoseconds to wait
     * @return {@code true} if acquired; {@code false} if timed out
     * @throws InterruptedException
     *             if the current thread is interrupted
     */
    public final boolean tryAcquireNanos(int arg, long nanosTimeout) throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        return tryAcquire(arg) || doAcquireNanos(arg, nanosTimeout);
    }

    /**
     * Acquires in shared mode, ignoring interrupts. Implemented by first
     * invoking at least once {@link #tryAcquireShared}, returning on success.
     * Otherwise the thread is queued, possibly repeatedly blocking and
     * unblocking, invoking {@link #tryAcquireShared} until success.
     * <p>
     * 共享模式获取。
     * 
     * @param arg
     *            the acquire argument. This value is conveyed to
     *            {@link #tryAcquireShared} but is otherwise uninterpreted and
     *            can represent anything you like.
     */
    public final void acquireShared(int arg) {
        if (tryAcquireShared(arg) < 0) {
            doAcquireShared(arg);
        }
        System.out.println(Thread.currentThread().getId() + " acquireShared(): success");
    }

    /**
     * Acquires in shared mode, aborting if interrupted. Implemented by first
     * checking interrupt status, then invoking at least once
     * {@link #tryAcquireShared}, returning on success. Otherwise the thread is
     * queued, possibly repeatedly blocking and unblocking, invoking
     * {@link #tryAcquireShared} until success or the thread is interrupted.
     * <p>
     * 可中断模式共享获取。
     * 
     * @param arg
     *            the acquire argument This value is conveyed to
     *            {@link #tryAcquireShared} but is otherwise uninterpreted and
     *            can represent anything you like.
     * @throws InterruptedException
     *             if the current thread is interrupted
     */
    public final void acquireSharedInterruptibly(int arg) throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        if (tryAcquireShared(arg) < 0)
            doAcquireSharedInterruptibly(arg);
    }

    /**
     * Attempts to acquire in shared mode, aborting if interrupted, and failing
     * if the given timeout elapses. Implemented by first checking interrupt
     * status, then invoking at least once {@link #tryAcquireShared}, returning
     * on success. Otherwise, the thread is queued, possibly repeatedly blocking
     * and unblocking, invoking {@link #tryAcquireShared} until success or the
     * thread is interrupted or the timeout elapses.
     * <p>
     * 共享模式带超时。
     * 
     * @param arg
     *            the acquire argument. This value is conveyed to
     *            {@link #tryAcquireShared} but is otherwise uninterpreted and
     *            can represent anything you like.
     * @param nanosTimeout
     *            the maximum number of nanoseconds to wait
     * @return {@code true} if acquired; {@code false} if timed out
     * @throws InterruptedException
     *             if the current thread is interrupted
     */
    public final boolean tryAcquireSharedNanos(int arg, long nanosTimeout) throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        return tryAcquireShared(arg) >= 0 || doAcquireSharedNanos(arg, nanosTimeout);
    }

    /**
     * Releases in shared mode. Implemented by unblocking one or more threads if
     * {@link #tryReleaseShared} returns true.
     * <p>
     * 共享锁释放。
     * 
     * @param arg
     *            the release argument. This value is conveyed to
     *            {@link #tryReleaseShared} but is otherwise uninterpreted and
     *            can represent anything you like.
     * @return the value returned from {@link #tryReleaseShared}
     */
    public final boolean releaseShared(int arg) {
        if (tryReleaseShared(arg)) {
            doReleaseShared();
            return true;
        }
        return false;
    }

    // Queue inspection methods

    /**
     * Queries whether any threads are waiting to acquire. Note that because
     * cancellations due to interrupts and timeouts may occur at any time, a
     * {@code true} return does not guarantee that any other thread will ever
     * acquire.
     * 
     * <p>
     * In this implementation, this operation returns in constant time.
     * 
     * @return {@code true} if there may be other threads waiting to acquire
     */
    public final boolean hasQueuedThreads() {
        return head != tail;
    }

    /**
     * Queries whether any threads have ever contended to acquire this
     * synchronizer; that is if an acquire method has ever blocked.
     * 
     * <p>
     * In this implementation, this operation returns in constant time.
     * 
     * @return {@code true} if there has ever been contention
     */
    public final boolean hasContended() {
        return head != null;
    }

    /**
     * Returns the first (longest-waiting) thread in the queue, or {@code null}
     * if no threads are currently queued.
     * 
     * <p>
     * In this implementation, this operation normally returns in constant time,
     * but may iterate upon contention if other threads are concurrently
     * modifying the queue.
     * 
     * @return the first (longest-waiting) thread in the queue, or {@code null}
     *         if no threads are currently queued
     */
    public final Thread getFirstQueuedThread() {
        // handle only fast path, else relay
        return (head == tail) ? null : fullGetFirstQueuedThread();
    }

    /**
     * Version of getFirstQueuedThread called when fastpath fails
     */
    private Thread fullGetFirstQueuedThread() {
        /*
         * The first node is normally head.next. Try to get its thread field,
         * ensuring consistent reads: If thread field is nulled out or s.prev is
         * no longer head, then some other thread(s) concurrently performed
         * setHead in between some of our reads. We try this twice before
         * resorting to traversal.
         */
        Node h, s;
        Thread st;
        if (((h = head) != null && (s = h.next) != null && s.prev == head && (st = s.thread) != null)
                || ((h = head) != null && (s = h.next) != null && s.prev == head && (st = s.thread) != null))
            return st;

        /*
         * Head's next field might not have been set yet, or may have been unset
         * after setHead. So we must check to see if tail is actually first
         * node. If not, we continue on, safely traversing from tail back to
         * head to find first, guaranteeing termination.
         */

        Node t = tail;
        Thread firstThread = null;
        while (t != null && t != head) {
            Thread tt = t.thread;
            if (tt != null)
                firstThread = tt;
            t = t.prev;
        }
        return firstThread;
    }

    /**
     * Returns true if the given thread is currently queued.
     * 
     * <p>
     * This implementation traverses the queue to determine presence of the
     * given thread.
     * 
     * @param thread
     *            the thread
     * @return {@code true} if the given thread is on the queue
     * @throws NullPointerException
     *             if the thread is null
     */
    public final boolean isQueued(Thread thread) {
        if (thread == null)
            throw new NullPointerException();
        for (Node p = tail; p != null; p = p.prev)
            if (p.thread == thread)
                return true;
        return false;
    }

    /**
     * Returns {@code true} if the apparent first queued thread, if one exists,
     * is waiting in exclusive mode. If this method returns {@code true}, and
     * the current thread is attempting to acquire in shared mode (that is, this
     * method is invoked from {@link #tryAcquireShared}) then it is guaranteed
     * that the current thread is not the first queued thread. Used only as a
     * heuristic in ReentrantReadWriteLock.
     */
    final boolean apparentlyFirstQueuedIsExclusive() {
        Node h, s;
        return (h = head) != null && (s = h.next) != null && !s.isShared() && s.thread != null;
    }

    /**
     * Queries whether any threads have been waiting to acquire longer than the
     * current thread.
     * 
     * <p>
     * An invocation of this method is equivalent to (but may be more efficient
     * than):
     * 
     * <pre>
     * {@code
     * getFirstQueuedThread() != Thread.currentThread() &&
     * hasQueuedThreads()}
     * </pre>
     * 
     * <p>
     * Note that because cancellations due to interrupts and timeouts may occur
     * at any time, a {@code true} return does not guarantee that some other
     * thread will acquire before the current thread. Likewise, it is possible
     * for another thread to win a race to enqueue after this method has
     * returned {@code false}, due to the queue being empty.
     * 
     * <p>
     * This method is designed to be used by a fair synchronizer to avoid <a
     * href="AbstractQueuedSynchronizer#barging">barging</a>. Such a
     * synchronizer's {@link #tryAcquire} method should return {@code false},
     * and its {@link #tryAcquireShared} method should return a negative value,
     * if this method returns {@code true} (unless this is a reentrant acquire).
     * For example, the {@code tryAcquire} method for a fair, reentrant,
     * exclusive mode synchronizer might look like this:
     * 
     * <pre>
     * {@code
     * protected boolean tryAcquire(int arg) {
     *   if (isHeldExclusively()) {
     *     // A reentrant acquire; increment hold count
     *     return true;
     *   } else if (hasQueuedPredecessors()) {
     *     return false;
     *   } else {
     *     // try to acquire normally
     *   }
     * }}
     * </pre>
     * 
     * @return {@code true} if there is a queued thread preceding the current
     *         thread, and {@code false} if the current thread is at the head of
     *         the queue or the queue is empty
     * @since 1.7
     */
    public final boolean hasQueuedPredecessors() {
        // The correctness of this depends on head being initialized
        // before tail and on head.next being accurate if the current
        // thread is first in queue.
        Node t = tail; // Read fields in reverse initialization order
        Node h = head;
        Node s;
        return h != t && ((s = h.next) == null || s.thread != Thread.currentThread());
    }

    // Instrumentation and monitoring methods

    /**
     * Returns an estimate of the number of threads waiting to acquire. The
     * value is only an estimate because the number of threads may change
     * dynamically while this method traverses internal data structures. This
     * method is designed for use in monitoring system state, not for
     * synchronization control.
     * 
     * @return the estimated number of threads waiting to acquire
     */
    public final int getQueueLength() {
        int n = 0;
        for (Node p = tail; p != null; p = p.prev) {
            if (p.thread != null)
                ++n;
        }
        return n;
    }

    /**
     * Returns a collection containing threads that may be waiting to acquire.
     * Because the actual set of threads may change dynamically while
     * constructing this result, the returned collection is only a best-effort
     * estimate. The elements of the returned collection are in no particular
     * order. This method is designed to facilitate construction of subclasses
     * that provide more extensive monitoring facilities.
     * 
     * @return the collection of threads
     */
    public final Collection<Thread> getQueuedThreads() {
        ArrayList<Thread> list = new ArrayList<Thread>();
        for (Node p = tail; p != null; p = p.prev) {
            Thread t = p.thread;
            if (t != null)
                list.add(t);
        }
        return list;
    }

    /**
     * Returns a collection containing threads that may be waiting to acquire in
     * exclusive mode. This has the same properties as {@link #getQueuedThreads}
     * except that it only returns those threads waiting due to an exclusive
     * acquire.
     * 
     * @return the collection of threads
     */
    public final Collection<Thread> getExclusiveQueuedThreads() {
        ArrayList<Thread> list = new ArrayList<Thread>();
        for (Node p = tail; p != null; p = p.prev) {
            if (!p.isShared()) {
                Thread t = p.thread;
                if (t != null)
                    list.add(t);
            }
        }
        return list;
    }

    /**
     * Returns a collection containing threads that may be waiting to acquire in
     * shared mode. This has the same properties as {@link #getQueuedThreads}
     * except that it only returns those threads waiting due to a shared
     * acquire.
     * 
     * @return the collection of threads
     */
    public final Collection<Thread> getSharedQueuedThreads() {
        ArrayList<Thread> list = new ArrayList<Thread>();
        for (Node p = tail; p != null; p = p.prev) {
            if (p.isShared()) {
                Thread t = p.thread;
                if (t != null)
                    list.add(t);
            }
        }
        return list;
    }

    /**
     * Returns a string identifying this synchronizer, as well as its state. The
     * state, in brackets, includes the String {@code "State ="} followed by the
     * current value of {@link #getState}, and either {@code "nonempty"} or
     * {@code "empty"} depending on whether the queue is empty.
     * 
     * @return a string identifying this synchronizer, as well as its state
     */
    public String toString() {
        int s = getState();
        String q = hasQueuedThreads() ? "non" : "";
        return super.toString() + "[State = " + s + ", " + q + "empty queue]";
    }

    // Internal support methods for Conditions

    /**
     * Returns true if a node, always one that was initially placed on a
     * condition queue, is now waiting to reacquire on sync queue.
     * 
     * @param node
     *            the node
     * @return true if is reacquiring
     */
    final boolean isOnSyncQueue(Node node) {
        if (node.waitStatus == Node.CONDITION || node.prev == null)
            return false;
        if (node.next != null) // If has successor, it must be on queue
            return true;
        /*
         * node.prev can be non-null, but not yet on queue because the CAS to
         * place it on queue can fail. So we have to traverse from tail to make
         * sure it actually made it. It will always be near the tail in calls to
         * this method, and unless the CAS failed (which is unlikely), it will
         * be there, so we hardly ever traverse much.
         */
        return findNodeFromTail(node);
    }

    /**
     * Returns true if node is on sync queue by searching backwards from tail.
     * Called only when needed by isOnSyncQueue.
     * 
     * @return true if present
     */
    private boolean findNodeFromTail(Node node) {
        Node t = tail;
        for (;;) {
            if (t == node)
                return true;
            if (t == null)
                return false;
            t = t.prev;
        }
    }

    /**
     * Transfers a node from a condition queue onto sync queue. Returns true if
     * successful.
     * 
     * @param node
     *            the node
     * @return true if successfully transferred (else the node was cancelled
     *         before signal).
     */
    final boolean transferForSignal(Node node) {
        /*
         * If cannot change waitStatus, the node has been cancelled.
         */
        if (!compareAndSetWaitStatus(node, Node.CONDITION, 0))
            return false;

        /*
         * Splice onto queue and try to set waitStatus of predecessor to
         * indicate that thread is (probably) waiting. If cancelled or attempt
         * to set waitStatus fails, wake up to resync (in which case the
         * waitStatus can be transiently and harmlessly wrong).
         */
        Node p = enq(node);
        int ws = p.waitStatus;
        if (ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL))
            LockSupport.unpark(node.thread);
        return true;
    }

    /**
     * Transfers node, if necessary, to sync queue after a cancelled wait.
     * Returns true if thread was cancelled before being signalled.
     * 
     * @param current
     *            the waiting thread
     * @param node
     *            its node
     * @return true if cancelled before the node was signalled
     */
    final boolean transferAfterCancelledWait(Node node) {
        if (compareAndSetWaitStatus(node, Node.CONDITION, 0)) {
            enq(node);
            return true;
        }
        /*
         * If we lost out to a signal(), then we can't proceed until it finishes
         * its enq(). Cancelling during an incomplete transfer is both rare and
         * transient, so just spin.
         */
        while (!isOnSyncQueue(node))
            Thread.yield();
        return false;
    }

    /**
     * Invokes release with current state value; returns saved state. Cancels
     * node and throws exception on failure.
     * 
     * @param node
     *            the condition node for this wait
     * @return previous sync state
     */
    final int fullyRelease(Node node) {
        boolean failed = true;
        try {
            int savedState = getState();
            if (release(savedState)) {
                failed = false;
                return savedState;
            } else {
                throw new IllegalMonitorStateException();
            }
        } finally {
            if (failed)
                node.waitStatus = Node.CANCELLED;
        }
    }

    // Instrumentation methods for conditions

    /**
     * Queries whether the given ConditionObject uses this synchronizer as its
     * lock.
     * 
     * @param condition
     *            the condition
     * @return <tt>true</tt> if owned
     * @throws NullPointerException
     *             if the condition is null
     */
    public final boolean owns(ConditionObject condition) {
        if (condition == null)
            throw new NullPointerException();
        return condition.isOwnedBy(this);
    }

    /**
     * Queries whether any threads are waiting on the given condition associated
     * with this synchronizer. Note that because timeouts and interrupts may
     * occur at any time, a <tt>true</tt> return does not guarantee that a
     * future <tt>signal</tt> will awaken any threads. This method is designed
     * primarily for use in monitoring of the system state.
     * 
     * @param condition
     *            the condition
     * @return <tt>true</tt> if there are any waiting threads
     * @throws IllegalMonitorStateException
     *             if exclusive synchronization is not held
     * @throws IllegalArgumentException
     *             if the given condition is not associated with this
     *             synchronizer
     * @throws NullPointerException
     *             if the condition is null
     */
    public final boolean hasWaiters(ConditionObject condition) {
        if (!owns(condition))
            throw new IllegalArgumentException("Not owner");
        return condition.hasWaiters();
    }

    /**
     * Returns an estimate of the number of threads waiting on the given
     * condition associated with this synchronizer. Note that because timeouts
     * and interrupts may occur at any time, the estimate serves only as an
     * upper bound on the actual number of waiters. This method is designed for
     * use in monitoring of the system state, not for synchronization control.
     * 
     * @param condition
     *            the condition
     * @return the estimated number of waiting threads
     * @throws IllegalMonitorStateException
     *             if exclusive synchronization is not held
     * @throws IllegalArgumentException
     *             if the given condition is not associated with this
     *             synchronizer
     * @throws NullPointerException
     *             if the condition is null
     */
    public final int getWaitQueueLength(ConditionObject condition) {
        if (!owns(condition))
            throw new IllegalArgumentException("Not owner");
        return condition.getWaitQueueLength();
    }

    /**
     * Returns a collection containing those threads that may be waiting on the
     * given condition associated with this synchronizer. Because the actual set
     * of threads may change dynamically while constructing this result, the
     * returned collection is only a best-effort estimate. The elements of the
     * returned collection are in no particular order.
     * 
     * @param condition
     *            the condition
     * @return the collection of threads
     * @throws IllegalMonitorStateException
     *             if exclusive synchronization is not held
     * @throws IllegalArgumentException
     *             if the given condition is not associated with this
     *             synchronizer
     * @throws NullPointerException
     *             if the condition is null
     */
    public final Collection<Thread> getWaitingThreads(ConditionObject condition) {
        if (!owns(condition))
            throw new IllegalArgumentException("Not owner");
        return condition.getWaitingThreads();
    }

    /**
     * Condition implementation for a {@link AbstractQueuedSynchronizer} serving
     * as the basis of a {@link Lock} implementation.
     * 
     * <p>
     * Method documentation for this class describes mechanics, not behavioral
     * specifications from the point of view of Lock and Condition users.
     * Exported versions of this class will in general need to be accompanied by
     * documentation describing condition semantics that rely on those of the
     * associated <tt>AbstractQueuedSynchronizer</tt>.
     * 
     * <p>
     * This class is Serializable, but all fields are transient, so deserialized
     * conditions have no waiters.
     */
    public class ConditionObject implements Condition, java.io.Serializable {
        private static final long serialVersionUID = 1173984872572414699L;
        /** First node of condition queue. */
        private transient Node firstWaiter;
        /** Last node of condition queue. */
        private transient Node lastWaiter;

        /**
         * Creates a new <tt>ConditionObject</tt> instance.
         */
        public ConditionObject() {
        }

        // Internal methods

        /**
         * Adds a new waiter to wait queue.
         * 
         * @return its new wait node
         */
        private Node addConditionWaiter() {
            Node t = lastWaiter;
            // If lastWaiter is cancelled, clean out.
            if (t != null && t.waitStatus != Node.CONDITION) {
                unlinkCancelledWaiters();
                t = lastWaiter;
            }
            Node node = new Node(Thread.currentThread(), Node.CONDITION);
            if (t == null)
                firstWaiter = node;
            else
                t.nextWaiter = node;
            lastWaiter = node;
            return node;
        }

        /**
         * Removes and transfers nodes until hit non-cancelled one or null.
         * Split out from signal in part to encourage compilers to inline the
         * case of no waiters.
         * 
         * @param first
         *            (non-null) the first node on condition queue
         */
        private void doSignal(Node first) {
            do {
                if ((firstWaiter = first.nextWaiter) == null)
                    lastWaiter = null;
                first.nextWaiter = null;
            } while (!transferForSignal(first) && (first = firstWaiter) != null);
        }

        /**
         * Removes and transfers all nodes.
         * 
         * @param first
         *            (non-null) the first node on condition queue
         */
        private void doSignalAll(Node first) {
            lastWaiter = firstWaiter = null;
            do {
                Node next = first.nextWaiter;
                first.nextWaiter = null;
                transferForSignal(first);
                first = next;
            } while (first != null);
        }

        /**
         * Unlinks cancelled waiter nodes from condition queue. Called only
         * while holding lock. This is called when cancellation occurred during
         * condition wait, and upon insertion of a new waiter when lastWaiter is
         * seen to have been cancelled. This method is needed to avoid garbage
         * retention in the absence of signals. So even though it may require a
         * full traversal, it comes into play only when timeouts or
         * cancellations occur in the absence of signals. It traverses all nodes
         * rather than stopping at a particular target to unlink all pointers to
         * garbage nodes without requiring many re-traversals during
         * cancellation storms.
         */
        private void unlinkCancelledWaiters() {
            Node t = firstWaiter;
            Node trail = null;
            while (t != null) {
                Node next = t.nextWaiter;
                if (t.waitStatus != Node.CONDITION) {
                    t.nextWaiter = null;
                    if (trail == null)
                        firstWaiter = next;
                    else
                        trail.nextWaiter = next;
                    if (next == null)
                        lastWaiter = trail;
                } else
                    trail = t;
                t = next;
            }
        }

        // public methods

        /**
         * Moves the longest-waiting thread, if one exists, from the wait queue
         * for this condition to the wait queue for the owning lock.
         * 
         * @throws IllegalMonitorStateException
         *             if {@link #isHeldExclusively} returns {@code false}
         */
        public final void signal() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            Node first = firstWaiter;
            if (first != null)
                doSignal(first);
        }

        /**
         * Moves all threads from the wait queue for this condition to the wait
         * queue for the owning lock.
         * 
         * @throws IllegalMonitorStateException
         *             if {@link #isHeldExclusively} returns {@code false}
         */
        public final void signalAll() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            Node first = firstWaiter;
            if (first != null)
                doSignalAll(first);
        }

        /**
         * Implements uninterruptible condition wait.
         * <ol>
         * <li>Save lock state returned by {@link #getState}.
         * <li>Invoke {@link #release} with saved state as argument, throwing
         * IllegalMonitorStateException if it fails.
         * <li>Block until signalled.
         * <li>Reacquire by invoking specialized version of {@link #acquire}
         * with saved state as argument.
         * </ol>
         */
        public final void awaitUninterruptibly() {
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            boolean interrupted = false;
            while (!isOnSyncQueue(node)) {
                LockSupport.park(this);
                if (Thread.interrupted())
                    interrupted = true;
            }
            if (acquireQueued(node, savedState) || interrupted)
                selfInterrupt();
        }

        /*
         * For interruptible waits, we need to track whether to throw
         * InterruptedException, if interrupted while blocked on condition,
         * versus reinterrupt current thread, if interrupted while blocked
         * waiting to re-acquire.
         */

        /** Mode meaning to reinterrupt on exit from wait */
        private static final int REINTERRUPT = 1;
        /** Mode meaning to throw InterruptedException on exit from wait */
        private static final int THROW_IE = -1;

        /**
         * Checks for interrupt, returning THROW_IE if interrupted before
         * signalled, REINTERRUPT if after signalled, or 0 if not interrupted.
         */
        private int checkInterruptWhileWaiting(Node node) {
            return Thread.interrupted() ? (transferAfterCancelledWait(node) ? THROW_IE : REINTERRUPT) : 0;
        }

        /**
         * Throws InterruptedException, reinterrupts current thread, or does
         * nothing, depending on mode.
         */
        private void reportInterruptAfterWait(int interruptMode) throws InterruptedException {
            if (interruptMode == THROW_IE)
                throw new InterruptedException();
            else if (interruptMode == REINTERRUPT)
                selfInterrupt();
        }

        /**
         * Implements interruptible condition wait.
         * <ol>
         * <li>If current thread is interrupted, throw InterruptedException.
         * <li>Save lock state returned by {@link #getState}.
         * <li>Invoke {@link #release} with saved state as argument, throwing
         * IllegalMonitorStateException if it fails.
         * <li>Block until signalled or interrupted.
         * <li>Reacquire by invoking specialized version of {@link #acquire}
         * with saved state as argument.
         * <li>If interrupted while blocked in step 4, throw
         * InterruptedException.
         * </ol>
         */
        public final void await() throws InterruptedException {
            if (Thread.interrupted())
                throw new InterruptedException();
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            int interruptMode = 0;
            while (!isOnSyncQueue(node)) {
                LockSupport.park(this);
                if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                    break;
            }
            if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
                interruptMode = REINTERRUPT;
            if (node.nextWaiter != null) // clean up if cancelled
                unlinkCancelledWaiters();
            if (interruptMode != 0)
                reportInterruptAfterWait(interruptMode);
        }

        /**
         * Implements timed condition wait.
         * <ol>
         * <li>If current thread is interrupted, throw InterruptedException.
         * <li>Save lock state returned by {@link #getState}.
         * <li>Invoke {@link #release} with saved state as argument, throwing
         * IllegalMonitorStateException if it fails.
         * <li>Block until signalled, interrupted, or timed out.
         * <li>Reacquire by invoking specialized version of {@link #acquire}
         * with saved state as argument.
         * <li>If interrupted while blocked in step 4, throw
         * InterruptedException.
         * </ol>
         */
        public final long awaitNanos(long nanosTimeout) throws InterruptedException {
            if (Thread.interrupted())
                throw new InterruptedException();
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            long lastTime = System.nanoTime();
            int interruptMode = 0;
            while (!isOnSyncQueue(node)) {
                if (nanosTimeout <= 0L) {
                    transferAfterCancelledWait(node);
                    break;
                }
                LockSupport.parkNanos(this, nanosTimeout);
                if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                    break;

                long now = System.nanoTime();
                nanosTimeout -= now - lastTime;
                lastTime = now;
            }
            if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
                interruptMode = REINTERRUPT;
            if (node.nextWaiter != null)
                unlinkCancelledWaiters();
            if (interruptMode != 0)
                reportInterruptAfterWait(interruptMode);
            return nanosTimeout - (System.nanoTime() - lastTime);
        }

        /**
         * Implements absolute timed condition wait.
         * <ol>
         * <li>If current thread is interrupted, throw InterruptedException.
         * <li>Save lock state returned by {@link #getState}.
         * <li>Invoke {@link #release} with saved state as argument, throwing
         * IllegalMonitorStateException if it fails.
         * <li>Block until signalled, interrupted, or timed out.
         * <li>Reacquire by invoking specialized version of {@link #acquire}
         * with saved state as argument.
         * <li>If interrupted while blocked in step 4, throw
         * InterruptedException.
         * <li>If timed out while blocked in step 4, return false, else true.
         * </ol>
         */
        public final boolean awaitUntil(Date deadline) throws InterruptedException {
            if (deadline == null)
                throw new NullPointerException();
            long abstime = deadline.getTime();
            if (Thread.interrupted())
                throw new InterruptedException();
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            boolean timedout = false;
            int interruptMode = 0;
            while (!isOnSyncQueue(node)) {
                if (System.currentTimeMillis() > abstime) {
                    timedout = transferAfterCancelledWait(node);
                    break;
                }
                LockSupport.parkUntil(this, abstime);
                if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                    break;
            }
            if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
                interruptMode = REINTERRUPT;
            if (node.nextWaiter != null)
                unlinkCancelledWaiters();
            if (interruptMode != 0)
                reportInterruptAfterWait(interruptMode);
            return !timedout;
        }

        /**
         * Implements timed condition wait.
         * <ol>
         * <li>If current thread is interrupted, throw InterruptedException.
         * <li>Save lock state returned by {@link #getState}.
         * <li>Invoke {@link #release} with saved state as argument, throwing
         * IllegalMonitorStateException if it fails.
         * <li>Block until signalled, interrupted, or timed out.
         * <li>Reacquire by invoking specialized version of {@link #acquire}
         * with saved state as argument.
         * <li>If interrupted while blocked in step 4, throw
         * InterruptedException.
         * <li>If timed out while blocked in step 4, return false, else true.
         * </ol>
         */
        public final boolean await(long time, TimeUnit unit) throws InterruptedException {
            if (unit == null)
                throw new NullPointerException();
            long nanosTimeout = unit.toNanos(time);
            if (Thread.interrupted())
                throw new InterruptedException();
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            long lastTime = System.nanoTime();
            boolean timedout = false;
            int interruptMode = 0;
            while (!isOnSyncQueue(node)) {
                if (nanosTimeout <= 0L) {
                    timedout = transferAfterCancelledWait(node);
                    break;
                }
                if (nanosTimeout >= spinForTimeoutThreshold)
                    LockSupport.parkNanos(this, nanosTimeout);
                if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                    break;
                long now = System.nanoTime();
                nanosTimeout -= now - lastTime;
                lastTime = now;
            }
            if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
                interruptMode = REINTERRUPT;
            if (node.nextWaiter != null)
                unlinkCancelledWaiters();
            if (interruptMode != 0)
                reportInterruptAfterWait(interruptMode);
            return !timedout;
        }

        // support for instrumentation

        /**
         * Returns true if this condition was created by the given
         * synchronization object.
         * 
         * @return {@code true} if owned
         */
        final boolean isOwnedBy(AbstractQueuedSynchronizer sync) {
            return sync == AbstractQueuedSynchronizer.this;
        }

        /**
         * Queries whether any threads are waiting on this condition. Implements
         * {@link AbstractQueuedSynchronizer#hasWaiters}.
         * 
         * @return {@code true} if there are any waiting threads
         * @throws IllegalMonitorStateException
         *             if {@link #isHeldExclusively} returns {@code false}
         */
        protected final boolean hasWaiters() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            for (Node w = firstWaiter; w != null; w = w.nextWaiter) {
                if (w.waitStatus == Node.CONDITION)
                    return true;
            }
            return false;
        }

        /**
         * Returns an estimate of the number of threads waiting on this
         * condition. Implements
         * {@link AbstractQueuedSynchronizer#getWaitQueueLength}.
         * 
         * @return the estimated number of waiting threads
         * @throws IllegalMonitorStateException
         *             if {@link #isHeldExclusively} returns {@code false}
         */
        protected final int getWaitQueueLength() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            int n = 0;
            for (Node w = firstWaiter; w != null; w = w.nextWaiter) {
                if (w.waitStatus == Node.CONDITION)
                    ++n;
            }
            return n;
        }

        /**
         * Returns a collection containing those threads that may be waiting on
         * this Condition. Implements
         * {@link AbstractQueuedSynchronizer#getWaitingThreads}.
         * 
         * @return the collection of threads
         * @throws IllegalMonitorStateException
         *             if {@link #isHeldExclusively} returns {@code false}
         */
        protected final Collection<Thread> getWaitingThreads() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            ArrayList<Thread> list = new ArrayList<Thread>();
            for (Node w = firstWaiter; w != null; w = w.nextWaiter) {
                if (w.waitStatus == Node.CONDITION) {
                    Thread t = w.thread;
                    if (t != null)
                        list.add(t);
                }
            }
            return list;
        }
    }

    /**
     * Setup to support compareAndSet. We need to natively implement this here:
     * For the sake of permitting future enhancements, we cannot explicitly
     * subclass AtomicInteger, which would be efficient and useful otherwise.
     * So, as the lesser of evils, we natively implement using hotspot
     * intrinsics API. And while we are at it, we do the same for other CASable
     * fields (which could otherwise be done with atomic field updaters).
     */
    //private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final Unsafe unsafe;
    private static final long stateOffset;
    private static final long headOffset;
    private static final long tailOffset;
    private static final long waitStatusOffset;
    private static final long nextOffset;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
            
            stateOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("state"));
            headOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("head"));
            tailOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("tail"));
            waitStatusOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("waitStatus"));
            nextOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("next"));

        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    /**
     * CAS head field. Used only by enq.
     */
    private final boolean compareAndSetHead(Node update) {
        return unsafe.compareAndSwapObject(this, headOffset, null, update);
    }

    /**
     * CAS tail field. Used only by enq.
     */
    private final boolean compareAndSetTail(Node expect, Node update) {
        return unsafe.compareAndSwapObject(this, tailOffset, expect, update);
    }

    /**
     * CAS waitStatus field of a node.
     */
    private static final boolean compareAndSetWaitStatus(Node node, int expect, int update) {
        return unsafe.compareAndSwapInt(node, waitStatusOffset, expect, update);
    }

    /**
     * CAS next field of a node.
     */
    private static final boolean compareAndSetNext(Node node, Node expect, Node update) {
        return unsafe.compareAndSwapObject(node, nextOffset, expect, update);
    }
}
