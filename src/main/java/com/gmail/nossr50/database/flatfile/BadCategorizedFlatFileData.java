package com.gmail.nossr50.database.flatfile;

import com.gmail.nossr50.database.FlatFileDataFlag;
import com.google.common.base.Objects;
import java.util.Arrays;
import java.util.HashSet;
import org.jetbrains.annotations.NotNull;

public class BadCategorizedFlatFileData extends CategorizedFlatFileData {
    private final boolean[] badDataIndexes;

    protected BadCategorizedFlatFileData(int uniqueProcessingId,
            @NotNull HashSet<FlatFileDataFlag> dataFlags, @NotNull String[] splitData,
            boolean[] badDataIndexes) {
        super(uniqueProcessingId, dataFlags, splitData);
        this.badDataIndexes = badDataIndexes;
    }

    public boolean[] getBadDataIndexes() {
        return badDataIndexes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        BadCategorizedFlatFileData that = (BadCategorizedFlatFileData) o;
        return Objects.equal(badDataIndexes, that.badDataIndexes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), badDataIndexes);
    }

    @Override
    public String toString() {
        return "BadCategorizedFlatFileData{" +
                "badDataIndexes=" + Arrays.toString(badDataIndexes) +
                '}';
    }
}
