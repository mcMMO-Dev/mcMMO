package com.gmail.nossr50.datatypes;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.skills.Excavation;
import com.gmail.nossr50.skills.Herbalism;
import com.gmail.nossr50.skills.Mining;

public enum AbilityType {
    BERSERK(
            LoadProperties.berserkCooldown,
            "Unarmed.Skills.Berserk.On",
            "Unarmed.Skills.Berserk.Off",
            "Unarmed.Skills.Berserk.Other.On",
            "Unarmed.Skills.Berserk.Refresh",
            "Unarmed.Skills.Berserk.Other.Off"),

    SUPER_BREAKER(
            LoadProperties.superBreakerCooldown,
            "Mining.Skills.SuperBreaker.On",
            "Mining.Skills.SuperBreaker.Off",
            "Mining.Skills.SuperBreaker.Other.On",
            "Mining.Skills.SuperBreaker.Refresh",
            "Mining.Skills.SuperBreaker.Other.Off"),

    GIGA_DRILL_BREAKER(
            LoadProperties.gigaDrillBreakerCooldown,
            "Excavation.Skills.GigaDrillBreaker.On",
            "Excavation.Skills.GigaDrillBreaker.Off",
            "Excavation.Skills.GigaDrillBreaker.Other.On",
            "Excavation.Skills.GigaDrillBreaker.Refresh",
            "Excavation.Skills.GigaDrillBreaker.Other.Off"),

    GREEN_TERRA(
            LoadProperties.greenTerraCooldown,
            "Herbalism.Skills.GTe.On",
            "Herbalism.Skills.GTe.Off",
            "Herbalism.Skills.GTe.Other.On",
            "Herbalism.Skills.GTe.Refresh",
            "Herbalism.Skills.GTe.Other.Off"),

    SKULL_SPLIITER(
            LoadProperties.skullSplitterCooldown,
            "Axes.Skills.SS.On",
            "Axes.Skills.SS.Off",
            "Axes.Skills.SS.Other.On",
            "Axes.Skills.SS.Refresh",
            "Axes.Skills.SS.Other.Off"),

    TREE_FELLER(
            LoadProperties.treeFellerCooldown,
            "Woodcutting.Skills.TreeFeller.On",
            "Woodcutting.Skills.TreeFeller.Off",
            "Woodcutting.Skills.TreeFeller.Other.On",
            "Woodcutting.Skills.TreeFeller.Refresh",
            "Woodcutting.Skills.TreeFeller.Other.Off"),

    SERRATED_STRIKES(
            LoadProperties.serratedStrikeCooldown,
            "Swords.Skills.SS.On",
            "Swords.Skills.SS.Off",
            "Swords.Skills.SS.Other.On",
            "Swords.Skills.SS.Refresh",
            "Swords.Skills.SS.Other.Off"),

    BLAST_MINING(
            LoadProperties.blastMiningCooldown,
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
        return mcLocale.getString(this.abilityOn);
    }

    public String getAbilityOff() {
        return mcLocale.getString(this.abilityOff);
    }

    public String getAbilityPlayer(Player player) {
        return mcLocale.getString(this.abilityPlayer, new Object[] {player.getName()});
    }

    public String getAbilityPlayerOff(Player player) {
        return mcLocale.getString(this.abilityPlayerOff, new Object[] {player.getName()});
    }

    public String getAbilityRefresh() {
        return mcLocale.getString(this.abilityRefresh);
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
            return mcPermissions.getInstance().berserk(player);

        case BLAST_MINING:
            return mcPermissions.getInstance().blastMining(player);

        case GIGA_DRILL_BREAKER:
            return mcPermissions.getInstance().gigaDrillBreaker(player);

        case GREEN_TERRA:
            return mcPermissions.getInstance().greenTerra(player);

        case LEAF_BLOWER:
            return mcPermissions.getInstance().leafBlower(player);

        case SERRATED_STRIKES:
            return mcPermissions.getInstance().serratedStrikes(player);

        case SKULL_SPLIITER:
            return mcPermissions.getInstance().skullSplitter(player);

        case SUPER_BREAKER:
            return mcPermissions.getInstance().superBreaker(player);

        case TREE_FELLER:
            return mcPermissions.getInstance().treeFeller(player);

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
            return (Excavation.canBeGigaDrillBroken(material) || material.equals(Material.SNOW));

        case GIGA_DRILL_BREAKER:
            return Excavation.canBeGigaDrillBroken(material);

        case GREEN_TERRA:
            return Herbalism.makeMossy(material);

        case LEAF_BLOWER:
            return material.equals(Material.LEAVES);

        case SUPER_BREAKER:
            return Mining.canBeSuperBroken(material);

        case TREE_FELLER:
            return material.equals(Material.LOG);

        default:
            return false;
        }
    }
}
