package com.gmail.nossr50.database.flatfile;

import com.gmail.nossr50.database.FlatFileDatabaseManager;
import java.util.HashSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FlatFileDataUtilTest {

    @Test
    void getPreparedSaveDataLine() {

    }

    @Test
    void repairBadData() {

    }

    @Test
    void getZeroInitialisedData() {

    }

    @Test
    void testTooManyDataEntriesSplitString() {
        Assertions.assertThrows(AssertionError.class, () -> {
            FlatFileDataContainer dataContainer = new CategorizedFlatFileData(0, new HashSet<>(),
                    new String[FlatFileDatabaseManager.DATA_ENTRY_COUNT + 1]);
            FlatFileDataUtil.getPreparedSaveDataLine(dataContainer);
        });
    }
}
