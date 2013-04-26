package com.gmail.nossr50.api;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.util.player.UserManager;

public final class AbilityAPI {
    private AbilityAPI() {}

    public static boolean berserkEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(AbilityType.BERSERK);
    }

    public static boolean gigaDrillBreakerEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(AbilityType.GIGA_DRILL_BREAKER);
    }

    public static boolean greenTerraEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(AbilityType.GREEN_TERRA);
    }

    public static boolean serratedStrikesEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(AbilityType.SERRATED_STRIKES);
    }

    public static boolean skullSplitterEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(AbilityType.SKULL_SPLITTER);
    }

    public static boolean superBreakerEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(AbilityType.SUPER_BREAKER);
    }

    public static boolean treeFellerEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(AbilityType.TREE_FELLER);
    }

    public static boolean isAnyAbilityEnabled(Player player) {
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        for (AbilityType ability : AbilityType.values()) {
            if (mcMMOPlayer.getAbilityMode(ability)) {
                return true;
            }
        }

        return false;
    }

    public static void resetCooldowns(Player player) {
        UserManager.getPlayer(player).getProfile().resetCooldowns();
    }

    public static void setBerserkCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).getProfile().setSkillDATS(AbilityType.BERSERK, cooldown);
    }

    public static void setGigaDrillBreakerCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).getProfile().setSkillDATS(AbilityType.GIGA_DRILL_BREAKER, cooldown);
    }

    public static void setGreenTerraCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).getProfile().setSkillDATS(AbilityType.GREEN_TERRA, cooldown);
    }

    public static void setSerratedStrikesCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).getProfile().setSkillDATS(AbilityType.SERRATED_STRIKES, cooldown);
    }

    public static void setSkullSplitterCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).getProfile().setSkillDATS(AbilityType.SKULL_SPLITTER, cooldown);
    }

    public static void setSuperBreakerCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).getProfile().setSkillDATS(AbilityType.SUPER_BREAKER, cooldown);
    }

    public static void setTreeFellerCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).getProfile().setSkillDATS(AbilityType.TREE_FELLER, cooldown);
    }
}
