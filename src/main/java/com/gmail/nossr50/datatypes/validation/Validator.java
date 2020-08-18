package com.gmail.nossr50.datatypes.validation;

import java.util.ArrayList;
import java.util.List;

public class Validator<T> implements Validates<T> {
    private List<Rule<T>> rulesList;

    public Validator() {
        this.rulesList = new ArrayList<>();
    }

    @Override
    public T validate(T object) throws Exception {
        for(Rule<T> rule : rulesList) {
            rule.applyRule(object);
        }

        return object;
    }

    public void addRule(Rule<T> newRule) {
        rulesList.add(newRule);
    }
}
