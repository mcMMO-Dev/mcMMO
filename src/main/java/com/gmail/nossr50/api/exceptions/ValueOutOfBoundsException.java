package com.gmail.nossr50.api.exceptions;

import org.jetbrains.annotations.NotNull;

public class ValueOutOfBoundsException extends RuntimeException {
    public ValueOutOfBoundsException(@NotNull String message) {
        super(message);
    }
}
