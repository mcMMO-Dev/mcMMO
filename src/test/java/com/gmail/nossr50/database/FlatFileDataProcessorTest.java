package com.gmail.nossr50.database;

import org.junit.Test;

public class FlatFileDataProcessorTest {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void testGetExpectedValueType() {
        for(int i = 0; i < FlatFileDatabaseManager.DATA_ENTRY_COUNT; i++) {
            FlatFileDataProcessor.getExpectedValueType(i);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetExpectedValueTypeException() {
        for(int i = 0; i < FlatFileDatabaseManager.DATA_ENTRY_COUNT+1; i++) {
            FlatFileDataProcessor.getExpectedValueType(i);
        }
    }

}