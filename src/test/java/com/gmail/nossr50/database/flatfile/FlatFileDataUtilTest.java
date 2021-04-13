package com.gmail.nossr50.database.flatfile;

import com.gmail.nossr50.database.FlatFileDatabaseManager;
import org.junit.Test;

import java.util.HashSet;

public class FlatFileDataUtilTest {

    @Test
    public void getPreparedSaveDataLine() {
    }

    @Test
    public void repairBadData() {
    }

    @Test
    public void getZeroInitialisedData() {
    }

    @Test(expected = AssertionError.class)
    public void testTooManyDataEntriesSplitString() {
        FlatFileDataContainer dataContainer = new CategorizedFlatFileData(0, new HashSet<>(), new String[FlatFileDatabaseManager.DATA_ENTRY_COUNT + 1]);
        FlatFileDataUtil.getPreparedSaveDataLine(dataContainer);
    }
}