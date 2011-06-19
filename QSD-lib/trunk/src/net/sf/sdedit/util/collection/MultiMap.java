package net.sf.sdedit.util.collection;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.sdedit.util.Utilities;

public class MultiMap<K, V, C extends Collection<?>, M extends Map<?,?>> {
    
    private Class<C> collectionType;
    
    private Map<K, C> map;
    
    @SuppressWarnings("unchecked")
    public MultiMap(Class<?> collectionType, Class<?> mapType) {
        this.collectionType = (Class<C>) collectionType;
        map = (Map<K, C>) Utilities.newInstance(mapType.getName(), mapType);
    }

    @SuppressWarnings("unchecked")
    private Collection<V> collection(K key) {
        C collection = map.get(key);
        if (collection == null) {
            collection = Utilities.newInstance(collectionType.getName(), collectionType);
            map.put(key, collection);
        }
        return (Collection<V>) collection;
    }
    
    public void add(K key, V value) {
        collection(key).add(value);
    }
    
    public Collection<V> getValues (K key) {
        return collection(key);
    }
    
    public Set<Entry<K, C>> entries () {
        return map.entrySet();
    }
    
    public void clear () {
        map.clear();
    }

    public Set<K> keySet() {
        return map.keySet();
    }
    
    public Set<Entry<K, C>> entrySet () {
        return map.entrySet();
    }

}
