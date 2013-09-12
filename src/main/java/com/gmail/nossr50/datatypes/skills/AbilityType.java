package com.gmail.nossr50.datatypes.skills;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.google.common.collect.ImmutableList;

public enum AbilityType {
    BERSERK(
            "Unarmed.Skills.Berserk.Name",
            "Unarmed.Skills.Berserk.On",
            "Unarmed.Skills.Berserk.Off",
            "Unarmed.Skills.Berserk.Other.On",
            "Unarmed.Skills.Berserk.Refresh",
            "Unarmed.Skills.Berserk.Other.Off"),

    SUPER_BREAKER(
            "Mining.Skills.SuperBreaker.Name",
            "Mining.Skills.SuperBreaker.On",
            "Mining.Skills.SuperBreaker.Off",
            "Mining.Skills.SuperBreaker.Other.On",
            "Mining.Skills.SuperBreaker.Refresh",
            "Mining.Skills.SuperBreaker.Other.Off"),

    GIGA_DRILL_BREAKER(
            "Excavation.Skills.GigaDrillBreaker.Name",
            "Excavation.Skills.GigaDrillBreaker.On",
            "Excavation.Skills.GigaDrillBreaker.Off",
            "Excavation.Skills.GigaDrillBreaker.Other.On",
            "Excavation.Skills.GigaDrillBreaker.Refresh",
            "Excavation.Skills.GigaDrillBreaker.Other.Off"),

    GREEN_TERRA(
            "Herbalism.Skills.GTe.Name",
            "Herbalism.Skills.GTe.On",
            "Herbalism.Skills.GTe.Off",
            "Herbalism.Skills.GTe.Other.On",
            "Herbalism.Skills.GTe.Refresh",
            "Herbalism.Skills.GTe.Other.Off"),

    SKULL_SPLITTER(
            "Axes.Skills.SS.Name",
            "Axes.Skills.SS.On",
            "Axes.Skills.SS.Off",
            "Axes.Skills.SS.Other.On",
            "Axes.Skills.SS.Refresh",
            "Axes.Skills.SS.Other.Off"),

    TREE_FELLER(
            "Woodcutting.Skills.TreeFeller.Name",
            "Woodcutting.Skills.TreeFeller.On",
            "Woodcutting.Skills.TreeFeller.Off",
            "Woodcutting.Skills.TreeFeller.Other.On",
            "Woodcutting.Skills.TreeFeller.Refresh",
            "Woodcutting.Skills.TreeFeller.Other.Off"),

    SERRATED_STRIKES(
            "Swords.Skills.SS.Name",
            "Swords.Skills.SS.On",
            "Swords.Skills.SS.Off",
            "Swords.Skills.SS.Other.On",
            "Swords.Skills.SS.Refresh",
            "Swords.Skills.SS.Other.Off"),

    /**
     * Has cooldown - but has to share a skill with Super Breaker, so needs special treatment
     */
    BLAST_MINING(
            "Mining.Blast.Name",
            null,
            null,
            "Mining.Blast.Other.On",
            "Mining.Blast.Refresh",
            null),

    /**
     * No cooldown - always active
     */
    LEAF_BLOWER(
            null,
            null,
            null,
            null,
            null,
            null),

    /**
     * Not a first-class Ability - part of Berserk
     */
    BLOCK_CRACKER(
            null,
            null,
            null,
            null,
            null,
            null);

    private String abilityName;
    private String abilityOn;
    private String abilityOff;
    private String abilityPlayer;
    private String abilityRefresh;
    private String abilityPlayerOff;

    /**
     * Those abilities that have a cooldown saved to the database.
     */
    public static final List<AbilityType> NORMAL_ABILITIES;
    /**
     * Those abilities that do not have a cooldown saved to the database.
     */
    public static final List<AbilityType> NON_NORMAL_ABILITIES;

    static {
        NORMAL_ABILITIES = ImmutableList.of(
                BERSERK,
                SUPER_BREAKER,
                GIGA_DRILL_BREAKER,
                GREEN_TERRA,
                SKULL_SPLITTER,
                TREE_FELLER,
                SERRATED_STRIKES,
                BLAST_MINING
                );
        NON_NORMAL_ABILITIES = ImmutableList.of(
                LEAF_BLOWER,
                BLOCK_CRACKER
                );
    }

    private AbilityType(String abilityName, String abilityOn, String abilityOff, String abilityPlayer, String abilityRefresh, String abilityPlayerOff) {
        this.abilityName = abilityName;
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

    /**
     * May return null
     * @return ability name, or null if unavailable
     */
    public String getAbilityName() {
        if (this.abilityName == null) {
            return null;
        }
        return LocaleLoader.getString(this.abilityName);
    }

    public String getAbilityOn() {
        return LocaleLoader.getString(this.abilityOn);
    }

    public String getAbilityOff() {
        return LocaleLoader.getString(this.abilityOff);
    }

    public String getAbilityPlayer(Player player) {
        return LocaleLoader.getString(this.abilityPlayer, player.getName());
    }

    public String getAbilityPlayerOff(Player player) {
        return LocaleLoader.getString(this.abilityPlayerOff, player.getName());
    }

    public String getAbilityRefresh() {
        return LocaleLoader.getString(this.abilityRefresh);
    }

    public String getConfigString() {
        // If toString() changes, place old code here to not break config.yml
        return this.toString();
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

            case BLOCK_CRACKER:
                return Permissions.blockCracker(player);

            case GIGA_DRILL_BREAKER:
                return Permissions.gigaDrillBreaker(player);

            case GREEN_TERRA:
                return Permissions.greenTerra(player);

            case LEAF_BLOWER:
                return Permissions.leafBlower(player);

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

            case BLOCK_CRACKER:
                return BlockUtils.affectedByBlockCracker(blockState);

            case GIGA_DRILL_BREAKER:
                return BlockUtils.affectedByGigaDrillBreaker(blockState);

            case GREEN_TERRA:
                return BlockUtils.canMakeMossy(blockState);

            case LEAF_BLOWER:
                return BlockUtils.isLeaves(blockState);

            case SUPER_BREAKER:
                return BlockUtils.affectedBySuperBreaker(blockState);

            case TREE_FELLER:
                return BlockUtils.isLog(blockState);

            default:
                return false;
        }
    }

    /**
     * Check to see if ability should be triggered.
     *
     * @param player The player using the ability
     * @param block The block modified by the ability
     * @return true if the ability should activate, false otherwise
     */
    public boolean triggerCheck(Player player, Block block) {
        switch (this) {
            case BERSERK:
            case BLOCK_CRACKER:
            case LEAF_BLOWER:
                return blockCheck(block.getState()) && EventUtils.simulateBlockBreak(block, player, true);

            default:
                return false;
        }
    }
}
