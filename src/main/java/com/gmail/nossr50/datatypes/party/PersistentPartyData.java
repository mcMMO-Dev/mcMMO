package com.gmail.nossr50.datatypes.party;

import com.gmail.nossr50.datatypes.dirtydata.DirtyData;
import com.gmail.nossr50.datatypes.dirtydata.DirtyDataSet;
import com.gmail.nossr50.datatypes.mutableprimitives.MutableBoolean;
import com.gmail.nossr50.datatypes.mutableprimitives.MutableString;
import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class PersistentPartyData {

    private final @NotNull MutableBoolean dirtyFlag; //Dirty values in this class will change this flag as needed
    private final @NotNull DirtyData<MutableString> partyName;
    private final @NotNull DirtyDataSet<PartyMember> partyMembers; //TODO: Add cache for subsets

    public PersistentPartyData(@NotNull String partyName,
                               @NotNull Set<PartyMember> partyMembers) {
        dirtyFlag = new MutableBoolean(false);
        this.partyName = new DirtyData<>(new MutableString(partyName), dirtyFlag);
        this.partyMembers = new DirtyDataSet<>(new HashSet<>(partyMembers), dirtyFlag);
    }

    public String getPartyName() {
        return partyName.getData().getImmutableCopy();
    }

    public Set<PartyMember> getPartyMembers() {
        return partyMembers.getDataSet();
    }

    public boolean isDataDirty() {
        return dirtyFlag.getImmutableCopy();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersistentPartyData that = (PersistentPartyData) o;
        return Objects.equal(getPartyName(), that.getPartyName()) &&
                Objects.equal(getPartyMembers(), that.getPartyMembers());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getPartyName(), getPartyMembers());
    }
}
