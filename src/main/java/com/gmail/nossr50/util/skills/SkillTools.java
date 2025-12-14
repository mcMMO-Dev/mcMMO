package com.gmail.nossr50.util.skills;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

public class SkillTools {
    private final mcMMO pluginRef;

    // TODO: Java has immutable types now, switch to those
    // TODO: Figure out which ones we don't need, this was copy pasted from a diff branch
    public final @NotNull ImmutableList<String> LOCALIZED_SKILL_NAMES;
    public final @NotNull ImmutableList<String> FORMATTED_SUBSKILL_NAMES;
    public final @NotNull ImmutableSet<String> EXACT_SUBSKILL_NAMES;
    public final @NotNull ImmutableList<PrimarySkillType> CHILD_SKILLS;
    public static final @NotNull ImmutableList<PrimarySkillType> NON_CHILD_SKILLS;
    public static final @NotNull ImmutableList<PrimarySkillType> SALVAGE_PARENTS;
    public static final @NotNull ImmutableList<PrimarySkillType> SMELTING_PARENTS;
    public final @NotNull ImmutableList<PrimarySkillType> COMBAT_SKILLS;
    public final @NotNull ImmutableList<PrimarySkillType> GATHERING_SKILLS;
    public final @NotNull ImmutableList<PrimarySkillType> MISC_SKILLS;

    private final @NotNull ImmutableMap<SubSkillType, PrimarySkillType> subSkillParentRelationshipMap;
    private final @NotNull ImmutableMap<SuperAbilityType, PrimarySkillType> superAbilityParentRelationshipMap;
    private final @NotNull ImmutableMap<PrimarySkillType, Set<SubSkillType>> primarySkillChildrenMap;

    private final ImmutableMap<PrimarySkillType, SuperAbilityType> mainActivatedAbilityChildMap;
    private final ImmutableMap<PrimarySkillType, ToolType> primarySkillToolMap;

    static {
        // Build NON_CHILD_SKILLS once from the enum values
        ArrayList<PrimarySkillType> tempNonChildSkills = new ArrayList<>();
        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            if (!isChildSkill(primarySkillType)) {
                tempNonChildSkills.add(primarySkillType);
            }
        }
        NON_CHILD_SKILLS = ImmutableList.copyOf(tempNonChildSkills);

