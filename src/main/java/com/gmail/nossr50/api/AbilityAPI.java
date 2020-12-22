package com.gmail.nossr50.api;

import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.skills.BleedTimerTask;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public final class AbilityAPI {
    private AbilityAPI() {}

    public static boolean berserkEnabled(Player player) {
        return mcMMO.getUserManager().queryPlayer(player).getSuperAbilityManager().getAbilityMode(SuperAbilityType.BERSERK);
    }

    public static boolean gigaDrillBreakerEnabled(Player player) {
        return mcMMO.getUserManager().queryPlayer(player).getSuperAbilityManager().getAbilityMode(SuperAbilityType.GIGA_DRILL_BREAKER);
    }

    public static boolean greenTerraEnabled(Player player) {
        return mcMMO.getUserManager().queryPlayer(player).getSuperAbilityManager().getAbilityMode(SuperAbilityType.GREEN_TERRA);
    }

    public static boolean serratedStrikesEnabled(Player player) {
        return mcMMO.getUserManager().queryPlayer(player).getSuperAbilityManager().getAbilityMode(SuperAbilityType.SERRATED_STRIKES);
    }

    public static boolean skullSplitterEnabled(Player player) {
        return mcMMO.getUserManager().queryPlayer(player).getSuperAbilityManager().getAbilityMode(SuperAbilityType.SKULL_SPLITTER);
    }

    public static boolean superBreakerEnabled(Player player) {
        return mcMMO.getUserManager().queryPlayer(player).getSuperAbilityManager().getAbilityMode(SuperAbilityType.SUPER_BREAKER);
    }

    public static boolean treeFellerEnabled(Player player) {
        return mcMMO.getUserManager().queryPlayer(player).getSuperAbilityManager().getAbilityMode(SuperAbilityType.TREE_FELLER);
    }

    public static boolean isAnyAbilityEnabled(Player player) {
        OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer(player);

        for (SuperAbilityType ability : SuperAbilityType.values()) {
            if (mmoPlayer.getSuperAbilityManager().getAbilityMode(ability)) {
                return true;
            }
        }

        return false;
    }

    public static void resetCooldowns(Player player) {
        mcMMO.getUserManager().queryPlayer(player).getPersistentPlayerData().resetCooldowns();
    }

    public static void setBerserkCooldown(Player player, long cooldown) {
        mcMMO.getUserManager().queryPlayer(player).getPersistentPlayerData().setAbilityDATS(SuperAbilityType.BERSERK, cooldown);
    }

    public static void setGigaDrillBreakerCooldown(Player player, long cooldown) {
        mcMMO.getUserManager().queryPlayer(player).getPersistentPlayerData().setAbilityDATS(SuperAbilityType.GIGA_DRILL_BREAKER, cooldown);
    }

    public static void setGreenTerraCooldown(Player player, long cooldown) {
        mcMMO.getUserManager().queryPlayer(player).getPersistentPlayerData().setAbilityDATS(SuperAbilityType.GREEN_TERRA, cooldown);
    }

    public static void setSerratedStrikesCooldown(Player player, long cooldown) {
        mcMMO.getUserManager().queryPlayer(player).getPersistentPlayerData().setAbilityDATS(SuperAbilityType.SERRATED_STRIKES, cooldown);
    }

    public static void setSkullSplitterCooldown(Player player, long cooldown) {
        mcMMO.getUserManager().queryPlayer(player).getPersistentPlayerData().setAbilityDATS(SuperAbilityType.SKULL_SPLITTER, cooldown);
    }

    public static void setSuperBreakerCooldown(Player player, long cooldown) {
        mcMMO.getUserManager().queryPlayer(player).getPersistentPlayerData().setAbilityDATS(SuperAbilityType.SUPER_BREAKER, cooldown);
    }

    public static void setTreeFellerCooldown(Player player, long cooldown) {
        mcMMO.getUserManager().queryPlayer(player).getPersistentPlayerData().setAbilityDATS(SuperAbilityType.TREE_FELLER, cooldown);
    }

    public static boolean isBleeding(LivingEntity entity) {
        return BleedTimerTask.isBleeding(entity);
    }
}
