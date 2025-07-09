package com.gmail.nossr50.api.exceptions;

import java.io.Serial;
import org.jetbrains.annotations.NotNull;

public class IncompleteNamespacedKeyRegister extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -6905157273569301219L;

    public IncompleteNamespacedKeyRegister(@NotNull String message) {
        super(message);
    }
}
