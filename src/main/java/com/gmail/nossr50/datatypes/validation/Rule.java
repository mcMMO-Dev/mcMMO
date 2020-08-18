package com.gmail.nossr50.datatypes.validation;

public abstract class Rule<T> {
    public abstract void applyRule(T object) throws Exception;
}
