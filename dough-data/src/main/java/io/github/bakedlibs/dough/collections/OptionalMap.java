package io.github.bakedlibs.dough.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class OptionalMap<K, V> implements Iterable<Map.Entry<K, V>>, Streamable<Entry<K, V>> {
    private final Map<K, V> internalMap;

    public OptionalMap(Supplier<? extends Map<K, V>> constructor) {
        internalMap = constructor.get();

        if (internalMap == null) {
            throw new IllegalStateException("Internal Map is not allowed to be null!");
        }
    }

    /**
     * This method returns the size of this Map.
     * 
     * @return The size of our Map
     */
    public int size() {
        return internalMap.size();
    }

    public boolean isEmpty() {
        return internalMap.isEmpty();
    }

    public Optional<V> get(K key) {
        return Optional.ofNullable(internalMap.get(key));
    }

    public boolean containsKey(K key) {
        return get(key).isPresent();
    }

    public boolean containsValue(V value) {
        return internalMap.containsValue(value);
    }

    public void ifPresent(K key, Consumer<? super V> consumer) {
        get(key).ifPresent(consumer);
    }

    public void ifAbsent(K key, Consumer<Void> consumer) {
        if (!containsKey(key))
            consumer.accept(null);
    }

    public Optional<V> put(K key, V value) {
        return Optional.ofNullable(internalMap.put(key, value));
    }

    public Optional<V> remove(K key) {
        return Optional.ofNullable(internalMap.remove(key));
    }

    public void putAll(Map<? extends K, ? extends V> map) {
        internalMap.putAll(map);
    }

    public void clear() {
        internalMap.clear();
    }

    public Set<K> keySet() {
        return internalMap.keySet();
    }

    public Collection<V> values() {
        return internalMap.values();
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return internalMap.entrySet();
    }

    @Override
    public boolean equals(Object obj) {
        return internalMap.equals(obj);
    }

    @Override
    public int hashCode() {
        return internalMap.hashCode();
    }

    public V getOrDefault(K key, V defaultValue) {
        return internalMap.getOrDefault(key, defaultValue);
    }

    public void forEach(BiConsumer<? super K, ? super V> consumer) {
        internalMap.forEach(consumer);
    }

    public V putIfAbsent(K key, V value) {
        return internalMap.putIfAbsent(key, value);
    }

    public V computeIfAbsent(K key, Function<? super K, ? extends V> function) {
        return internalMap.computeIfAbsent(key, function);
    }

    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> function) {
        return internalMap.computeIfPresent(key, function);
    }

    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> function) {
        return internalMap.compute(key, function);
    }

    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> function) {
        return internalMap.merge(key, value, function);
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return entrySet().iterator();
    }

    @Override
    public Stream<Entry<K, V>> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    public Map<K, V> getInternalMap() {
        return internalMap;
    }
}
