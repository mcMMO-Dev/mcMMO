package com.gmail.nossr50.datatypes.skills;

import com.google.common.collect.ImmutableSet;
import com.neetgames.mcmmo.skill.RootSkill;
import com.neetgames.mcmmo.skill.SkillIdentity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CoreSkills {

    private static final @NotNull ImmutableSet<RootSkill> CORE_ROOT_SKILLS;
    private static final @NotNull ImmutableSet<RootSkill> CORE_CHILD_SKILLS;
    private static final @NotNull ImmutableSet<RootSkill> CORE_NON_CHILD_SKILLS;

    public static final @NotNull CoreRootSkill ACROBATICS_CS, ALCHEMY_CS, ARCHERY_CS, AXES_CS, EXCAVATION_CS,
    FISHING_CS, HERBALISM_CS, MINING_CS, REPAIR_CS, SALVAGE_CS, SMELTING_CS, SWORDS_CS, TAMING_CS, UNARMED_CS,
    WOODCUTTING_CS, TRIDENTS_CS, CROSSBOWS_CS;

    public static final @NotNull SkillIdentity ACROBATICS_ID, ALCHEMY_ID, ARCHERY_ID, AXES_ID, EXCAVATION_ID,
    FISHING_ID, HERBALISM_ID, MINING_ID, REPAIR_ID, SALVAGE_ID, SMELTING_ID, SWORDS_ID, TAMING_ID, UNARMED_ID,
    WOODCUTTING_ID, TRIDENTS_ID, CROSSBOWS_ID;


    private static @NotNull
    final HackySkillMappings hackySkillMappings = new HackySkillMappings();

    static {
        HashSet<CoreRootSkill> rootSkillSet = new HashSet<>();
        HashSet<CoreRootSkill> childSkillSet = new HashSet<>();

        ACROBATICS_CS = new CoreRootSkill("acrobatics");
        ACROBATICS_ID = ACROBATICS_CS.getSkillIdentity();

        ALCHEMY_CS = new CoreRootSkill("alchemy");
        ALCHEMY_ID = ALCHEMY_CS.getSkillIdentity();

        ARCHERY_CS = new CoreRootSkill("archery");
        ARCHERY_ID = ARCHERY_CS.getSkillIdentity();

        AXES_CS = new CoreRootSkill("axes");
        AXES_ID = AXES_CS.getSkillIdentity();

        EXCAVATION_CS = new CoreRootSkill("excavation");
        EXCAVATION_ID = EXCAVATION_CS.getSkillIdentity();

        FISHING_CS = new CoreRootSkill("fishing");
        FISHING_ID = FISHING_CS.getSkillIdentity();

        HERBALISM_CS = new CoreRootSkill("herbalism");
        HERBALISM_ID = HERBALISM_CS.getSkillIdentity();

        MINING_CS = new CoreRootSkill("mining");
        MINING_ID = MINING_CS.getSkillIdentity();

        REPAIR_CS = new CoreRootSkill("repair");
        REPAIR_ID = REPAIR_CS.getSkillIdentity();

        SALVAGE_CS = new CoreRootSkill("salvage");
        SALVAGE_ID = SALVAGE_CS.getSkillIdentity();

        SMELTING_CS = new CoreRootSkill("smelting");
        SMELTING_ID = SMELTING_CS.getSkillIdentity();

        SWORDS_CS = new CoreRootSkill("swords");
        SWORDS_ID = SWORDS_CS.getSkillIdentity();

        TAMING_CS = new CoreRootSkill("taming");
        TAMING_ID = TAMING_CS.getSkillIdentity();

        UNARMED_CS = new CoreRootSkill("unarmed");
        UNARMED_ID = UNARMED_CS.getSkillIdentity();

        WOODCUTTING_CS = new CoreRootSkill("woodcutting");
        WOODCUTTING_ID = WOODCUTTING_CS.getSkillIdentity();

        TRIDENTS_CS = new CoreRootSkill("tridents");
        TRIDENTS_ID = TRIDENTS_CS.getSkillIdentity();

        CROSSBOWS_CS = new CoreRootSkill("crossbows");
        CROSSBOWS_ID = CROSSBOWS_CS.getSkillIdentity();
        
        //Child skills (soon to be removed)
        childSkillSet.add(SMELTING_CS);
        childSkillSet.add(SALVAGE_CS);

        rootSkillSet.add(ACROBATICS_CS);
        rootSkillSet.add(ALCHEMY_CS);
        rootSkillSet.add(ARCHERY_CS);
        rootSkillSet.add(AXES_CS);
        rootSkillSet.add(EXCAVATION_CS);
        rootSkillSet.add(FISHING_CS);
        rootSkillSet.add(HERBALISM_CS);
        rootSkillSet.add(MINING_CS);
        rootSkillSet.add(REPAIR_CS);
        rootSkillSet.add(SALVAGE_CS);
        rootSkillSet.add(SMELTING_CS);
        rootSkillSet.add(SWORDS_CS);
        rootSkillSet.add(TAMING_CS);
        rootSkillSet.add(UNARMED_CS);
        rootSkillSet.add(WOODCUTTING_CS);
        rootSkillSet.add(TRIDENTS_CS);
        rootSkillSet.add(CROSSBOWS_CS);

        CORE_ROOT_SKILLS = ImmutableSet.copyOf(rootSkillSet);
        CORE_CHILD_SKILLS = ImmutableSet.copyOf(childSkillSet);
        CORE_NON_CHILD_SKILLS = ImmutableSet.copyOf(generateNonChildSkillSet());
    }

    /**
     * Returns a set of built in skills for mcMMO
     * No guarantees for whether or not the skills are registered or active or inactive
     *
     * @return a set of all root skills built into mcMMO
     */
    public static @NotNull Set<RootSkill> getImmutableCoreRootSkillSet() {
        return CORE_ROOT_SKILLS;
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
     * @param rootSkill target skill
     * @return true if the skill identity belongs to a core "child" root skill
     */
    public static boolean isChildSkill(@NotNull RootSkill rootSkill) {
        return CORE_CHILD_SKILLS.contains(rootSkill);
    }

    @Deprecated
    public static @NotNull RootSkill getSkill(@NotNull PrimarySkillType primarySkillType) {
        if(!hackySkillMappings.init) {
            hackySkillMappings.initMappings();
        }

        return hackySkillMappings.primaryToRootMap.get(primarySkillType);
    }

    @Deprecated
    public static @NotNull PrimarySkillType getSkill(@NotNull RootSkill rootSkill) {
        if(!hackySkillMappings.init) {
            hackySkillMappings.initMappings();
        }

        return hackySkillMappings.rootToPrimaryMap.get(rootSkill);
    }

    @Deprecated
    private static @NotNull Set<RootSkill> generateNonChildSkillSet() {
        return getImmutableCoreRootSkillSet().stream().filter((x) -> !isChildSkill(x)).collect(Collectors.toSet());
    }

    public static @NotNull Set<RootSkill> getNonChildSkills() {
        return CORE_NON_CHILD_SKILLS;
    }

    protected static class HackySkillMappings {
        @NotNull Map<PrimarySkillType, RootSkill> primaryToRootMap = new HashMap<>();
        @NotNull Map<RootSkill, PrimarySkillType> rootToPrimaryMap = new HashMap<>();
        boolean init = false;

        protected void initMappings() {
            //TODO: add tests
            //Can't init these from the get go as PrimarySkillType does some stuff and it would be race condition hell
            primaryToRootMap.put(PrimarySkillType.ACROBATICS, ACROBATICS_CS);
            rootToPrimaryMap.put(ACROBATICS_CS, PrimarySkillType.ACROBATICS);

            primaryToRootMap.put(PrimarySkillType.ALCHEMY, ALCHEMY_CS);
            rootToPrimaryMap.put(ALCHEMY_CS, PrimarySkillType.ALCHEMY);

            primaryToRootMap.put(PrimarySkillType.ARCHERY, ARCHERY_CS);
            rootToPrimaryMap.put(ARCHERY_CS, PrimarySkillType.ARCHERY);

            primaryToRootMap.put(PrimarySkillType.AXES, AXES_CS);
            rootToPrimaryMap.put(AXES_CS, PrimarySkillType.AXES);

            primaryToRootMap.put(PrimarySkillType.EXCAVATION, EXCAVATION_CS);
            rootToPrimaryMap.put(EXCAVATION_CS, PrimarySkillType.EXCAVATION);

            primaryToRootMap.put(PrimarySkillType.FISHING, FISHING_CS);
            rootToPrimaryMap.put(FISHING_CS, PrimarySkillType.FISHING);

            primaryToRootMap.put(PrimarySkillType.HERBALISM, HERBALISM_CS);
            rootToPrimaryMap.put(HERBALISM_CS, PrimarySkillType.HERBALISM);

            primaryToRootMap.put(PrimarySkillType.MINING, MINING_CS);
            rootToPrimaryMap.put(MINING_CS, PrimarySkillType.MINING);

            primaryToRootMap.put(PrimarySkillType.REPAIR, REPAIR_CS);
            rootToPrimaryMap.put(REPAIR_CS, PrimarySkillType.REPAIR);

            primaryToRootMap.put(PrimarySkillType.SALVAGE, SALVAGE_CS);
            rootToPrimaryMap.put(SALVAGE_CS, PrimarySkillType.SALVAGE);

            primaryToRootMap.put(PrimarySkillType.SMELTING, SMELTING_CS);
            rootToPrimaryMap.put(SMELTING_CS, PrimarySkillType.SMELTING);

            primaryToRootMap.put(PrimarySkillType.SWORDS, SWORDS_CS);
            rootToPrimaryMap.put(SWORDS_CS, PrimarySkillType.SWORDS);

            primaryToRootMap.put(PrimarySkillType.TAMING, TAMING_CS);
            rootToPrimaryMap.put(TAMING_CS, PrimarySkillType.TAMING);

            primaryToRootMap.put(PrimarySkillType.UNARMED, UNARMED_CS);
            rootToPrimaryMap.put(UNARMED_CS, PrimarySkillType.UNARMED);

            primaryToRootMap.put(PrimarySkillType.WOODCUTTING, WOODCUTTING_CS);
            rootToPrimaryMap.put(WOODCUTTING_CS, PrimarySkillType.WOODCUTTING);

            primaryToRootMap.put(PrimarySkillType.TRIDENTS, TRIDENTS_CS);
            rootToPrimaryMap.put(TRIDENTS_CS, PrimarySkillType.TRIDENTS);

            primaryToRootMap.put(PrimarySkillType.CROSSBOWS, CROSSBOWS_CS);
            rootToPrimaryMap.put(CROSSBOWS_CS, PrimarySkillType.CROSSBOWS);

            init = true;
        }
    }
}
