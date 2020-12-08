package com.gmail.nossr50.datatypes.party;

import com.gmail.nossr50.datatypes.dirtydata.DirtyData;
import com.gmail.nossr50.datatypes.dirtydata.DirtySet;
import com.gmail.nossr50.datatypes.mutableprimitives.MutableBoolean;
import com.gmail.nossr50.datatypes.mutableprimitives.MutableString;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PersistentPartyData {

    private final @NotNull MutableBoolean dirtyFlag; //Dirty values in this class will change this flag as needed
    private final @NotNull DirtyData<MutableString> partyName;
    private final @NotNull DirtySet<PartyMember> partyMembers; //TODO: Add cache for subsets
    private @NotNull PartyMember partyLeaderRef;

    public PersistentPartyData(@NotNull String partyName,
                               @NotNull Set<PartyMember> partyMembers) {
        dirtyFlag = new MutableBoolean(false);
        this.partyName = new DirtyData<>(new MutableString(partyName), dirtyFlag);
        this.partyMembers = new DirtySet<>(new HashSet<>(partyMembers), dirtyFlag);
        initPartyLeaderRef();
    }

    private void initPartyLeaderRef() {
        for(PartyMember partyMember : getPartyMembers()) {
            if(partyMember.getPartyMemberRank() == PartyMemberRank.LEADER) {
                partyLeaderRef = partyMember;
                break;
            }
        }

        //TODO: Make it impossible for the party leader to be null
        if(partyLeaderRef == null)
            throw new RuntimeException("Party leader is null!");
    }

    public @NotNull PartyMember getPartyLeader() {
        return partyLeaderRef;
    }


    public @NotNull String getPartyName() {
        return partyName.getData().getImmutableCopy();
    }

    public @NotNull Set<PartyMember> getPartyMembers() {
        return partyMembers;
    }

    public boolean isDataDirty() {
        return dirtyFlag.getImmutableCopy();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersistentPartyData that = (PersistentPartyData) o;
        return partyName.equals(that.partyName) && partyMembers.equals(that.partyMembers) && Objects.equals(partyLeaderRef, that.partyLeaderRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partyName, partyMembers, partyLeaderRef);
    }
}
