package com.gmail.nossr50.datatypes.skills;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;

public enum AbilityType {
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

    BLAST_MINING(
            null,
            null,
            "Mining.Blast.Other.On",
            "Mining.Blast.Refresh",
            null),

    LEAF_BLOWER(
            null,
            null,
            null,
            null,
            null);

    private String abilityOn;
    private String abilityOff;
    private String abilityPlayer;
    private String abilityRefresh;
    private String abilityPlayerOff;

    private AbilityType(String abilityOn, String abilityOff, String abilityPlayer, String abilityRefresh, String abilityPlayerOff) {
        this.abilityOn = abilityOn;
        this.abilityOff = abilityOff;
        this.abilityPlayer = abilityPlayer;
        this.abilityRefresh = abilityRefresh;
        this.abilityPlayerOff = abilityPlayerOff;
    }

    public int getCooldown() {
        return Config.getInstance().getCooldown(this);
    }

    public int getMaxTicks() {
        return Config.getInstance().getMaxTicks(this);
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
}
