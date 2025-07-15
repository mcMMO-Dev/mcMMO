package com.gmail.nossr50.database.flatfile;

import com.gmail.nossr50.database.FlatFileDataFlag;
import com.google.common.base.Objects;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class CategorizedFlatFileData implements FlatFileDataContainer {
    private final @NotNull Set<FlatFileDataFlag> dataFlags;
    private final @NotNull String[] splitData;
    private final int uniqueProcessingId;

    public CategorizedFlatFileData(int uniqueProcessingId,
            @NotNull HashSet<FlatFileDataFlag> dataFlags, @NotNull String[] splitData) {
        this.uniqueProcessingId = uniqueProcessingId;
        this.dataFlags = dataFlags;
        this.splitData = splitData;
    }

    public @NotNull Set<FlatFileDataFlag> getDataFlags() {
        return dataFlags;
    }

    public @NotNull String[] getSplitData() {
        return splitData;
    }

    public int getUniqueProcessingId() {
        return uniqueProcessingId;
    }

    public boolean isHealthyData() {
        return dataFlags.size() == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CategorizedFlatFileData that = (CategorizedFlatFileData) o;
        return uniqueProcessingId == that.uniqueProcessingId && Objects.equal(dataFlags,
                that.dataFlags) && Objects.equal(splitData, that.splitData);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(dataFlags, splitData, uniqueProcessingId);
    }

    @Override
    public String toString() {
        return "CategorizedFlatFileData{" +
                "dataFlags=" + dataFlags +
                ", stringDataRepresentation='" + splitData + '\'' +
                ", uniqueProcessingId=" + uniqueProcessingId +
                '}';
    }
}
