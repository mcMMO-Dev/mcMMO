package com.gmail.nossr50.skills.child;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.gmail.nossr50.datatypes.skills.SkillType;

public class FamilyTree {
    private static HashMap<SkillType, Set<SkillType>> tree = new HashMap<SkillType, Set<SkillType>>();

    public static Set<SkillType> getParents(SkillType childSkill) {
        enforceChildSkill(childSkill);

        // We do not check if we have the child skill in question, as not having it would mean we did something wrong, and an NPE is desired.
        return tree.get(childSkill);
    }

    protected static void registerParent(SkillType childSkill, SkillType parentSkill) {
        enforceChildSkill(childSkill);
        enforceNotChildSkill(parentSkill);

        if (!tree.containsKey(childSkill)) {
            tree.put(childSkill, new HashSet<SkillType>());
        }

        tree.get(childSkill).add(parentSkill);
    }

    protected static void closeRegistration() {
        for (SkillType childSkill : tree.keySet()) {
            Set<SkillType> immutableSet = Collections.unmodifiableSet(tree.get(childSkill));
            tree.put(childSkill, immutableSet);
        }
    }

    protected static void clearRegistrations() {
        tree.clear();
    }

    protected static void enforceChildSkill(SkillType skill) {
        if (!skill.isChildSkill()) {
            throw new IllegalArgumentException(skill.getName() + " is not a child skill!");
        }
    }

    protected static void enforceNotChildSkill(SkillType skill) {
        if (skill.isChildSkill()) {
            throw new IllegalArgumentException(skill.getName() + " is a child skill!");
        }
    }
}
