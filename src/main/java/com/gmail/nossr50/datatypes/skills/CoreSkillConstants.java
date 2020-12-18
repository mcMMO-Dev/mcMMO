package com.gmail.nossr50.datatypes.skills;

import com.google.common.collect.ImmutableSet;
import com.neetgames.mcmmo.skill.RootSkill;
import com.neetgames.mcmmo.skill.Skill;
import com.neetgames.mcmmo.skill.SkillIdentity;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class CoreSkillConstants {

    private static final @NotNull ImmutableSet<RootSkill> CORE_ROOT_SKILLS_IMMUTABLE_SET;
    private static final @NotNull ImmutableSet<RootSkill> CORE_CHILD_SKILLS;

    public static final @NotNull CoreRootSkill ACROBATICS, ALCHEMY, ARCHERY, AXES, EXCAVATION,
            FISHING, HERBALISM, MINING, REPAIR, SALVAGE, SMELTING, SWORDS, TAMING, UNARMED,
            WOODCUTTING, TRIDENTS, CROSSBOWS;

    public static final @NotNull SkillIdentity ACROBATICS_ID, ALCHEMY_ID, ARCHERY_ID, AXES_ID, EXCAVATION_ID,
            FISHING_ID, HERBALISM_ID, MINING_ID, REPAIR_ID, SALVAGE_ID, SMELTING_ID, SWORDS_ID, TAMING_ID, UNARMED_ID,
            WOODCUTTING_ID, TRIDENTS_ID, CROSSBOWS_ID;

    static {
        HashSet<CoreRootSkill> rootSkillSet = new HashSet<>();
        HashSet<CoreRootSkill> childSkillSet = new HashSet<>();

        ACROBATICS = new CoreRootSkill("acrobatics");
        ACROBATICS_ID = ACROBATICS.getSkillIdentity();

        ALCHEMY = new CoreRootSkill("alchemy");
        ALCHEMY_ID = ALCHEMY.getSkillIdentity();

        ARCHERY = new CoreRootSkill("archery");
        ARCHERY_ID = ARCHERY.getSkillIdentity();

        AXES = new CoreRootSkill("axes");
        AXES_ID = AXES.getSkillIdentity();

        EXCAVATION = new CoreRootSkill("excavation");
        EXCAVATION_ID = EXCAVATION.getSkillIdentity();

        FISHING = new CoreRootSkill("fishing");
        FISHING_ID = FISHING.getSkillIdentity();

        HERBALISM = new CoreRootSkill("herbalism");
        HERBALISM_ID = HERBALISM.getSkillIdentity();

        MINING = new CoreRootSkill("mining");
        MINING_ID = MINING.getSkillIdentity();

        REPAIR = new CoreRootSkill("repair");
        REPAIR_ID = REPAIR.getSkillIdentity();

        SALVAGE = new CoreRootSkill("salvage");
        SALVAGE_ID = SALVAGE.getSkillIdentity();

        SMELTING = new CoreRootSkill("smelting");
        SMELTING_ID = SMELTING.getSkillIdentity();

        SWORDS = new CoreRootSkill("swords");
        SWORDS_ID = SWORDS.getSkillIdentity();

        TAMING = new CoreRootSkill("taming");
        TAMING_ID = TAMING.getSkillIdentity();

        UNARMED = new CoreRootSkill("unarmed");
        UNARMED_ID = UNARMED.getSkillIdentity();

        WOODCUTTING = new CoreRootSkill("woodcutting");
        WOODCUTTING_ID = WOODCUTTING.getSkillIdentity();

        TRIDENTS = new CoreRootSkill("tridents");
        TRIDENTS_ID = TRIDENTS.getSkillIdentity();

        CROSSBOWS = new CoreRootSkill("crossbows");
        CROSSBOWS_ID = CROSSBOWS.getSkillIdentity();
        
        //Child skills (soon to be removed)
        childSkillSet.add(SMELTING);
        childSkillSet.add(SALVAGE);

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

        CORE_ROOT_SKILLS_IMMUTABLE_SET = ImmutableSet.copyOf(rootSkillSet);
        CORE_CHILD_SKILLS = ImmutableSet.copyOf(childSkillSet);
    }

    /**
     * Returns a set of built in skills for mcMMO
     * No guarantees for whether or not the skills are registered or active or inactive
     *
     * @return a set of all root skills built into mcMMO
     */
    public static @NotNull Set<RootSkill> getImmutableCoreRootSkillSet() {
        return CORE_ROOT_SKILLS_IMMUTABLE_SET;
    }

    /**
     * Returns a set of built in skills for mcMMO which are child skills
     * No guarantees for whether or not the skills are registered or active or inactive
     *
     * @return a set of all "child" root skills for mcMMO
     * @deprecated child skills will be removed in an upcoming update
     */
    @Deprecated
    public static @NotNull Set<RootSkill> getChildSkills() {
        return CORE_CHILD_SKILLS;
    }

    /**
     * Whether or not a skill is considered a child skill
     * @param skillIdentity target skill identity
     * @return true if the skill identity belongs to a core "child" root skill
     */
    public static boolean isChildSkill(@NotNull SkillIdentity skillIdentity) {
        for(RootSkill rootSkill : CORE_CHILD_SKILLS) {
            if(rootSkill.getSkillIdentity().equals(skillIdentity)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Whether or not a skill is considered a child skill
     * @param skill target skill
     * @return true if the skill identity belongs to a core "child" root skill
     */
    public static boolean isChildSkill(@NotNull Skill skill) {
        return isChildSkill(skill.getSkillIdentity());
    }
}
