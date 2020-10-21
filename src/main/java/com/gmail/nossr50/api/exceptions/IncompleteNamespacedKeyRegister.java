package com.gmail.nossr50.api.exceptions;

public class IncompleteNamespacedKeyRegister extends RuntimeException {
    private static final long serialVersionUID = -6905157273569301219L;

    public IncompleteNamespacedKeyRegister(String message) {
        super(message);
    }
}
