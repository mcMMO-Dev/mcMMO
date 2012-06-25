package com.gmail.nossr50.api;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.util.Users;

public class AbilityAPI {

    public static boolean berserkEnabled(Player player) {
        return Users.getProfile(player).getAbilityMode(AbilityType.BERSERK);
    }

    public static boolean gigaDrillBreakerEnabled(Player player) {
        return Users.getProfile(player).getAbilityMode(AbilityType.GIGA_DRILL_BREAKER);
    }

    public static boolean greenTerraEnabled(Player player) {
        return Users.getProfile(player).getAbilityMode(AbilityType.GREEN_TERRA);
    }

    public static boolean serratedStrikesEnabled(Player player) {
        return Users.getProfile(player).getAbilityMode(AbilityType.SERRATED_STRIKES);
    }

    public static boolean skullSplitterEnabled(Player player) {
        return Users.getProfile(player).getAbilityMode(AbilityType.SKULL_SPLIITER);
    }

    public static boolean superBreakerEnabled(Player player) {
        return Users.getProfile(player).getAbilityMode(AbilityType.SUPER_BREAKER);
    }

    public static boolean treeFellerEnabled(Player player) {
        return Users.getProfile(player).getAbilityMode(AbilityType.TREE_FELLER);
    }

    public static boolean isAnyAbilityEnabled(Player player) {
        for (AbilityType ability : AbilityType.values()) {
            if (Users.getProfile(player).getAbilityMode(ability)) {
                return true;
            }
        }

        return false;
    }
}
