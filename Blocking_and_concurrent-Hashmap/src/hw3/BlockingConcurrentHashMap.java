/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hw3;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Omi
 */
public class BlockingConcurrentHashMap<K, V> implements Map<K, V>, Cloneable {

    private volatile Map<K, V> internalMap;
    private final ReentrantLock lock = new ReentrantLock();
    final Condition flag = lock.newCondition();

    /**
     * @param args the command line arguments
     */
    public BlockingConcurrentHashMap() {
        internalMap = new HashMap<K, V>();
    }

    public BlockingConcurrentHashMap(Map<K, V> map) {
        internalMap = new HashMap<K, V>(map);
    }

    public BlockingConcurrentHashMap(int initCap) {
        internalMap = new HashMap<>(initCap);
    }

    @Override
    public int size() {
        return internalMap.size();
    }

    @Override
    public boolean isEmpty() {
        return internalMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return internalMap.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return internalMap.containsValue(o);
    }

    @Override

    public Set<K> keySet() {
        return internalMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return internalMap.values();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return internalMap.entrySet();
    }

    public V put(K key, V value) {

        lock.lock();//acquire lock to perform the function
        try {
            internalMap.put(key, value);

            flag.signalAll();//send signal to all the other threads
            return value;
        } finally {
            lock.unlock();//unlock
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {

        lock.lock();//acquire lock to perform the function
        try {
            internalMap.putAll(map);

            flag.signalAll();//send signal to all the other threads

        } finally {
            lock.unlock();
        }

    }

    public V get(Object o) {
        // return internalMap.get(o);
        lock.lock();//acquire lock to perform the function
        try {
            if (internalMap.containsKey(o)) {//if the internalMap contains the key,no blocking
                return internalMap.get(o);
            } else {
                try {

                    while (!internalMap.containsKey(o)) {//block if the internalMap does'nt have the key
                        flag.await();
                    }

                } catch (InterruptedException ex) {
                    Logger.getLogger(BlockingConcurrentHashMap.class.getName()).log(Level.SEVERE, null, ex);
                }
                return internalMap.get(o);
            }
        } finally {
            lock.unlock();//unlock
        }
    }

    public V remove(Object key) {
        lock.lock();//acquire lock to perform the function
        try {
            if (internalMap.containsKey(key)) {
                V val = internalMap.remove(key);
                return val;
            } else {
                try {

                    while (!internalMap.containsKey(key)) {//block if the internalMap does'nt have the key
                        flag.await();
                    }

                } catch (InterruptedException ex) {
                    Logger.getLogger(BlockingConcurrentHashMap.class.getName()).log(Level.SEVERE, null, ex);
                }
                V val = internalMap.remove(key);
                return val;
            }
        } finally {
            lock.unlock();//unlock
        }

    }

    public void clear() {

        lock.lock();
        try {
            internalMap.clear();

        } finally {
            lock.unlock();
        }

    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
    
    public V tryGet(Object key) {
        try {
            lock.lock();//acquire lock
            while (internalMap.get(key) == null) {//if the key is not found in internalMap
                // it should check over the while loop
                flag.await();//thread is made to wait

            }
            lock.unlock();//lock is released after key is found
        } catch (InterruptedException e) {
            
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return internalMap.get(key);

    }

    public V getInterruptably(Object key) {
        try {
            lock.lockInterruptibly();//the acquiring thread can be interrupted if necessary
            while (internalMap.get(key) == null) {

                flag.await();//thread is made to wait if the key is not found in internalMap

            }
        } catch (InterruptedException e) {
            
            e.printStackTrace();
        } finally {
            lock.unlock();//lock is released
        }

        return internalMap.get(key);
    }

    public V tryGet(Object key, Long timeOut) {
        try {//if the internalMap does'nt contain key wait for a specified period of time and release lock
            if (lock.tryLock(timeOut, TimeUnit.MILLISECONDS)) {
                while (internalMap.get(key) == null) {

                    flag.await();

                }
                lock.unlock();
            } else {
                return null;
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

        }

        return internalMap.get(key);

    }
}
