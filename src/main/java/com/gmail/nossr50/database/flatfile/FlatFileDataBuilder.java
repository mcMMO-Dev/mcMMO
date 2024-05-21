package com.gmail.nossr50.database.flatfile;

import com.gmail.nossr50.database.FlatFileDataFlag;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class FlatFileDataBuilder {
    private final @NotNull HashSet<FlatFileDataFlag> dataFlags;
    private @NotNull String[] splitStringData;
    private final int uniqueProcessingId;
    private boolean[] badDataValues;

    public FlatFileDataBuilder(@NotNull String[] splitStringData, int uniqueProcessingId) {
        this.uniqueProcessingId = uniqueProcessingId;
        this.splitStringData = splitStringData;
        dataFlags = new HashSet<>();
    }

    public @NotNull FlatFileDataBuilder appendFlag(@NotNull FlatFileDataFlag dataFlag) {
        dataFlags.add(dataFlag);
        return this;
    }

    public @NotNull FlatFileDataBuilder appendBadDataValues(boolean[] badDataValues) {
        this.badDataValues = badDataValues;
        return this;
    }

    public @NotNull FlatFileDataContainer build() {
        if (dataFlags.contains(FlatFileDataFlag.BAD_VALUES)) {
            return new BadCategorizedFlatFileData(uniqueProcessingId, dataFlags, splitStringData, badDataValues);
        }

        return new CategorizedFlatFileData(uniqueProcessingId, dataFlags, splitStringData);
    }

    public @NotNull FlatFileDataBuilder setSplitStringData(@NotNull String[] splitStringData) {
        this.splitStringData = splitStringData;
        return this;
    }
}
