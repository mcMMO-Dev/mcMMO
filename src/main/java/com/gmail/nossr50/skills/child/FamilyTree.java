package com.gmail.nossr50.skills.child;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.skills.SkillTools;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;

public class FamilyTree {
    private static final HashMap<PrimarySkillType, Set<PrimarySkillType>> tree = new HashMap<>();

    public static Set<PrimarySkillType> getParents(PrimarySkillType childSkill) {
        enforceChildSkill(childSkill);

        // We do not check if we have the child skill in question, as not having it would mean we did something wrong, and an NPE is desired.
        return tree.get(childSkill);
    }

    protected static void registerParent(PrimarySkillType childSkill, PrimarySkillType parentSkill) {
        enforceChildSkill(childSkill);
        enforceNotChildSkill(parentSkill);

        if (!tree.containsKey(childSkill)) {
            tree.put(childSkill, EnumSet.noneOf(PrimarySkillType.class));
        }

        tree.get(childSkill).add(parentSkill);
    }

    protected static void closeRegistration() {
        for (PrimarySkillType childSkill : tree.keySet()) {
            Set<PrimarySkillType> immutableSet = Collections.unmodifiableSet(tree.get(childSkill));
            tree.put(childSkill, immutableSet);
        }
    }

    protected static void clearRegistrations() {
        tree.clear();
    }

    protected static void enforceChildSkill(PrimarySkillType skill) {
        if (!SkillTools.isChildSkill(skill)) {
            throw new IllegalArgumentException(skill.name() + " is not a child skill!");
        }
    }

    protected static void enforceNotChildSkill(PrimarySkillType skill) {
        if (SkillTools.isChildSkill(skill)) {
            throw new IllegalArgumentException(skill.name() + " is a child skill!");
        }
    }
}
