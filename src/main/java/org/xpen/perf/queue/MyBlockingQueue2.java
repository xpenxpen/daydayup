package org.xpen.perf.queue;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class MyBlockingQueue2<E> implements BlockingQueue<E> {
	
    private LinkedList<E> queue = null;
    private Object lock = null;
    

	public MyBlockingQueue2(int size) {
        queue = new LinkedList<E>();
        lock = new Object();
	}

	@Override
	public void put(E e) throws InterruptedException {
        synchronized(queue) {
            queue.add(e);
        }
        synchronized(lock) {
        	lock.notifyAll();
        }
	}

	@Override
	public E take() throws InterruptedException {
        E e = null;

        while (queue.size() == 0) {
            synchronized(lock) {
            	lock.wait();
            }
        }

        synchronized (queue) {
            if (queue.size() > 0) {
                e = queue.remove();
            }
        }

        return e;
	}

	@Override
	public E remove() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E poll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E element() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E peek() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
        return queue.size();
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean add(E e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean offer(E e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean offer(E e, long timeout, TimeUnit unit)
			throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int remainingCapacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int drainTo(Collection<? super E> c) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int drainTo(Collection<? super E> c, int maxElements) {
		// TODO Auto-generated method stub
		return 0;
	}

}
