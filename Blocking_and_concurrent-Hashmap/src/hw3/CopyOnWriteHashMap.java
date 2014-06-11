/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hw3;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Omi
 */
public class CopyOnWriteHashMap<K, V> implements Map<K, V>, Cloneable {

    private volatile Map<K, V> internalMap;//All copie of internalMap contains the updated value

    /**
     * @param args the command line arguments
     */
//overloaded constructors to assign internalMap with HashMap
    public CopyOnWriteHashMap() {
        internalMap = new HashMap<K, V>();
    }

    public CopyOnWriteHashMap(Map<K, V> map) {
        internalMap = new HashMap<K, V>(map);
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
    public V get(Object o) {
        return internalMap.get(o);
    }

    @Override
    public Set<K> keySet() {
        return Collections.unmodifiableSet(internalMap.keySet());
    }

    @Override
    public Collection<V> values() {
        return Collections.unmodifiableCollection(internalMap.values());
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return Collections.unmodifiableSet(internalMap.entrySet());
    }

    //synchronized versions
    public V put(K key, V value) {

        synchronized (this) {
            //call the constructor of HashMap class which initializes new map wih
            // values in old map
            Map<K, V> copyMap = new HashMap<K, V>(internalMap);
            copyMap.put(key, value);
            internalMap = copyMap;
            return value;
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {

        synchronized (this) {
	    //call the constructor of hashMap class which initializes new map wih
            // values in old map
            Map<K, V> copyMap = new HashMap<K, V>(internalMap);
            copyMap.putAll(map);
            internalMap = copyMap;
        }

    }

    public V remove(Object key) {

        synchronized (this) {
            // create a copy
            Map<K, V> copyMap = new HashMap<K, V>(internalMap);
            V val = copyMap.remove(key);
            internalMap = copyMap;
            // return the removed value 
            return val;
        }

    }

    public void clear() {

        synchronized (this) {
             Map<K, V> copyMap = new HashMap<K, V>(internalMap);
            internalMap = new HashMap<K, V>();
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
}
