package com.gmail.nossr50.datatypes.dirtydata;

import com.gmail.nossr50.datatypes.mutableprimitives.MutableBoolean;
import org.jetbrains.annotations.NotNull;

public class DirtyData<T> {

    private final @NotNull MutableBoolean dirtyFlag; //Can be pointed at a reference
    private @NotNull T data;


    public DirtyData(@NotNull T data, @NotNull MutableBoolean referenceFlag) {
        this.data = data;
        this.dirtyFlag = referenceFlag;
    }

    public boolean isDirty() {
        return dirtyFlag.getImmutableCopy();
    }

    public void setDirty(boolean newDirtyValue) {
        dirtyFlag.setBoolean(newDirtyValue);
    }

    public @NotNull T getData() {
        return data;
    }

    public T getData(boolean newDirty) {
        setDirty(newDirty);
        return data;
    }

    public void setData(@NotNull T data) {
        this.data = data;
        setDirty(true);
    }
}
