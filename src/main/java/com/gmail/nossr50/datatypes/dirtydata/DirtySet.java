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

public class DirtySet<E> implements Set<E> {

    private final @NotNull MutableBoolean dirtyFlag; //Can be pointed at a reference
    private @NotNull Set<E> set;

    public DirtySet(@NotNull Set<E> data, @NotNull MutableBoolean referenceFlag) {
        this.set = data;
        this.dirtyFlag = referenceFlag;
    }

    public boolean isDirty() {
        return dirtyFlag.getImmutableCopy();
    }

    private void setDirty() {
        dirtyFlag.setBoolean(true);
    }

    /**
     * Assign the inner wrapped set to a new value
     * @param dataSet the new value to assign the inner wrapped set
     */
    public void setSet(@NotNull Set<E> dataSet) {
        this.set = dataSet;
        setDirty();
    }

    /**
     * Get the wrapped set of this DirtySet
     * @return the inner wrapped Set of this DirtySet
     */
    public @NotNull Set<E> unwrapSet() {
        setDirty();
        return set;
    }

    /* Set Interface Delegates */

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return set.contains(o);
    }

    @Override
    public @NotNull Iterator<E> iterator() {
        return set.iterator();
    }

    @Override
    public Object[] toArray() {
        return set.toArray();
    }

    @Override
    public <T> T[] toArray(@NotNull T[] ts) {
        return set.toArray(ts);
    }

    @Override
    public boolean add(E e) {
        setDirty();
        return set.add(e);
    }

    @Override
    public boolean remove(Object o) {
        setDirty();
        return set.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> collection) {
        return set.containsAll(collection);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> collection) {
        setDirty();
        return set.addAll(collection);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> collection) {
        setDirty();
        return set.retainAll(collection);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> collection) {
        setDirty();
        return set.removeAll(collection);
    }

    @Override
    public void clear() {
        setDirty();
        set.clear();
    }

    @Override
    public Spliterator<E> spliterator() {
        setDirty();
        return set.spliterator();
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return set.removeIf(filter);
    }

    @Override
    public Stream<E> stream() {
        return set.stream();
    }

    @Override
    public Stream<E> parallelStream() {
        return set.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        set.forEach(action);
    }

    /* Equals & Hash Overrides */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DirtySet<?> dirtySet = (DirtySet<?>) o;
        return Objects.equal(set, dirtySet.set);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(set);
    }
}
