package com.gmail.nossr50.api.exceptions;

import java.io.Serial;

public class InvalidXPGainReasonException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 4427052841957931157L;

    public InvalidXPGainReasonException() {
        super("That is not a valid XPGainReason.");
    }
}