        SALVAGE_PARENTS = ImmutableList.of(
                PrimarySkillType.REPAIR,
                PrimarySkillType.FISHING
        );
        SMELTING_PARENTS = ImmutableList.of(
                PrimarySkillType.MINING,
                PrimarySkillType.REPAIR
        );
    }

    public SkillTools(@NotNull mcMMO pluginRef) {
        this.pluginRef = pluginRef;

        /*
         * Setup subskill -> parent relationship map
         */
        this.subSkillParentRelationshipMap = buildSubSkillParentMap();

        /*
         * Setup primary -> (collection) subskill map
         */
        this.primarySkillChildrenMap = buildPrimarySkillChildrenMap(subSkillParentRelationshipMap);

        /*
         * Setup primary -> tooltype map
         */
        this.primarySkillToolMap = buildPrimarySkillToolMap();

        /*
         * Setup ability -> primary map
         * Setup primary -> ability map
         */
        var abilityMaps = buildSuperAbilityMaps();
        this.superAbilityParentRelationshipMap = abilityMaps.superAbilityParentRelationshipMap();
        this.mainActivatedAbilityChildMap = abilityMaps.mainActivatedAbilityChildMap();

        /*
         * Build child skill list
         */
        this.CHILD_SKILLS = buildChildSkills();

        /*
         * Build categorized skill lists
         */
        this.COMBAT_SKILLS = buildCombatSkills();
        this.GATHERING_SKILLS = ImmutableList.of(
                PrimarySkillType.EXCAVATION,
                PrimarySkillType.FISHING,
                PrimarySkillType.HERBALISM,
                PrimarySkillType.MINING,
                PrimarySkillType.WOODCUTTING
        );
        this.MISC_SKILLS = ImmutableList.of(
                PrimarySkillType.ACROBATICS,
                PrimarySkillType.ALCHEMY,
                PrimarySkillType.REPAIR,
                PrimarySkillType.SALVAGE,
                PrimarySkillType.SMELTING
        );

        /*
         * Build formatted/localized/etc string lists
         */
        this.LOCALIZED_SKILL_NAMES = ImmutableList.copyOf(buildLocalizedPrimarySkillNames());
        this.FORMATTED_SUBSKILL_NAMES = ImmutableList.copyOf(buildFormattedSubSkillNameList());
        this.EXACT_SUBSKILL_NAMES = ImmutableSet.copyOf(buildExactSubSkillNameList());
    }

    @VisibleForTesting
    @NotNull
    ImmutableMap<SubSkillType, PrimarySkillType> buildSubSkillParentMap() {
        EnumMap<SubSkillType, PrimarySkillType> tempSubParentMap =
                new EnumMap<>(SubSkillType.class);

        // SubSkillType names use a convention: <PRIMARY>_SOMETHING
        for (SubSkillType subSkillType : SubSkillType.values()) {
            String enumName = subSkillType.name();
            int underscoreIndex = enumName.indexOf('_');
            String parentPrefix = underscoreIndex == -1
                    ? enumName
                    : enumName.substring(0, underscoreIndex);

            for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
                if (primarySkillType.name().equalsIgnoreCase(parentPrefix)) {
                    tempSubParentMap.put(subSkillType, primarySkillType);
                    break;
                }
            }
        }

        return ImmutableMap.copyOf(tempSubParentMap);
    }

    @VisibleForTesting
    @NotNull
    ImmutableMap<PrimarySkillType, Set<SubSkillType>> buildPrimarySkillChildrenMap(
            ImmutableMap<SubSkillType, PrimarySkillType> subParentMap) {

        EnumMap<PrimarySkillType, Set<SubSkillType>> tempPrimaryChildMap =
                new EnumMap<>(PrimarySkillType.class);

        // Initialize empty sets
        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            tempPrimaryChildMap.put(primarySkillType, new HashSet<>());
        }

        // Fill sets
        for (SubSkillType subSkillType : SubSkillType.values()) {
            PrimarySkillType parentSkill = subParentMap.get(subSkillType);
            if (parentSkill != null) {
                tempPrimaryChildMap.get(parentSkill).add(subSkillType);
            }
        }

        return ImmutableMap.copyOf(tempPrimaryChildMap);
    }

    @VisibleForTesting
    @NotNull
    ImmutableMap<PrimarySkillType, ToolType> buildPrimarySkillToolMap() {
        EnumMap<PrimarySkillType, ToolType> tempToolMap =
                new EnumMap<>(PrimarySkillType.class);

        tempToolMap.put(PrimarySkillType.AXES, ToolType.AXE);
        tempToolMap.put(PrimarySkillType.WOODCUTTING, ToolType.AXE);
        tempToolMap.put(PrimarySkillType.UNARMED, ToolType.FISTS);
        tempToolMap.put(PrimarySkillType.SWORDS, ToolType.SWORD);
        tempToolMap.put(PrimarySkillType.EXCAVATION, ToolType.SHOVEL);
        tempToolMap.put(PrimarySkillType.HERBALISM, ToolType.HOE);
        tempToolMap.put(PrimarySkillType.MINING, ToolType.PICKAXE);

        return ImmutableMap.copyOf(tempToolMap);
    }

    /**
     * Holder for the two super ability maps, so we can build them in one pass.
     */
    @VisibleForTesting
    record SuperAbilityMaps(
            @NotNull ImmutableMap<SuperAbilityType, PrimarySkillType> superAbilityParentRelationshipMap,
            @NotNull ImmutableMap<PrimarySkillType, SuperAbilityType> mainActivatedAbilityChildMap) {
    }

    @VisibleForTesting
    @NotNull
    SuperAbilityMaps buildSuperAbilityMaps() {
        final Map<SuperAbilityType, PrimarySkillType> tempAbilityParentRelationshipMap =
                new EnumMap<>(SuperAbilityType.class);
        final Map<PrimarySkillType, SuperAbilityType> tempMainActivatedAbilityChildMap =
                new EnumMap<>(PrimarySkillType.class);

        for (SuperAbilityType superAbilityType : SuperAbilityType.values()) {
            final PrimarySkillType parent = getSuperAbilityParent(superAbilityType);
            tempAbilityParentRelationshipMap.put(superAbilityType, parent);

            // This map is used only for abilities that have a tool readying phase,
            // so Blast Mining is ignored.
            if (superAbilityType != SuperAbilityType.BLAST_MINING) {
                tempMainActivatedAbilityChildMap.put(parent, superAbilityType);
            }
        }

        return new SuperAbilityMaps(
                ImmutableMap.copyOf(tempAbilityParentRelationshipMap),
                ImmutableMap.copyOf(tempMainActivatedAbilityChildMap)
        );
    }

    @VisibleForTesting
    @NotNull
    ImmutableList<PrimarySkillType> buildChildSkills() {
        List<PrimarySkillType> childSkills = new ArrayList<>();
        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            if (isChildSkill(primarySkillType)) {
                childSkills.add(primarySkillType);
            }
        }
        return ImmutableList.copyOf(childSkills);
    }

    @VisibleForTesting
    @NotNull
    ImmutableList<PrimarySkillType> buildCombatSkills() {
        var gameVersion = mcMMO.getCompatibilityManager().getMinecraftGameVersion();

        if (gameVersion.isAtLeast(1, 21, 11)) {
            // We are in a game version with Spears and Maces
            return ImmutableList.of(
                    PrimarySkillType.ARCHERY,
                    PrimarySkillType.AXES,
                    PrimarySkillType.CROSSBOWS,
                    PrimarySkillType.MACES,
                    PrimarySkillType.SWORDS,
                    PrimarySkillType.SPEARS,
                    PrimarySkillType.TAMING,
                    PrimarySkillType.TRIDENTS,
                    PrimarySkillType.UNARMED
            );
        } else if (gameVersion.isAtLeast(1, 21, 0)) {
            // We are in a game version with Maces
            return ImmutableList.of(
                    PrimarySkillType.ARCHERY,
                    PrimarySkillType.AXES,
                    PrimarySkillType.CROSSBOWS,
                    PrimarySkillType.MACES,
                    PrimarySkillType.SWORDS,
                    PrimarySkillType.TAMING,
                    PrimarySkillType.TRIDENTS,
                    PrimarySkillType.UNARMED
            );
        } else {
            // No Maces in this version
            return ImmutableList.of(
                    PrimarySkillType.ARCHERY,
                    PrimarySkillType.AXES,
                    PrimarySkillType.CROSSBOWS,
                    PrimarySkillType.SWORDS,
                    PrimarySkillType.TAMING,
                    PrimarySkillType.TRIDENTS,
                    PrimarySkillType.UNARMED
            );
        }
    }

    private @NotNull PrimarySkillType getSuperAbilityParent(SuperAbilityType superAbilityType) {
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
            case SPEARS_SUPER_ABILITY -> PrimarySkillType.SPEARS;
        };
    }

    /**
     * Makes a list of the "nice" version of sub skill names. Used in tab completion mostly.
     *
     * @return a list of formatted sub skill names
     */
    private @NotNull ArrayList<String> buildFormattedSubSkillNameList() {
        ArrayList<String> subSkillNameList = new ArrayList<>();

        for (SubSkillType subSkillType : SubSkillType.values()) {
            subSkillNameList.add(subSkillType.getNiceNameNoSpaces(subSkillType));
        }

        return subSkillNameList;
    }

    private @NotNull HashSet<String> buildExactSubSkillNameList() {
        HashSet<String> subSkillNameExactSet = new HashSet<>();

        for (SubSkillType subSkillType : SubSkillType.values()) {
            subSkillNameExactSet.add(subSkillType.toString());
        }

        return subSkillNameExactSet;
    }

    /**
     * Builds a list of localized {@link PrimarySkillType} names
     *
     * @return list of localized {@link PrimarySkillType} names
     */
    @VisibleForTesting
    private @NotNull ArrayList<String> buildLocalizedPrimarySkillNames() {
        ArrayList<String> localizedSkillNameList = new ArrayList<>();

        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            localizedSkillNameList.add(getLocalizedSkillName(primarySkillType));
        }

        Collections.sort(localizedSkillNameList);

        return localizedSkillNameList;
    }

    /**
     * Matches a string of a skill to a skill.
     * This is NOT case-sensitive.
     * <p>
     * First it checks the locale file and tries to match by the localized name of the skill.
     * Then if nothing is found it checks against the hard coded "name" of the skill,
     * which is just its name in English.
     *
     * @param skillName target skill name
     * @return the matching PrimarySkillType if one is found, otherwise null
     */
    public PrimarySkillType matchSkill(String skillName) {
        if (!pluginRef.getGeneralConfig().getLocale().equalsIgnoreCase("en_US")) {
            for (PrimarySkillType type : PrimarySkillType.values()) {
                String localized = LocaleLoader.getString(
                        StringUtils.getCapitalized(type.name()) + ".SkillName");
                if (skillName.equalsIgnoreCase(localized)) {
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
            pluginRef.getLogger()
                    .warning("Invalid mcMMO skill (" + skillName + ")"); // TODO: Localize
        }

        return null;
    }

    /**
     * Gets the PrimarySkillType to which a SubSkillType belongs.
     * Returns null if it does not belong to one (which should be impossible in most circumstances).
     *
     * @param subSkillType target subskill
     * @return the PrimarySkillType of this SubSkill, null if it doesn't exist
     */
    public PrimarySkillType getPrimarySkillBySubSkill(SubSkillType subSkillType) {
        return subSkillParentRelationshipMap.get(subSkillType);
    }

    /**
     * Gets the PrimarySkillType to which a SuperAbilityType belongs.
     * Returns null if it does not belong to one (which should be impossible in most circumstances).
     *
     * @param superAbilityType target super ability
     * @return the PrimarySkillType of this SuperAbilityType, null if it doesn't exist
     */
    public PrimarySkillType getPrimarySkillBySuperAbility(SuperAbilityType superAbilityType) {
        return superAbilityParentRelationshipMap.get(superAbilityType);
    }

    public SuperAbilityType getSuperAbility(PrimarySkillType primarySkillType) {
        return mainActivatedAbilityChildMap.get(primarySkillType);
    }

    public boolean isSuperAbilityUnlocked(PrimarySkillType primarySkillType, Player player) {
        SuperAbilityType superAbilityType = getSuperAbility(primarySkillType);
        if (superAbilityType == null) {
            return false;
        }

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

    public static boolean isChildSkill(PrimarySkillType primarySkillType) {
        return switch (primarySkillType) {
            case SALVAGE, SMELTING -> true;
            default -> false;
        };
    }

    /**
     * Get the localized name for a {@link PrimarySkillType}
     *
     * @param primarySkillType target {@link PrimarySkillType}
     * @return the localized name for a {@link PrimarySkillType}
     */
    public String getLocalizedSkillName(PrimarySkillType primarySkillType) {
        return LocaleLoader.getString(
                StringUtils.getCapitalized(primarySkillType.toString()) + ".SkillName");
    }

    public boolean doesPlayerHaveSkillPermission(Player player, PrimarySkillType primarySkillType) {
        return Permissions.skillEnabled(player, primarySkillType);
    }

    public boolean canCombatSkillsTrigger(PrimarySkillType primarySkillType, Entity target) {
        boolean isPlayerOrTamed = (target instanceof Player)
                || (target instanceof Tameable && ((Tameable) target).isTamed());
        return isPlayerOrTamed
                ? getPVPEnabled(primarySkillType)
                : getPVEEnabled(primarySkillType);
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

    public int getLevelCap(@NotNull PrimarySkillType primarySkillType) {
        return pluginRef.getGeneralConfig().getLevelCap(primarySkillType);
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

    public @NotNull ImmutableList<PrimarySkillType> getChildSkillParents(
            PrimarySkillType childSkill) throws IllegalArgumentException {
        return switch (childSkill) {
            case SALVAGE -> SALVAGE_PARENTS;
            case SMELTING -> SMELTING_PARENTS;
            default -> throw new IllegalArgumentException(
                    "Skill " + childSkill + " is not a child skill");
        };
    }
}
