package com.gmail.nossr50.datatypes.dirtydata;

import com.gmail.nossr50.datatypes.mutableprimitives.MutableBoolean;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DirtyDataMap<K, V> {

    private final @NotNull MutableBoolean dirtyFlag; //Can be pointed at a reference
    private @NotNull Map<K, V> dataMap;

    public DirtyDataMap(@NotNull Map<K, V> data, @NotNull MutableBoolean referenceFlag) {
        this.dataMap = data;
        this.dirtyFlag = referenceFlag;
    }

    public boolean isDirty() {
        return dirtyFlag.getImmutableCopy();
    }

    private void setDirty() {
        dirtyFlag.setBoolean(true);
    }

    public void setData(@NotNull Map<K, V> dataMap) {
        this.dataMap = dataMap;
        setDirty();
    }

    public @NotNull Map<K, V> getDataMap() {
        setDirty();
        return dataMap;
    }

    /* Map Interface Delegates */

    public V get(K key) {
        return dataMap.get(key);
    }

    public int size() {
        return dataMap.size();
    }

    public boolean isEmpty() {
        return dataMap.isEmpty();
    }

    public boolean containsKey(K key) {
        return dataMap.containsKey(key);
    }

    public boolean containsValue(V value) {
        return dataMap.containsValue(value);
    }

    public V put(K key, V value) {
        setDirty();
        return dataMap.put(key, value);
    }

    public V remove(K key) {
        setDirty();
        return dataMap.remove(key);
    }

    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        setDirty();
        dataMap.putAll(m);
    }

    public void clear() {
        setDirty();
        dataMap.clear();
    }

    public Set<K> keySet() {
        setDirty();
        return dataMap.keySet();
    }

    public Collection<V> values() {
        setDirty();
        return dataMap.values();
    }

    public Set<Map.Entry<K, V>> entrySet() {
        setDirty();
        return dataMap.entrySet();
    }

    public V getOrDefault(K key, V defaultValue) {
        return dataMap.getOrDefault(key, defaultValue);
    }

    public void forEach(BiConsumer<? super K, ? super V> action) {
        setDirty();
        dataMap.forEach(action);
    }

    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        setDirty();
        dataMap.replaceAll(function);
    }

    public V putIfAbsent(K key, V value) {
        setDirty();
        return dataMap.putIfAbsent(key, value);
    }

    public boolean remove(K key, V value) {
        setDirty();
        return dataMap.remove(key, value);
    }

    public boolean replace(K key, V oldValue, V newValue) {
        setDirty();
        return dataMap.replace(key, oldValue, newValue);
    }

    public V replace(K key, V value) {
        setDirty();
        return dataMap.replace(key, value);
    }

    public V computeIfAbsent(K key, @NotNull Function<? super K, ? extends V> mappingFunction) {
        return dataMap.computeIfAbsent(key, mappingFunction);
    }

    public V computeIfPresent(K key, @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return dataMap.computeIfPresent(key, remappingFunction);
    }

    public V compute(K key, @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return dataMap.compute(key, remappingFunction);
    }

    public V merge(K key, @NotNull V value, @NotNull BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        setDirty();
        return dataMap.merge(key, value, remappingFunction);
    }
}
