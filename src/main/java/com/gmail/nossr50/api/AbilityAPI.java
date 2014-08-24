package com.gmail.nossr50.api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.runnables.skills.BleedTimerTask;
import com.gmail.nossr50.util.player.UserManager;

public final class AbilityAPI {
    private AbilityAPI() {}

    public static boolean berserkEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(AbilityType.berserk);
    }

    public static boolean gigaDrillBreakerEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(AbilityType.gigaDrillBreaker);
    }

    public static boolean greenTerraEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(AbilityType.greenTerra);
    }

    public static boolean serratedStrikesEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(AbilityType.serratedStrikes);
    }

    public static boolean skullSplitterEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(AbilityType.skullSplitter);
    }

    public static boolean superBreakerEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(AbilityType.superBreaker);
    }

    public static boolean treeFellerEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(AbilityType.treeFeller);
    }

    public static boolean isAnyAbilityEnabled(Player player) {
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        for (AbilityType ability : AbilityType.getAbilities()) {
            if (mcMMOPlayer.getAbilityMode(ability)) {
                return true;
            }
        }

        return false;
    }

    public static void resetCooldowns(Player player) {
        UserManager.getPlayer(player).resetCooldowns();
    }

    public static void setBerserkCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(AbilityType.berserk, cooldown);
    }

    public static void setGigaDrillBreakerCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(AbilityType.gigaDrillBreaker, cooldown);
    }

    public static void setGreenTerraCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(AbilityType.greenTerra, cooldown);
    }

    public static void setSerratedStrikesCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(AbilityType.serratedStrikes, cooldown);
    }

    public static void setSkullSplitterCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(AbilityType.skullSplitter, cooldown);
    }

    public static void setSuperBreakerCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(AbilityType.superBreaker, cooldown);
    }

    public static void setTreeFellerCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(AbilityType.treeFeller, cooldown);
    }

    public static boolean isBleeding(LivingEntity entity) {
        return BleedTimerTask.isBleeding(entity);
    }
}
