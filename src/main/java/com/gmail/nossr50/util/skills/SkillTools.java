package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.text.StringUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.*;

public class SkillTools {
    private final mcMMO pluginRef;

    //TODO: Figure out which ones we don't need, this was copy pasted from a diff branch
    public final @NotNull ImmutableList<String> LOCALIZED_SKILL_NAMES;
    public final @NotNull ImmutableList<String> FORMATTED_SUBSKILL_NAMES;
    public final @NotNull ImmutableSet<String> EXACT_SUBSKILL_NAMES;
    public final @NotNull ImmutableList<PrimarySkillType> CHILD_SKILLS;
    public final static @NotNull ImmutableList<PrimarySkillType> NON_CHILD_SKILLS;
    public final static @NotNull ImmutableList<PrimarySkillType> SALVAGE_PARENTS;
    public final static @NotNull ImmutableList<PrimarySkillType> SMELTING_PARENTS;
    public final @NotNull ImmutableList<PrimarySkillType> COMBAT_SKILLS;
    public final @NotNull ImmutableList<PrimarySkillType> GATHERING_SKILLS;
    public final @NotNull ImmutableList<PrimarySkillType> MISC_SKILLS;

    private final @NotNull ImmutableMap<SubSkillType, PrimarySkillType> subSkillParentRelationshipMap;
    private final @NotNull ImmutableMap<SuperAbilityType, PrimarySkillType> superAbilityParentRelationshipMap;
    private final @NotNull ImmutableMap<PrimarySkillType, Set<SubSkillType>> primarySkillChildrenMap;

    // The map below is for the super abilities which require readying a tool, its everything except blast mining
    private final ImmutableMap<PrimarySkillType, SuperAbilityType> mainActivatedAbilityChildMap;
    private final ImmutableMap<PrimarySkillType, ToolType> primarySkillToolMap;

    static {
        ArrayList<PrimarySkillType> tempNonChildSkills = new ArrayList<>();
        for(PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            if (primarySkillType != PrimarySkillType.SALVAGE && primarySkillType != PrimarySkillType.SMELTING)
                tempNonChildSkills.add(primarySkillType);
        }

        NON_CHILD_SKILLS = ImmutableList.copyOf(tempNonChildSkills);
        SALVAGE_PARENTS = ImmutableList.of(PrimarySkillType.REPAIR, PrimarySkillType.FISHING);
        SMELTING_PARENTS = ImmutableList.of(PrimarySkillType.MINING, PrimarySkillType.REPAIR);
    }

    public SkillTools(@NotNull mcMMO pluginRef) throws InvalidSkillException {
        this.pluginRef = pluginRef;

        /*
         * Setup subskill -> parent relationship map
         */
        EnumMap<SubSkillType, PrimarySkillType> tempSubParentMap = new EnumMap<>(SubSkillType.class);

        //Super hacky and disgusting
        for(PrimarySkillType primarySkillType1 : PrimarySkillType.values()) {
            for(SubSkillType subSkillType : SubSkillType.values()) {
                String[] splitSubSkillName = subSkillType.toString().split("_");

                if(primarySkillType1.toString().equalsIgnoreCase(splitSubSkillName[0])) {
                    //Parent Skill Found
                    tempSubParentMap.put(subSkillType, primarySkillType1);
                }
            }
        }

        subSkillParentRelationshipMap = ImmutableMap.copyOf(tempSubParentMap);

        /*
         * Setup primary -> (collection) subskill map
         */

        EnumMap<PrimarySkillType, Set<SubSkillType>> tempPrimaryChildMap = new EnumMap<>(PrimarySkillType.class);

        //Init the empty Hash Sets
        for(PrimarySkillType primarySkillType1 : PrimarySkillType.values()) {
            tempPrimaryChildMap.put(primarySkillType1, new HashSet<>());
        }

        //Fill in the hash sets
        for(SubSkillType subSkillType : SubSkillType.values()) {
            PrimarySkillType parentSkill = subSkillParentRelationshipMap.get(subSkillType);

            //Add this subskill as a child
            tempPrimaryChildMap.get(parentSkill).add(subSkillType);
        }

        primarySkillChildrenMap = ImmutableMap.copyOf(tempPrimaryChildMap);

        /*
         * Setup primary -> tooltype map
         */
        EnumMap<PrimarySkillType, ToolType> tempToolMap = new EnumMap<>(PrimarySkillType.class);

        tempToolMap.put(PrimarySkillType.AXES, ToolType.AXE);
        tempToolMap.put(PrimarySkillType.WOODCUTTING, ToolType.AXE);
        tempToolMap.put(PrimarySkillType.UNARMED, ToolType.FISTS);
        tempToolMap.put(PrimarySkillType.SWORDS, ToolType.SWORD);
        tempToolMap.put(PrimarySkillType.EXCAVATION, ToolType.SHOVEL);
        tempToolMap.put(PrimarySkillType.HERBALISM, ToolType.HOE);
        tempToolMap.put(PrimarySkillType.MINING, ToolType.PICKAXE);

        primarySkillToolMap = ImmutableMap.copyOf(tempToolMap);

        /*
         * Setup ability -> primary map
         * Setup primary -> ability map
         */

        EnumMap<SuperAbilityType, PrimarySkillType> tempAbilityParentRelationshipMap = new EnumMap<>(SuperAbilityType.class);
        EnumMap<PrimarySkillType, SuperAbilityType> tempMainActivatedAbilityChildMap = new EnumMap<>(PrimarySkillType.class);

        for(SuperAbilityType superAbilityType : SuperAbilityType.values()) {
            try {
                PrimarySkillType parent = getSuperAbilityParent(superAbilityType);
                tempAbilityParentRelationshipMap.put(superAbilityType, parent);

                if(superAbilityType != SuperAbilityType.BLAST_MINING) {
                    //This map is used only for abilities that have a tool readying phase, so blast mining is ignored
                    tempMainActivatedAbilityChildMap.put(parent, superAbilityType);
                }
            } catch (InvalidSkillException e) {
                e.printStackTrace();
            }
        }

        superAbilityParentRelationshipMap = ImmutableMap.copyOf(tempAbilityParentRelationshipMap);
        mainActivatedAbilityChildMap = ImmutableMap.copyOf(tempMainActivatedAbilityChildMap);

        /*
         * Build child skill and nonchild skill lists
         */

        List<PrimarySkillType> childSkills = new ArrayList<>();

        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            if (isChildSkill(primarySkillType))
                childSkills.add(primarySkillType);
        }

