package com.gmail.nossr50.datatypes.validation;

import com.gmail.nossr50.api.exceptions.UnexpectedValueException;

public class PositiveIntegerRule<T extends Number> extends Rule<T> {
    @Override
    public void applyRule(T number) throws Exception {
        if(number.intValue() < 0)
            throw new UnexpectedValueException();
    }
}
