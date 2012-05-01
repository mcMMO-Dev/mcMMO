package com.gmail.nossr50.datatypes;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.BlockChecks;
import com.gmail.nossr50.util.Permissions;

public enum AbilityType {
    BERSERK(
            Config.getInstance().getAbilityCooldownBerserk(),
            "Unarmed.Skills.Berserk.On",
            "Unarmed.Skills.Berserk.Off",
            "Unarmed.Skills.Berserk.Other.On",
            "Unarmed.Skills.Berserk.Refresh",
            "Unarmed.Skills.Berserk.Other.Off"),

    SUPER_BREAKER(
            Config.getInstance().getAbilityCooldownSuperBreaker(),
            "Mining.Skills.SuperBreaker.On",
            "Mining.Skills.SuperBreaker.Off",
            "Mining.Skills.SuperBreaker.Other.On",
            "Mining.Skills.SuperBreaker.Refresh",
            "Mining.Skills.SuperBreaker.Other.Off"),

    GIGA_DRILL_BREAKER(
            Config.getInstance().getAbilityCooldownGigaDrillBreaker(),
            "Excavation.Skills.GigaDrillBreaker.On",
            "Excavation.Skills.GigaDrillBreaker.Off",
            "Excavation.Skills.GigaDrillBreaker.Other.On",
            "Excavation.Skills.GigaDrillBreaker.Refresh",
            "Excavation.Skills.GigaDrillBreaker.Other.Off"),

    GREEN_TERRA(
            Config.getInstance().getAbilityCooldownGreenTerra(),
            "Herbalism.Skills.GTe.On",
            "Herbalism.Skills.GTe.Off",
            "Herbalism.Skills.GTe.Other.On",
            "Herbalism.Skills.GTe.Refresh",
            "Herbalism.Skills.GTe.Other.Off"),

    SKULL_SPLIITER(
            Config.getInstance().getAbilityCooldownSkullSplitter(),
            "Axes.Skills.SS.On",
            "Axes.Skills.SS.Off",
            "Axes.Skills.SS.Other.On",
            "Axes.Skills.SS.Refresh",
            "Axes.Skills.SS.Other.Off"),

    TREE_FELLER(
            Config.getInstance().getAbilityCooldownTreeFeller(),
            "Woodcutting.Skills.TreeFeller.On",
            "Woodcutting.Skills.TreeFeller.Off",
            "Woodcutting.Skills.TreeFeller.Other.On",
            "Woodcutting.Skills.TreeFeller.Refresh",
            "Woodcutting.Skills.TreeFeller.Other.Off"),

    SERRATED_STRIKES(
            Config.getInstance().getAbilityCooldownSerratedStrikes(),
            "Swords.Skills.SS.On",
            "Swords.Skills.SS.Off",
            "Swords.Skills.SS.Other.On",
            "Swords.Skills.SS.Refresh",
            "Swords.Skills.SS.Other.Off"),

    BLAST_MINING(
            Config.getInstance().getAbilityCooldownBlastMining(),
            null,
            null,
            "Mining.Blast.Other.On",
            "Mining.Blast.Refresh",
            null),

    LEAF_BLOWER(
            0,
            null,
            null,
            null,
            null,
            null);

    private int cooldown;
    private String abilityOn;
    private String abilityOff;
    private String abilityPlayer;
    private String abilityRefresh;
    private String abilityPlayerOff;

    private AbilityType(int cooldown, String abilityOn, String abilityOff, String abilityPlayer, String abilityRefresh, String abilityPlayerOff) {
        this.cooldown = cooldown;
        this.abilityOn = abilityOn;
        this.abilityOff = abilityOff;
        this.abilityPlayer = abilityPlayer;
        this.abilityRefresh = abilityRefresh;
        this.abilityPlayerOff = abilityPlayerOff;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public String getAbilityOn() {
        return LocaleLoader.getString(this.abilityOn);
    }

    public String getAbilityOff() {
        return LocaleLoader.getString(this.abilityOff);
    }

    public String getAbilityPlayer(Player player) {
        return LocaleLoader.getString(this.abilityPlayer, new Object[] {player.getName()});
    }

    public String getAbilityPlayerOff(Player player) {
        return LocaleLoader.getString(this.abilityPlayerOff, new Object[] {player.getName()});
    }

    public String getAbilityRefresh() {
        return LocaleLoader.getString(this.abilityRefresh);
    }

    /**
     * Get the permissions for this ability.
     *
     * @param player Player to check permissions for
     * @return true if the player has permissions, false otherwise
     */
    public boolean getPermissions(Player player) {
        Permissions permInstance = Permissions.getInstance();

        switch (this) {
        case BERSERK:
            return permInstance.berserk(player);

        case BLAST_MINING:
            return permInstance.blastMining(player);

        case GIGA_DRILL_BREAKER:
            return permInstance.gigaDrillBreaker(player);

        case GREEN_TERRA:
            return permInstance.greenTerra(player);

        case LEAF_BLOWER:
            return permInstance.leafBlower(player);

        case SERRATED_STRIKES:
            return permInstance.serratedStrikes(player);

        case SKULL_SPLIITER:
            return permInstance.skullSplitter(player);

        case SUPER_BREAKER:
            return permInstance.superBreaker(player);

        case TREE_FELLER:
            return permInstance.treeFeller(player);

        default:
            return false;
        }
    }

    /**
     * Check if a block is affected by this ability.
     *
     * @param material The block type to check
     * @return true if the block is affected by this ability, false otherwise
     */
    public boolean blockCheck(Material material) {
        switch (this) {
        case BERSERK:
            return (BlockChecks.canBeGigaDrillBroken(material) || material.equals(Material.SNOW));

        case GIGA_DRILL_BREAKER:
            return BlockChecks.canBeGigaDrillBroken(material);

        case GREEN_TERRA:
            return BlockChecks.makeMossy(material);

        case LEAF_BLOWER:
            return material.equals(Material.LEAVES);

        case SUPER_BREAKER:
            return BlockChecks.canBeSuperBroken(material);

        case TREE_FELLER:
            return material.equals(Material.LOG);

        default:
            return false;
        }
    }
}