        CHILD_SKILLS = ImmutableList.copyOf(childSkills);

        /*
         * Build categorized skill lists
         */

        COMBAT_SKILLS = ImmutableList.of(
                PrimarySkillType.ARCHERY,
                PrimarySkillType.AXES,
                PrimarySkillType.CROSSBOWS,
                PrimarySkillType.SWORDS,
                PrimarySkillType.TAMING,
                PrimarySkillType.TRIDENTS,
                PrimarySkillType.UNARMED);
        GATHERING_SKILLS = ImmutableList.of(
                PrimarySkillType.EXCAVATION,
                PrimarySkillType.FISHING,
                PrimarySkillType.HERBALISM,
                PrimarySkillType.MINING,
                PrimarySkillType.WOODCUTTING);
        MISC_SKILLS = ImmutableList.of(
                PrimarySkillType.ACROBATICS,
                PrimarySkillType.ALCHEMY,
                PrimarySkillType.REPAIR,
                PrimarySkillType.SALVAGE,
                PrimarySkillType.SMELTING);

        /*
         * Build formatted/localized/etc string lists
         */

        LOCALIZED_SKILL_NAMES = ImmutableList.copyOf(buildLocalizedPrimarySkillNames());
        FORMATTED_SUBSKILL_NAMES = ImmutableList.copyOf(buildFormattedSubSkillNameList());
        EXACT_SUBSKILL_NAMES = ImmutableSet.copyOf(buildExactSubSkillNameList());
    }

    private @NotNull PrimarySkillType getSuperAbilityParent(SuperAbilityType superAbilityType) throws InvalidSkillException {
        return switch (superAbilityType) {
            case BERSERK -> PrimarySkillType.UNARMED;
            case GREEN_TERRA -> PrimarySkillType.HERBALISM;
            case TREE_FELLER -> PrimarySkillType.WOODCUTTING;
            case SUPER_BREAKER, BLAST_MINING -> PrimarySkillType.MINING;
            case SKULL_SPLITTER -> PrimarySkillType.AXES;
            case SERRATED_STRIKES -> PrimarySkillType.SWORDS;
            case GIGA_DRILL_BREAKER -> PrimarySkillType.EXCAVATION;
            case SUPER_SHOTGUN -> PrimarySkillType.CROSSBOWS;
            case TRIDENTS_SUPER_ABILITY -> PrimarySkillType.TRIDENTS;
            case EXPLOSIVE_SHOT -> PrimarySkillType.ARCHERY;
            case MACES_SUPER_ABILITY -> PrimarySkillType.MACES;
        };
    }

    /**
     * Makes a list of the "nice" version of sub skill names
     * Used in tab completion mostly
     * @return a list of formatted sub skill names
     */
    private @NotNull ArrayList<String> buildFormattedSubSkillNameList() {
        ArrayList<String> subSkillNameList = new ArrayList<>();

        for(SubSkillType subSkillType : SubSkillType.values()) {
            subSkillNameList.add(subSkillType.getNiceNameNoSpaces(subSkillType));
        }

        return subSkillNameList;
    }

    private @NotNull HashSet<String> buildExactSubSkillNameList() {
        HashSet<String> subSkillNameExactSet = new HashSet<>();

        for(SubSkillType subSkillType : SubSkillType.values()) {
            subSkillNameExactSet.add(subSkillType.toString());
        }

        return subSkillNameExactSet;
    }

    /**
     * Builds a list of localized {@link PrimarySkillType} names
     * @return list of localized {@link PrimarySkillType} names
     */
    @VisibleForTesting
    private @NotNull ArrayList<String> buildLocalizedPrimarySkillNames() {
        ArrayList<String> localizedSkillNameList = new ArrayList<>();

        for(PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            localizedSkillNameList.add(getLocalizedSkillName(primarySkillType));
        }

        Collections.sort(localizedSkillNameList);

        return localizedSkillNameList;
    }

    /**
     * Matches a string of a skill to a skill
     * This is NOT case sensitive
     * First it checks the locale file and tries to match by the localized name of the skill
     * Then if nothing is found it checks against the hard coded "name" of the skill, which is just its name in English
     *
     * @param skillName target skill name
     * @return the matching PrimarySkillType if one is found, otherwise null
     */
    public PrimarySkillType matchSkill(String skillName) {
        if (!pluginRef.getGeneralConfig().getLocale().equalsIgnoreCase("en_US")) {
            for (PrimarySkillType type : PrimarySkillType.values()) {
                if (skillName.equalsIgnoreCase(LocaleLoader.getString(StringUtils.getCapitalized(type.name()) + ".SkillName"))) {
                    return type;
                }
            }
        }

        for (PrimarySkillType type : PrimarySkillType.values()) {
            if (type.name().equalsIgnoreCase(skillName)) {
                return type;
            }
        }

        if (!skillName.equalsIgnoreCase("all")) {
            pluginRef.getLogger().warning("Invalid mcMMO skill (" + skillName + ")"); //TODO: Localize
        }

        return null;
    }

    /**
     * Gets the PrimarySkillStype to which a SubSkillType belongs
     * Return null if it does not belong to one.. which should be impossible in most circumstances
     * @param subSkillType target subskill
     * @return the PrimarySkillType of this SubSkill, null if it doesn't exist
     */
    public PrimarySkillType getPrimarySkillBySubSkill(SubSkillType subSkillType) {
        return subSkillParentRelationshipMap.get(subSkillType);
    }

    /**
     * Gets the PrimarySkillStype to which a SuperAbilityType belongs
     * Return null if it does not belong to one.. which should be impossible in most circumstances
     * @param superAbilityType target super ability
     * @return the PrimarySkillType of this SuperAbilityType, null if it doesn't exist
     */
    public PrimarySkillType getPrimarySkillBySuperAbility(SuperAbilityType superAbilityType) {
        return superAbilityParentRelationshipMap.get(superAbilityType);
    }

    public SuperAbilityType getSuperAbility(PrimarySkillType primarySkillType) {
        if(mainActivatedAbilityChildMap.get(primarySkillType) == null)
            return null;

        return mainActivatedAbilityChildMap.get(primarySkillType);
    }

    public boolean isSuperAbilityUnlocked(PrimarySkillType primarySkillType, Player player) {
        SuperAbilityType superAbilityType = mcMMO.p.getSkillTools().getSuperAbility(primarySkillType);
        SubSkillType subSkillType = superAbilityType.getSubSkillTypeDefinition();
        return RankUtils.hasUnlockedSubskill(player, subSkillType);
    }

    public boolean getPVPEnabled(PrimarySkillType primarySkillType) {
        return pluginRef.getGeneralConfig().getPVPEnabled(primarySkillType);
    }

    public boolean getPVEEnabled(PrimarySkillType primarySkillType) {
        return pluginRef.getGeneralConfig().getPVEEnabled(primarySkillType);
    }

    public boolean getHardcoreStatLossEnabled(PrimarySkillType primarySkillType) {
        return pluginRef.getGeneralConfig().getHardcoreStatLossEnabled(primarySkillType);
    }

    public boolean getHardcoreVampirismEnabled(PrimarySkillType primarySkillType) {
        return pluginRef.getGeneralConfig().getHardcoreVampirismEnabled(primarySkillType);
    }

    public ToolType getPrimarySkillToolType(PrimarySkillType primarySkillType) {
        return primarySkillToolMap.get(primarySkillType);
    }

    public Set<SubSkillType> getSubSkills(PrimarySkillType primarySkillType) {
        return primarySkillChildrenMap.get(primarySkillType);
    }

    public double getXpMultiplier(PrimarySkillType primarySkillType) {
        return ExperienceConfig.getInstance().getFormulaSkillModifier(primarySkillType);
    }

    // TODO: This is a little "hacky", we probably need to add something to distinguish child skills in the enum, or to use another enum for them
    public static boolean isChildSkill(PrimarySkillType primarySkillType) {
        return switch (primarySkillType) {
            case SALVAGE, SMELTING -> true;
            default -> false;
        };
    }

    /**
     * Get the localized name for a {@link PrimarySkillType}
     * @param primarySkillType target {@link PrimarySkillType}
     * @return the localized name for a {@link PrimarySkillType}
     */
    public String getLocalizedSkillName(PrimarySkillType primarySkillType) {
        return LocaleLoader.getString(StringUtils.getCapitalized(primarySkillType.toString()) + ".SkillName");
    }

    public boolean doesPlayerHaveSkillPermission(Player player, PrimarySkillType primarySkillType) {
        return Permissions.skillEnabled(player, primarySkillType);
    }

    public boolean canCombatSkillsTrigger(PrimarySkillType primarySkillType, Entity target) {
        return (target instanceof Player || (target instanceof Tameable && ((Tameable) target).isTamed())) ? getPVPEnabled(primarySkillType) : getPVEEnabled(primarySkillType);
    }

    public String getCapitalizedPrimarySkillName(PrimarySkillType primarySkillType) {
        return StringUtils.getCapitalized(primarySkillType.toString());
    }

    public int getSuperAbilityCooldown(SuperAbilityType superAbilityType) {
        return pluginRef.getGeneralConfig().getCooldown(superAbilityType);
    }

    public int getSuperAbilityMaxLength(SuperAbilityType superAbilityType) {
        return pluginRef.getGeneralConfig().getMaxLength(superAbilityType);
    }

    public String getSuperAbilityOnLocaleKey(SuperAbilityType superAbilityType) {
        return "SuperAbility." + StringUtils.getPrettyCamelCaseName(superAbilityType) + ".On";
    }

    public String getSuperAbilityOffLocaleKey(SuperAbilityType superAbilityType) {
        return "SuperAbility." + StringUtils.getPrettyCamelCaseName(superAbilityType) + ".Off";
    }

    public String getSuperAbilityOtherPlayerActivationLocaleKey(SuperAbilityType superAbilityType) {
        return "SuperAbility." + StringUtils.getPrettyCamelCaseName(superAbilityType) + ".Other.On";
    }

    public String getSuperAbilityOtherPlayerDeactivationLocaleKey(SuperAbilityType superAbilityType) {
        return "SuperAbility." + StringUtils.getPrettyCamelCaseName(superAbilityType) + "Other.Off";
    }

    public String getSuperAbilityRefreshedLocaleKey(SuperAbilityType superAbilityType) {
        return "SuperAbility." + StringUtils.getPrettyCamelCaseName(superAbilityType) + ".Refresh";
    }

    public int getLevelCap(@NotNull PrimarySkillType primarySkillType) {
        return mcMMO.p.getGeneralConfig().getLevelCap(primarySkillType);
    }

    /**
     * Get the permissions for this ability.
     *
     * @param player Player to check permissions for
     * @param superAbilityType target super ability
     * @return true if the player has permissions, false otherwise
     */
    public boolean superAbilityPermissionCheck(SuperAbilityType superAbilityType, Player player) {
        return superAbilityType.getPermissions(player);
    }

    public @NotNull List<PrimarySkillType> getChildSkills() {
        return CHILD_SKILLS;
    }

    public @NotNull ImmutableList<PrimarySkillType> getNonChildSkills() {
        return NON_CHILD_SKILLS;
    }

    public @NotNull ImmutableList<PrimarySkillType> getCombatSkills() {
        return COMBAT_SKILLS;
    }

    public @NotNull ImmutableList<PrimarySkillType> getGatheringSkills() {
        return GATHERING_SKILLS;
    }

    public @NotNull ImmutableList<PrimarySkillType> getMiscSkills() {
        return MISC_SKILLS;
    }

    public @NotNull ImmutableList<PrimarySkillType> getChildSkillParents(PrimarySkillType childSkill)
            throws IllegalArgumentException {
        switch (childSkill) {
            case SALVAGE -> {
                return SALVAGE_PARENTS;
            }
            case SMELTING -> {
                return SMELTING_PARENTS;
            }
            default -> throw new IllegalArgumentException("Skill " + childSkill + " is not a child skill");
        }
    }
}
