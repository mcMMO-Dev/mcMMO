package com.gmail.nossr50.api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.runnables.skills.BleedTimerTask;
import com.gmail.nossr50.util.player.UserManager;

public final class AbilityAPI {
    private AbilityAPI() {}

    /**
     * Checks if the ability "Berserk" is active.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The {@link Player} to check
     *
     * @return true if "Berserk" is active
     */
    public static boolean berserkEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(AbilityType.BERSERK);
    }

    /**
     * Checks if the ability "Giga Drill Breaker" is active.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The {@link Player} to check
     *
     * @return true if "Giga Drill Breaker" is active
     */
    public static boolean gigaDrillBreakerEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(AbilityType.GIGA_DRILL_BREAKER);
    }

    /**
     * Checks if the ability "Green Terra" is active.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The {@link Player} to check
     *
     * @return true if "Green Terra" is active
     */
    public static boolean greenTerraEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(AbilityType.GREEN_TERRA);
    }

    /**
     * Checks if the ability "Serrated Strikes" is active.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The {@link Player} to check
     *
     * @return true if "Serrated Strikes" is active
     */
    public static boolean serratedStrikesEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(AbilityType.SERRATED_STRIKES);
    }

    /**
     * Checks if the ability "Skull Splitter" is active.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The {@link Player} to check
     *
     * @return true if "Skull Splitter" is active
     */
    public static boolean skullSplitterEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(AbilityType.SKULL_SPLITTER);
    }

    /**
     * Checks if the ability "Super Breaker" is active.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The {@link Player} to check
     *
     * @return true if "Super Breaker" is active
     */
    public static boolean superBreakerEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(AbilityType.SUPER_BREAKER);
    }

    /**
     * Checks if the ability "Tree Feller" is active.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The {@link Player} to check
     *
     * @return true if "Tree Feller" is active
     */
    public static boolean treeFellerEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(AbilityType.TREE_FELLER);
    }

    /**
     * Checks if any ability is active.
     * </br>
     * This function is designed for API usage.
     *
     * @param player The {@link Player} to check
     *
     * @return true if any ability is active
     */
    public static boolean isAnyAbilityEnabled(Player player) {
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        for (AbilityType ability : AbilityType.values()) {
            if (mcMMOPlayer.getAbilityMode(ability)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Reset the ability cooldowns for a player
     * </br>
     * This function is designed for API usage.
     *
     * @param player The {@link Player} to reset the cooldowns for
     */
    public static void resetCooldowns(Player player) {
        UserManager.getPlayer(player).resetCooldowns();
    }

    /**
     * Set the cooldown for the "Berserk" ability
     * </br>
     * This function is designed for API usage.
     *
     * @param player The {@link Player} to set the cooldown for
     * @param cooldown The cooldown length to set
     */
    public static void setBerserkCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(AbilityType.BERSERK, cooldown);
    }

    /**
     * Set the cooldown for the "Giga Drill Breaker" ability
     * </br>
     * This function is designed for API usage.
     *
     * @param player The {@link Player} to set the cooldown for
     * @param cooldown The cooldown length to set
     */
    public static void setGigaDrillBreakerCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(AbilityType.GIGA_DRILL_BREAKER, cooldown);
    }

    /**
     * Set the cooldown for the "Green Terra" ability
     * </br>
     * This function is designed for API usage.
     *
     * @param player The {@link Player} to set the cooldown for
     * @param cooldown The cooldown length to set
     */
    public static void setGreenTerraCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(AbilityType.GREEN_TERRA, cooldown);
    }

    /**
     * Set the cooldown for the "Serrated Strikes" ability
     * </br>
     * This function is designed for API usage.
     *
     * @param player The {@link Player} to set the cooldown for
     * @param cooldown The cooldown length to set
     */
    public static void setSerratedStrikesCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(AbilityType.SERRATED_STRIKES, cooldown);
    }

    /**
     * Set the cooldown for the "Skull Splitter" ability
     * </br>
     * This function is designed for API usage.
     *
     * @param player The {@link Player} to set the cooldown for
     * @param cooldown The cooldown length to set
     */
    public static void setSkullSplitterCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(AbilityType.SKULL_SPLITTER, cooldown);
    }

    /**
     * Set the cooldown for the "Super Breaker" ability
     * </br>
     * This function is designed for API usage.
     *
     * @param player The {@link Player} to set the cooldown for
     * @param cooldown The cooldown length to set
     */
    public static void setSuperBreakerCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(AbilityType.SUPER_BREAKER, cooldown);
    }

    /**
     * Set the cooldown for the "Tree Feller" ability
     * </br>
     * This function is designed for API usage.
     *
     * @param player The {@link Player} to set the cooldown for
     * @param cooldown The cooldown length to set
     */
    public static void setTreeFellerCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(AbilityType.TREE_FELLER, cooldown);
    }


    /**
     * Check if a LivingEntity is bleeding
     * </br>
     * This function is designed for API usage.
     *
     * @param entity The {@link LivingEntity} to check
     * @return true if entity is bleeding, false otherwise
     */
    public static boolean isBleeding(LivingEntity entity) {
        return BleedTimerTask.isBleeding(entity);
    }
}
