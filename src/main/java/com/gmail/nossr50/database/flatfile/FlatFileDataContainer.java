package com.gmail.nossr50.database.flatfile;

import com.gmail.nossr50.database.FlatFileDataFlag;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface FlatFileDataContainer {
    default @Nullable Set<FlatFileDataFlag> getDataFlags() {
        return null;
    }

    @NotNull String[] getSplitData();

    int getUniqueProcessingId();

    default boolean isHealthyData() {
        return getDataFlags() == null || getDataFlags().size() == 0;
    }
}
