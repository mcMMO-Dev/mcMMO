package com.gmail.nossr50.database;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FlatFileDataProcessorTest {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void testGetExpectedValueType() {
        for (int i = 0; i < FlatFileDatabaseManager.DATA_ENTRY_COUNT; i++) {
            FlatFileDataProcessor.getExpectedValueType(i);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void testGetExpectedValueTypeException() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            for (int i = 0; i < FlatFileDatabaseManager.DATA_ENTRY_COUNT + 1; i++) {
                FlatFileDataProcessor.getExpectedValueType(i);
            }
        });
    }

}
