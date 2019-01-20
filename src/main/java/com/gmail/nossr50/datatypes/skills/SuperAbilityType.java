package com.gmail.nossr50.datatypes.skills;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

public enum SuperAbilityType {
    BERSERK(
            "Unarmed.Skills.Berserk.On",
            "Unarmed.Skills.Berserk.Off",
            "Unarmed.Skills.Berserk.Other.On",
            "Unarmed.Skills.Berserk.Refresh",
            "Unarmed.Skills.Berserk.Other.Off"),

    SUPER_BREAKER(
            "Mining.Skills.SuperBreaker.On",
            "Mining.Skills.SuperBreaker.Off",
            "Mining.Skills.SuperBreaker.Other.On",
            "Mining.Skills.SuperBreaker.Refresh",
            "Mining.Skills.SuperBreaker.Other.Off"),

    GIGA_DRILL_BREAKER(
            "Excavation.Skills.GigaDrillBreaker.On",
            "Excavation.Skills.GigaDrillBreaker.Off",
            "Excavation.Skills.GigaDrillBreaker.Other.On",
            "Excavation.Skills.GigaDrillBreaker.Refresh",
            "Excavation.Skills.GigaDrillBreaker.Other.Off"),

    GREEN_TERRA(
            "Herbalism.Skills.GTe.On",
            "Herbalism.Skills.GTe.Off",
            "Herbalism.Skills.GTe.Other.On",
            "Herbalism.Skills.GTe.Refresh",
            "Herbalism.Skills.GTe.Other.Off"),

    SKULL_SPLITTER(
            "Axes.Skills.SS.On",
            "Axes.Skills.SS.Off",
            "Axes.Skills.SS.Other.On",
            "Axes.Skills.SS.Refresh",
            "Axes.Skills.SS.Other.Off"),

    TREE_FELLER(
            "Woodcutting.Skills.TreeFeller.On",
            "Woodcutting.Skills.TreeFeller.Off",
            "Woodcutting.Skills.TreeFeller.Other.On",
            "Woodcutting.Skills.TreeFeller.Refresh",
            "Woodcutting.Skills.TreeFeller.Other.Off"),

    SERRATED_STRIKES(
            "Swords.Skills.SS.On",
            "Swords.Skills.SS.Off",
            "Swords.Skills.SS.Other.On",
            "Swords.Skills.SS.Refresh",
            "Swords.Skills.SS.Other.Off"),

    /**
     * Has cooldown - but has to share a skill with Super Breaker, so needs special treatment
     */
    BLAST_MINING(
            null,
            null,
            "Mining.Blast.Other.On",
            "Mining.Blast.Refresh",
            null),
    ;

    /*
     * Defining their associated SubSkillType definitions
     * This is a bit of a band-aid fix until the new skill system is in place
     */
    static {
        BERSERK.subSkillTypeDefinition              = SubSkillType.UNARMED_BERSERK;
        SUPER_BREAKER.subSkillTypeDefinition        = SubSkillType.MINING_SUPER_BREAKER;
        GIGA_DRILL_BREAKER.subSkillTypeDefinition   = SubSkillType.EXCAVATION_GIGA_DRILL_BREAKER;
        GREEN_TERRA.subSkillTypeDefinition          = SubSkillType.HERBALISM_GREEN_TERRA;
        SKULL_SPLITTER.subSkillTypeDefinition       = SubSkillType.AXES_SKULL_SPLITTER;
        TREE_FELLER.subSkillTypeDefinition          = SubSkillType.WOODCUTTING_TREE_FELLER;
        SERRATED_STRIKES.subSkillTypeDefinition     = SubSkillType.SWORDS_SERRATED_STRIKES;
        BLAST_MINING.subSkillTypeDefinition         = SubSkillType.MINING_BLAST_MINING;
    }

    private String abilityOn;
    private String abilityOff;
    private String abilityPlayer;
    private String abilityRefresh;
    private String abilityPlayerOff;
    private SubSkillType subSkillTypeDefinition;

    private SuperAbilityType(String abilityOn, String abilityOff, String abilityPlayer, String abilityRefresh, String abilityPlayerOff) {
        this.abilityOn = abilityOn;
        this.abilityOff = abilityOff;
        this.abilityPlayer = abilityPlayer;
        this.abilityRefresh = abilityRefresh;
        this.abilityPlayerOff = abilityPlayerOff;
    }

    public int getCooldown() {
        return Config.getInstance().getCooldown(this);
    }

    public int getMaxLength() {
        return Config.getInstance().getMaxLength(this);
    }

    public String getAbilityOn() {
        return abilityOn;
    }

    public String getAbilityOff() {
        return abilityOff;
    }

    public String getAbilityPlayer() {
        return abilityPlayer;
    }

    public String getAbilityPlayerOff() {
        return abilityPlayerOff;
    }

    public String getAbilityRefresh() {
        return abilityRefresh;
    }

    public String getName() {
        return StringUtils.getPrettyAbilityString(this);
    }

    @Override
    public String toString() {
        String baseString = name();
        String[] substrings = baseString.split("_");
        String formattedString = "";

        int size = 1;

        for (String string : substrings) {
            formattedString = formattedString.concat(StringUtils.getCapitalized(string));

            if (size < substrings.length) {
                formattedString = formattedString.concat("_");
            }

            size++;
        }

        return formattedString;
    }

    /**
     * Get the permissions for this ability.
     *
     * @param player Player to check permissions for
     * @return true if the player has permissions, false otherwise
     */
    public boolean getPermissions(Player player) {
        switch (this) {
            case BERSERK:
                return Permissions.berserk(player);

            case BLAST_MINING:
                return Permissions.remoteDetonation(player);

            case GIGA_DRILL_BREAKER:
                return Permissions.gigaDrillBreaker(player);

            case GREEN_TERRA:
                return Permissions.greenTerra(player);

            case SERRATED_STRIKES:
                return Permissions.serratedStrikes(player);

            case SKULL_SPLITTER:
                return Permissions.skullSplitter(player);

            case SUPER_BREAKER:
                return Permissions.superBreaker(player);

            case TREE_FELLER:
                return Permissions.treeFeller(player);

            default:
                return false;
        }
    }

    /**
     * Check if a block is affected by this ability.
     *
     * @param blockState the block to check
     * @return true if the block is affected by this ability, false otherwise
     */
    public boolean blockCheck(BlockState blockState) {
        switch (this) {
            case BERSERK:
                return (BlockUtils.affectedByGigaDrillBreaker(blockState) || blockState.getType() == Material.SNOW);

            case GIGA_DRILL_BREAKER:
                return BlockUtils.affectedByGigaDrillBreaker(blockState);

            case GREEN_TERRA:
                return BlockUtils.canMakeMossy(blockState);

            case SUPER_BREAKER:
                return BlockUtils.affectedBySuperBreaker(blockState);

            case TREE_FELLER:
                return BlockUtils.isLog(blockState);

            default:
                return false;
        }
    }

    /**
     * Grabs the associated SubSkillType definition for this SuperAbilityType
     * @return the matching SubSkillType definition for this SuperAbilityType
     */
    public SubSkillType getSubSkillTypeDefinition() {
        return subSkillTypeDefinition;
    }
}
