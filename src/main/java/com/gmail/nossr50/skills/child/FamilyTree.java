package com.gmail.nossr50.skills.child;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.Set;

public class FamilyTree {
    static final ImmutableSet<PrimarySkillType> salvageTree;
    static final ImmutableSet<PrimarySkillType> smeltingTree;

    static {
        ArrayList<PrimarySkillType> salvageParentsList = new ArrayList<>();
        ArrayList<PrimarySkillType> smeltingParentsList = new ArrayList<>();

        salvageParentsList.add(PrimarySkillType.FISHING);
        salvageParentsList.add(PrimarySkillType.REPAIR);

        smeltingParentsList.add(PrimarySkillType.MINING);
        smeltingParentsList.add(PrimarySkillType.REPAIR);

        salvageTree = ImmutableSet.copyOf(salvageParentsList);
        smeltingTree = ImmutableSet.copyOf(smeltingParentsList);
    }

    public static Set<PrimarySkillType> getParents(PrimarySkillType childSkill) {
        if(childSkill == PrimarySkillType.SALVAGE)
            return salvageTree;
        else
            return smeltingTree;
    }
}
