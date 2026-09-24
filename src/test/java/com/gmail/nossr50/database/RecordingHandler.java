package com.gmail.nossr50.database;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

/** Keeps what is logged to it, so a test can check what a database manager logged. */
final class RecordingHandler extends Handler {
    private final List<LogRecord> records = new CopyOnWriteArrayList<>();

    @Override
    public void publish(LogRecord record) {
        records.add(record);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    /**
     * A logger of its own that records here only. The shared loggers can carry filters other
     * tests left on them.
     */
    @NotNull Logger newLogger() {
        final Logger logger = Logger.getAnonymousLogger();
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);
        logger.addHandler(this);
        return logger;
    }

    List<String> messagesAt(@NotNull Level level) {
        return records.stream()
                .filter(record -> record.getLevel().equals(level))
                .map(LogRecord::getMessage)
                .toList();
    }
}
