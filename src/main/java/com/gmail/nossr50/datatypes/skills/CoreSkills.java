package com.gmail.nossr50.datatypes.skills;

import com.gmail.nossr50.skills.acrobatics.AcrobaticsManager;
import com.gmail.nossr50.skills.alchemy.AlchemyManager;
import com.gmail.nossr50.skills.archery.ArcheryManager;
import com.gmail.nossr50.skills.axes.AxesManager;
import com.gmail.nossr50.skills.crossbows.CrossbowsManager;
import com.gmail.nossr50.skills.excavation.ExcavationManager;
import com.gmail.nossr50.skills.fishing.FishingManager;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.repair.RepairManager;
import com.gmail.nossr50.skills.salvage.SalvageManager;
import com.gmail.nossr50.skills.smelting.SmeltingManager;
import com.gmail.nossr50.skills.swords.SwordsManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.tridents.TridentsManager;
import com.gmail.nossr50.skills.unarmed.UnarmedManager;
import com.gmail.nossr50.skills.woodcutting.WoodcuttingManager;
import com.google.common.collect.ImmutableSet;
import com.neetgames.mcmmo.skill.RootSkill;
import com.neetgames.mcmmo.skill.SkillIdentity;
import com.neetgames.mcmmo.skill.SuperSkill;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CoreSkills {

    private static final @NotNull ImmutableSet<CoreRootSkill> CORE_ROOT_SKILLS;
    private static final @NotNull ImmutableSet<CoreRootSkill> CORE_CHILD_SKILLS; //Could make this a marker interface
    private static final @NotNull ImmutableSet<CoreRootSkill> CORE_NON_CHILD_SKILLS;
    private static final @NotNull ImmutableSet<CoreSkill> CORE_SUB_SKILLS;
    private static final @NotNull ImmutableSet<SuperSkill> CORE_SUPER_SKILLS;

    //TODO: Use annotations instead
    public static final @NotNull CoreRootSkill ACROBATICS, ALCHEMY, ARCHERY, AXES, EXCAVATION,
            FISHING, HERBALISM, MINING, REPAIR, SALVAGE, SMELTING, SWORDS, TAMING, UNARMED,
            WOODCUTTING, TRIDENTS, CROSSBOWS;

    //TODO: Needed?
    public static final @NotNull SkillIdentity ACROBATICS_ID, ALCHEMY_ID, ARCHERY_ID, AXES_ID, EXCAVATION_ID,
    FISHING_ID, HERBALISM_ID, MINING_ID, REPAIR_ID, SALVAGE_ID, SMELTING_ID, SWORDS_ID, TAMING_ID, UNARMED_ID,
    WOODCUTTING_ID, TRIDENTS_ID, CROSSBOWS_ID;

    public static final @NotNull SuperSkill SKULL_SPLITTER, GIGA_DRILL_BREAKER, GREEN_TERRA, SUPER_BREAKER,
            BLAST_MINING, SERRATED_STRIKES, CALL_OF_THE_WILD, BERSERK, TREE_FELLER, TRIDENTS_SUPER, SUPER_SHOT_GUN;

    public static final @NotNull CoreSkill ROLL;

    private static final @NotNull HackySkillMappings hackySkillMappings = new HackySkillMappings();

    static {
        HashSet<CoreRootSkill> rootSkillSet = new HashSet<>();
        HashSet<CoreRootSkill> childSkillSet = new HashSet<>();
        HashSet<CoreSkill> subSkillSet = new HashSet<>();
        HashSet<SuperSkill> superSkillSet = new HashSet<>();

        ACROBATICS = new CoreRootSkill("acrobatics", AcrobaticsManager.class);
        ACROBATICS_ID = ACROBATICS.getSkillIdentity();

        ALCHEMY = new CoreRootSkill("alchemy", AlchemyManager.class);
        ALCHEMY_ID = ALCHEMY.getSkillIdentity();

        ARCHERY = new CoreRootSkill("archery", ArcheryManager.class);
        ARCHERY_ID = ARCHERY.getSkillIdentity();

        AXES = new CoreRootSkill("axes", AxesManager.class);
        AXES_ID = AXES.getSkillIdentity();

        EXCAVATION = new CoreRootSkill("excavation", ExcavationManager.class);
        EXCAVATION_ID = EXCAVATION.getSkillIdentity();

        FISHING = new CoreRootSkill("fishing", FishingManager.class);
        FISHING_ID = FISHING.getSkillIdentity();

        HERBALISM = new CoreRootSkill("herbalism", HerbalismManager.class);
        HERBALISM_ID = HERBALISM.getSkillIdentity();

        MINING = new CoreRootSkill("mining", MiningManager.class);
        MINING_ID = MINING.getSkillIdentity();

        REPAIR = new CoreRootSkill("repair", RepairManager.class);
        REPAIR_ID = REPAIR.getSkillIdentity();

        SALVAGE = new CoreRootSkill("salvage", SalvageManager.class);
        SALVAGE_ID = SALVAGE.getSkillIdentity();

        SMELTING = new CoreRootSkill("smelting", SmeltingManager.class);
        SMELTING_ID = SMELTING.getSkillIdentity();

        SWORDS = new CoreRootSkill("swords", SwordsManager.class);
        SWORDS_ID = SWORDS.getSkillIdentity();

        TAMING = new CoreRootSkill("taming", TamingManager.class);
        TAMING_ID = TAMING.getSkillIdentity();

        UNARMED = new CoreRootSkill("unarmed", UnarmedManager.class);
        UNARMED_ID = UNARMED.getSkillIdentity();

        WOODCUTTING = new CoreRootSkill("woodcutting", WoodcuttingManager.class);
        WOODCUTTING_ID = WOODCUTTING.getSkillIdentity();

        TRIDENTS = new CoreRootSkill("tridents", TridentsManager.class);
        TRIDENTS_ID = TRIDENTS.getSkillIdentity();

        CROSSBOWS = new CoreRootSkill("crossbows", CrossbowsManager.class);
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

        CORE_ROOT_SKILLS = ImmutableSet.copyOf(rootSkillSet);
        CORE_CHILD_SKILLS = ImmutableSet.copyOf(childSkillSet);
        CORE_NON_CHILD_SKILLS = ImmutableSet.copyOf(generateNonChildSkillSet());
        CORE_SUB_SKILLS = ImmutableSet.copyOf(subSkillSet);
        CORE_SUPER_SKILLS = ImmutableSet.copyOf(superSkillSet);
    }

    /**
     * Returns a set of built in {@link RootSkill}s for mcMMO
     * No guarantees for whether or not the skills are registered or active or inactive
     *
     * @return a set of all root skills built into mcMMO
     */
    public static @NotNull Set<CoreRootSkill> getCoreRootSkills() {
        return CORE_ROOT_SKILLS;
    }

    /**
     * Returns a set of built in skills for mcMMO
     * No guarantees for whether or not the skills are registered or active or inactive
     *
     * @return a set of all root skills built into mcMMO
     */
    public static @NotNull Set<CoreSkill> getCoreSkills() {
        return CORE_SUB_SKILLS;
    }

    /**
     * Returns a set of built in skills for mcMMO
     * No guarantees for whether or not the skills are registered or active or inactive
     *
     * @return a set of all root skills built into mcMMO
     */
    public static @NotNull Set<SuperSkill> getCoreSkills() {
        return CORE_SUPER_SKILLS;
    }

    /**
     * Returns a set of built in skills for mcMMO which are child skills
     * No guarantees for whether or not the skills are registered or active or inactive
     *
     * @return a set of all "child" root skills for mcMMO
     * @deprecated child skills will be removed in an upcoming update
     */
    @Deprecated
    public static @NotNull Set<CoreRootSkill> getChildSkills() {
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
    private static @NotNull Set<CoreRootSkill> generateNonChildSkillSet() {
        return getCoreRootSkills().stream().filter((x) -> !isChildSkill(x)).collect(Collectors.toSet());
    }

    public static @NotNull Set<CoreRootSkill> getNonChildSkills() {
        return CORE_NON_CHILD_SKILLS;
    }

    protected static class HackySkillMappings {
        @NotNull Map<PrimarySkillType, CoreRootSkill> primaryToRootMap = new HashMap<>();
        @NotNull Map<CoreRootSkill, PrimarySkillType> rootToPrimaryMap = new HashMap<>();
        boolean init = false;

        protected void initMappings() {
            //TODO: add tests
            //Can't init these from the get go as PrimarySkillType does some stuff and it would be race condition hell
            primaryToRootMap.put(PrimarySkillType.ACROBATICS, ACROBATICS);
            rootToPrimaryMap.put(ACROBATICS, PrimarySkillType.ACROBATICS);

            primaryToRootMap.put(PrimarySkillType.ALCHEMY, ALCHEMY);
            rootToPrimaryMap.put(ALCHEMY, PrimarySkillType.ALCHEMY);

            primaryToRootMap.put(PrimarySkillType.ARCHERY, ARCHERY);
            rootToPrimaryMap.put(ARCHERY, PrimarySkillType.ARCHERY);

            primaryToRootMap.put(PrimarySkillType.AXES, AXES);
            rootToPrimaryMap.put(AXES, PrimarySkillType.AXES);

            primaryToRootMap.put(PrimarySkillType.EXCAVATION, EXCAVATION);
            rootToPrimaryMap.put(EXCAVATION, PrimarySkillType.EXCAVATION);

            primaryToRootMap.put(PrimarySkillType.FISHING, FISHING);
            rootToPrimaryMap.put(FISHING, PrimarySkillType.FISHING);

            primaryToRootMap.put(PrimarySkillType.HERBALISM, HERBALISM);
            rootToPrimaryMap.put(HERBALISM, PrimarySkillType.HERBALISM);

            primaryToRootMap.put(PrimarySkillType.MINING, MINING);
            rootToPrimaryMap.put(MINING, PrimarySkillType.MINING);

            primaryToRootMap.put(PrimarySkillType.REPAIR, REPAIR);
            rootToPrimaryMap.put(REPAIR, PrimarySkillType.REPAIR);

            primaryToRootMap.put(PrimarySkillType.SALVAGE, SALVAGE);
            rootToPrimaryMap.put(SALVAGE, PrimarySkillType.SALVAGE);

            primaryToRootMap.put(PrimarySkillType.SMELTING, SMELTING);
            rootToPrimaryMap.put(SMELTING, PrimarySkillType.SMELTING);

            primaryToRootMap.put(PrimarySkillType.SWORDS, SWORDS);
            rootToPrimaryMap.put(SWORDS, PrimarySkillType.SWORDS);

            primaryToRootMap.put(PrimarySkillType.TAMING, TAMING);
            rootToPrimaryMap.put(TAMING, PrimarySkillType.TAMING);

            primaryToRootMap.put(PrimarySkillType.UNARMED, UNARMED);
            rootToPrimaryMap.put(UNARMED, PrimarySkillType.UNARMED);

            primaryToRootMap.put(PrimarySkillType.WOODCUTTING, WOODCUTTING);
            rootToPrimaryMap.put(WOODCUTTING, PrimarySkillType.WOODCUTTING);

            primaryToRootMap.put(PrimarySkillType.TRIDENTS, TRIDENTS);
            rootToPrimaryMap.put(TRIDENTS, PrimarySkillType.TRIDENTS);

            primaryToRootMap.put(PrimarySkillType.CROSSBOWS, CROSSBOWS);
            rootToPrimaryMap.put(CROSSBOWS, PrimarySkillType.CROSSBOWS);

            init = true;
        }
    }
}
