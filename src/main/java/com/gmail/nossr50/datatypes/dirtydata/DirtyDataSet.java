package com.gmail.nossr50.datatypes.dirtydata;

import com.gmail.nossr50.datatypes.mutableprimitives.MutableBoolean;
import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DirtyDataSet<E> {

    private final @NotNull MutableBoolean dirtyFlag; //Can be pointed at a reference
    private @NotNull Set<E> dataSet;

    public DirtyDataSet(@NotNull Set<E> data, @NotNull MutableBoolean referenceFlag) {
        this.dataSet = data;
        this.dirtyFlag = referenceFlag;
    }

    public boolean isDirty() {
        return dirtyFlag.getImmutableCopy();
    }

    private void setDirty() {
        dirtyFlag.setBoolean(true);
    }

    public void setData(@NotNull Set<E> dataSet) {
        this.dataSet = dataSet;
        setDirty();
    }

    public @NotNull Set<E> getDataSet() {
        setDirty();
        return dataSet;
    }

    /* Set Interface Delegates */

    public int size() {
        return dataSet.size();
    }

    public boolean isEmpty() {
        return dataSet.isEmpty();
    }

    public boolean contains(Object o) {
        return dataSet.contains(o);
    }

    public Iterator<E> iterator() {
        return dataSet.iterator();
    }

    public Object[] toArray() {
        return dataSet.toArray();
    }

    public <T> T[] toArray(@NotNull T[] ts) {
        return dataSet.toArray(ts);
    }

    public boolean add(E e) {
        return dataSet.add(e);
    }

    public boolean remove(Object o) {
        return dataSet.remove(o);
    }

    public boolean containsAll(@NotNull Collection<? extends E> collection) {
        return dataSet.containsAll(collection);
    }

    public boolean addAll(@NotNull Collection<? extends E> collection) {
        return dataSet.addAll(collection);
    }

    public boolean retainAll(@NotNull Collection<? extends E> collection) {
        return dataSet.retainAll(collection);
    }

    public boolean removeAll(@NotNull Collection<? extends E> collection) {
        return dataSet.removeAll(collection);
    }

    public void clear() {
        dataSet.clear();
    }

    public Spliterator<E> spliterator() {
        return dataSet.spliterator();
    }

    public boolean removeIf(Predicate<? super E> filter) {
        return dataSet.removeIf(filter);
    }

    public Stream<E> stream() {
        return dataSet.stream();
    }

    public Stream<E> parallelStream() {
        return dataSet.parallelStream();
    }

    public void forEach(Consumer<? super E> action) {
        dataSet.forEach(action);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DirtyDataSet<?> that = (DirtyDataSet<?>) o;
        return Objects.equal(getDataSet(), that.getDataSet());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getDataSet());
    }
}
