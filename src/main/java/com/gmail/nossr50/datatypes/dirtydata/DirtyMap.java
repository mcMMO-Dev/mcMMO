package com.gmail.nossr50.datatypes.dirtydata;

import com.gmail.nossr50.datatypes.mutableprimitives.MutableBoolean;
import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DirtyMap<K, V> implements Map<K, V> {

    private final @NotNull MutableBoolean dirtyFlag; //Can be pointed at a reference
    private @NotNull Map<K, V> map;

    public DirtyMap(@NotNull Map<K, V> data, @NotNull MutableBoolean referenceFlag) {
        this.map = data;
        this.dirtyFlag = referenceFlag;
    }

    public boolean isDirty() {
        return dirtyFlag.getImmutableCopy();
    }

    private void setDirty() {
        dirtyFlag.setBoolean(true);
    }

    /**
     * Change the map contained in this wrapper
     * @param dataMap the map to wrap around instead of the current map
     */
    public void setMap(@NotNull Map<K, V> dataMap) {
        this.map = dataMap;
        setDirty();
    }

    /**
     * Get the inner map that this DirtyMap is wrapping
     * @return the inner map of this DirtyMap
     */
    public @NotNull Map<K, V> unwrapMap() {
        setDirty();
        return map;
    }

    /* Map Interface Delegates */

    @Override
    public V get(Object key) {
        return map.get(key);
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public V put(K key, V value) {
        setDirty();
        return map.put(key, value);
    }

    public V remove(Object key) {
        setDirty();
        return map.remove(key);
    }

    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        setDirty();
        map.putAll(m);
    }

    public void clear() {
        setDirty();
        map.clear();
    }

    @Override
    public @NotNull Set<K> keySet() {
        setDirty();
        return map.keySet();
    }

    @Override
    public @NotNull Collection<V> values() {
        setDirty();
        return map.values();
    }

    @Override
    public @NotNull Set<Map.Entry<K, V>> entrySet() {
        setDirty();
        return map.entrySet();
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        setDirty();
        map.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        setDirty();
        map.replaceAll(function);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        setDirty();
        return map.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        setDirty();
        return map.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        setDirty();
        return map.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        setDirty();
        return map.replace(key, value);
    }

    @Override
    public V computeIfAbsent(K key, @NotNull Function<? super K, ? extends V> mappingFunction) {
        setDirty();
        return map.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(K key, @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        setDirty();
        return map.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(K key, @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        setDirty();
        return map.compute(key, remappingFunction);
    }

    @Override
    public V merge(K key, @NotNull V value, @NotNull BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        setDirty();
        return map.merge(key, value, remappingFunction);
    }

    /* Override for equals and hash */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DirtyMap<?, ?> that = (DirtyMap<?, ?>) o;
        return Objects.equal(map, that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(map);
    }
}
