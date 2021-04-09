package com.gmail.nossr50.database.flatfile;

import com.gmail.nossr50.database.FlatFileDataContainer;
import com.gmail.nossr50.database.FlatFileDataFlag;
import com.gmail.nossr50.database.FlatFileDatabaseManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class CategorizedFlatFileData implements FlatFileDataContainer {
    private final @NotNull Set<FlatFileDataFlag> dataFlags;
    private final @NotNull String stringDataRepresentation;
    private final int uniqueProcessingId;
    private final boolean[] badDataIndexes;

    protected CategorizedFlatFileData(int uniqueProcessingId, @NotNull HashSet<FlatFileDataFlag> dataFlags, @NotNull String stringDataRepresentation) {
        this.uniqueProcessingId = uniqueProcessingId;
        this.dataFlags = dataFlags;
        this.stringDataRepresentation = stringDataRepresentation;
        badDataIndexes = new boolean[FlatFileDatabaseManager.DATA_ENTRY_COUNT];
    }

    protected CategorizedFlatFileData(int uniqueProcessingId, @NotNull HashSet<FlatFileDataFlag> dataFlags, @NotNull String stringDataRepresentation, boolean[] badDataIndexes) {
        this.uniqueProcessingId = uniqueProcessingId;
        this.dataFlags = dataFlags;
        this.stringDataRepresentation = stringDataRepresentation;
        this.badDataIndexes = badDataIndexes;
    }

    public @NotNull Set<FlatFileDataFlag> getDataFlags() {
        return dataFlags;
    }

    public @NotNull String getStringDataRepresentation() {
        return stringDataRepresentation;
    }

    public int getUniqueProcessingId() {
        return uniqueProcessingId;
    }

    public boolean isHealthyData() {
        return dataFlags.size() == 0;
    }

    public boolean[] getBadDataIndexes() {
        return badDataIndexes;
    }
}
