package com.gmail.nossr50.database.flatfile;

import com.gmail.nossr50.database.FlatFileDataFlag;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class CategorizedFlatFileDataBuilder {
    private final @NotNull HashSet<FlatFileDataFlag> dataFlags;
    private @NotNull String stringDataRepresentation;
    private final int uniqueProcessingId;

    public CategorizedFlatFileDataBuilder(@NotNull String stringDataRepresentation, int uniqueProcessingId) {
        this.uniqueProcessingId = uniqueProcessingId;
        this.stringDataRepresentation = stringDataRepresentation;
        dataFlags = new HashSet<>();
    }

    public CategorizedFlatFileDataBuilder appendFlag(@NotNull FlatFileDataFlag dataFlag) {
        dataFlags.add(dataFlag);
        return this;
    }

    public CategorizedFlatFileData build() {
        return new CategorizedFlatFileData(uniqueProcessingId, dataFlags, stringDataRepresentation);
    }

    public CategorizedFlatFileDataBuilder setStringDataRepresentation(@NotNull String stringDataRepresentation) {
        this.stringDataRepresentation = stringDataRepresentation;
        return this;
    }
}
