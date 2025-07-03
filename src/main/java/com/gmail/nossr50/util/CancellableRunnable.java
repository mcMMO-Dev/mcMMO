package com.gmail.nossr50.util;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import java.util.function.Consumer;

public abstract class CancellableRunnable implements Consumer<WrappedTask> {
    private boolean cancelled = false;

    public void cancel() {
        cancelled = true;
    }

    public abstract void run();

    @Override
    public void accept(WrappedTask wrappedTask) {
        // Run the task if it hasn't been cancelled
        if (!cancelled) {
            run();
        }

        // Cancel the task if it has been cancelled after running
        if (cancelled) {
            wrappedTask.cancel();
        }
    }
}
