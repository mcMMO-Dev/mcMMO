package com.gmail.nossr50.api;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.util.player.UserManager;

public final class AbilityAPI {
    private AbilityAPI() {}

    public static boolean berserkEnabled(Player player) {
        return UserManager.getPlayer(player).getProfile().getAbilityMode(AbilityType.BERSERK);
    }

    public static boolean gigaDrillBreakerEnabled(Player player) {
        return UserManager.getPlayer(player).getProfile().getAbilityMode(AbilityType.GIGA_DRILL_BREAKER);
    }

    public static boolean greenTerraEnabled(Player player) {
        return UserManager.getPlayer(player).getProfile().getAbilityMode(AbilityType.GREEN_TERRA);
    }

    public static boolean serratedStrikesEnabled(Player player) {
        return UserManager.getPlayer(player).getProfile().getAbilityMode(AbilityType.SERRATED_STRIKES);
    }

    public static boolean skullSplitterEnabled(Player player) {
        return UserManager.getPlayer(player).getProfile().getAbilityMode(AbilityType.SKULL_SPLITTER);
    }

    public static boolean superBreakerEnabled(Player player) {
        return UserManager.getPlayer(player).getProfile().getAbilityMode(AbilityType.SUPER_BREAKER);
    }

    public static boolean treeFellerEnabled(Player player) {
        return UserManager.getPlayer(player).getProfile().getAbilityMode(AbilityType.TREE_FELLER);
    }

    public static boolean isAnyAbilityEnabled(Player player) {
        PlayerProfile profile = UserManager.getPlayer(player).getProfile();

        for (AbilityType ability : AbilityType.values()) {
            if (profile.getAbilityMode(ability)) {
                return true;
            }
        }

        return false;
    }
}
