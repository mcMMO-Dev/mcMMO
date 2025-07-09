package com.gmail.nossr50.api;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public final class AbilityAPI {
    private AbilityAPI() {
    }

    public static boolean berserkEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(SuperAbilityType.BERSERK);
    }

    public static boolean gigaDrillBreakerEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(SuperAbilityType.GIGA_DRILL_BREAKER);
    }

    public static boolean greenTerraEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(SuperAbilityType.GREEN_TERRA);
    }

    public static boolean serratedStrikesEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(SuperAbilityType.SERRATED_STRIKES);
    }

    public static boolean skullSplitterEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(SuperAbilityType.SKULL_SPLITTER);
    }

    public static boolean superBreakerEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(SuperAbilityType.SUPER_BREAKER);
    }

    public static boolean treeFellerEnabled(Player player) {
        return UserManager.getPlayer(player).getAbilityMode(SuperAbilityType.TREE_FELLER);
    }

    public static boolean isAnyAbilityEnabled(Player player) {
        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        for (SuperAbilityType ability : SuperAbilityType.values()) {
            if (mmoPlayer.getAbilityMode(ability)) {
                return true;
            }
        }

        return false;
    }

    public static void resetCooldowns(Player player) {
        UserManager.getPlayer(player).resetCooldowns();
    }

    public static void setBerserkCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(SuperAbilityType.BERSERK, cooldown);
    }

    public static void setGigaDrillBreakerCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(SuperAbilityType.GIGA_DRILL_BREAKER, cooldown);
    }

    public static void setGreenTerraCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(SuperAbilityType.GREEN_TERRA, cooldown);
    }

    public static void setSerratedStrikesCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(SuperAbilityType.SERRATED_STRIKES, cooldown);
    }

    public static void setSkullSplitterCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(SuperAbilityType.SKULL_SPLITTER, cooldown);
    }

    public static void setSuperBreakerCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(SuperAbilityType.SUPER_BREAKER, cooldown);
    }

    public static void setTreeFellerCooldown(Player player, long cooldown) {
        UserManager.getPlayer(player).setAbilityDATS(SuperAbilityType.TREE_FELLER, cooldown);
    }

    public static boolean isBleeding(LivingEntity entity) {
        if (entity.isValid()) {
            return entity.hasMetadata(MetadataConstants.METADATA_KEY_RUPTURE);
        }

        return false;
    }
}
