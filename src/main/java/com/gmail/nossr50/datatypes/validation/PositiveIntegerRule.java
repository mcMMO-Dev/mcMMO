package com.gmail.nossr50.datatypes.validation;

import com.neetgames.mcmmo.exceptions.UnexpectedValueException;

public class PositiveIntegerRule<T extends Number> extends Rule<T> {
    @Override
    public void applyRule(T number) throws UnexpectedValueException {
        if(number.intValue() < 0)
            throw new UnexpectedValueException();
    }
}
