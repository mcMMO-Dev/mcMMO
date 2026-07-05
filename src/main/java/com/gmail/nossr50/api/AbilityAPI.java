package com.gmail.nossr50.api;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public final class AbilityAPI {
    private AbilityAPI() {
    }

    public static boolean berserkEnabled(Player player) {
        return hasAbilityEnabled(player, SuperAbilityType.BERSERK);
    }

    public static boolean gigaDrillBreakerEnabled(Player player) {
        return hasAbilityEnabled(player, SuperAbilityType.GIGA_DRILL_BREAKER);
    }

    public static boolean greenTerraEnabled(Player player) {
        return hasAbilityEnabled(player, SuperAbilityType.GREEN_TERRA);
    }

    public static boolean serratedStrikesEnabled(Player player) {
        return hasAbilityEnabled(player, SuperAbilityType.SERRATED_STRIKES);
    }

    public static boolean skullSplitterEnabled(Player player) {
        return hasAbilityEnabled(player, SuperAbilityType.SKULL_SPLITTER);
    }

    public static boolean superBreakerEnabled(Player player) {
        return hasAbilityEnabled(player, SuperAbilityType.SUPER_BREAKER);
    }

    public static boolean treeFellerEnabled(Player player) {
        return hasAbilityEnabled(player, SuperAbilityType.TREE_FELLER);
    }

    public static boolean isAnyAbilityEnabled(Player player) {
        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        if(mmoPlayer == null) {
            return false;
        }

        for (SuperAbilityType ability : SuperAbilityType.values()) {
            if (mmoPlayer.getAbilityMode(ability)) {
                return true;
            }
        }

        return false;
    }

    private static boolean hasAbilityEnabled(Player player, SuperAbilityType ability) {
        McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
        return mmoPlayer != null && mmoPlayer.getAbilityMode(ability);
    }

    public static void resetCooldowns(Player player) {
        McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        if(mmoPlayer == null) {
            return;
        }

        mmoPlayer.resetCooldowns();
    }

    public static void setBerserkCooldown(Player player, long cooldown) {
        setAbilityCooldown(player, SuperAbilityType.BERSERK, cooldown);
    }

    public static void setGigaDrillBreakerCooldown(Player player, long cooldown) {
        setAbilityCooldown(player, SuperAbilityType.GIGA_DRILL_BREAKER, cooldown);
    }

    public static void setGreenTerraCooldown(Player player, long cooldown) {
        setAbilityCooldown(player, SuperAbilityType.GREEN_TERRA, cooldown);
    }

    public static void setSerratedStrikesCooldown(Player player, long cooldown) {
        setAbilityCooldown(player, SuperAbilityType.SERRATED_STRIKES, cooldown);
    }

    public static void setSkullSplitterCooldown(Player player, long cooldown) {
        setAbilityCooldown(player, SuperAbilityType.SKULL_SPLITTER, cooldown);
    }

    public static void setSuperBreakerCooldown(Player player, long cooldown) {
        setAbilityCooldown(player, SuperAbilityType.SUPER_BREAKER, cooldown);
    }

    public static void setTreeFellerCooldown(Player player, long cooldown) {
        setAbilityCooldown(player, SuperAbilityType.TREE_FELLER, cooldown);
    }

    private static void setAbilityCooldown(Player player, SuperAbilityType ability, long cooldown) {
        McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        if(mmoPlayer == null) {
            return;
        }

        mmoPlayer.setAbilityDATS(ability, cooldown);
    }

    public static boolean isBleeding(LivingEntity entity) {
        if (entity.isValid()) {
            return entity.hasMetadata(MetadataConstants.METADATA_KEY_RUPTURE);
        }

        return false;
    }
}
