package com.gmail.nossr50.datatypes.skills;

import com.google.common.collect.ImmutableSet;
import com.neetgames.mcmmo.skill.SkillIdentity;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class CoreSkillConstants {

    private static final @NotNull ImmutableSet<CoreRootSkill> immutableCoreRootSkillSet;

    public static @NotNull CoreRootSkill ACROBATICS, ALCHEMY, ARCHERY, AXES, EXCAVATION,
            FISHING, HERBALISM, MINING, REPAIR, SALVAGE, SMELTING, SWORDS, TAMING, UNARMED,
            WOODCUTTING, TRIDENTS, CROSSBOWS;

    static {
        HashSet<CoreRootSkill> rootSkillSet = new HashSet<>();

        ACROBATICS = new CoreRootSkill("acrobatics");
        ALCHEMY = new CoreRootSkill("alchemy");
        ARCHERY = new CoreRootSkill("archery");
        AXES = new CoreRootSkill("axes");
        EXCAVATION = new CoreRootSkill("excavation");
        FISHING = new CoreRootSkill("fishing");
        HERBALISM = new CoreRootSkill("herbalism");
        MINING = new CoreRootSkill("mining");
        REPAIR = new CoreRootSkill("repair");
        SALVAGE = new CoreRootSkill("salvage");
        SMELTING = new CoreRootSkill("smelting");
        SWORDS = new CoreRootSkill("swords");
        TAMING = new CoreRootSkill("taming");
        UNARMED = new CoreRootSkill("unarmed");
        WOODCUTTING = new CoreRootSkill("woodcutting");
        TRIDENTS = new CoreRootSkill("tridents");
        CROSSBOWS = new CoreRootSkill("crossbows");

        rootSkillSet.add(ACROBATICS);
        rootSkillSet.add(ALCHEMY);
        rootSkillSet.add(ARCHERY);
        rootSkillSet.add(AXES);
        rootSkillSet.add(EXCAVATION);
        rootSkillSet.add(FISHING);
        rootSkillSet.add(HERBALISM);
        rootSkillSet.add(MINING);
        rootSkillSet.add(REPAIR);
        rootSkillSet.add(SALVAGE);
        rootSkillSet.add(SMELTING);
        rootSkillSet.add(SWORDS);
        rootSkillSet.add(TAMING);
        rootSkillSet.add(UNARMED);
        rootSkillSet.add(WOODCUTTING);
        rootSkillSet.add(TRIDENTS);
        rootSkillSet.add(CROSSBOWS);

        immutableCoreRootSkillSet = ImmutableSet.copyOf(rootSkillSet);
    }

    /**
     * Returns a set of built in skills for mcMMO
     * No guarantees for whether or not the skills are registered or active or inactive
     *
     * @return a set of all root skills built into mcMMO
     */
    public static @NotNull Set<CoreRootSkill> getImmutableCoreRootSkillSet() {
        return immutableCoreRootSkillSet;
    }
}
