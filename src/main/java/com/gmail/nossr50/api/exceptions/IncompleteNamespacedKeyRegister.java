package com.gmail.nossr50.api.exceptions;

import javax.annotation.Nonnull;

public class IncompleteNamespacedKeyRegister extends RuntimeException {
    private static final long serialVersionUID = -6905157273569301219L;

    public IncompleteNamespacedKeyRegister(@Nonnull String message) {
        super(message);
    }
}
