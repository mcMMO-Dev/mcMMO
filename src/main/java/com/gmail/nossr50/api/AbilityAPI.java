package com.gmail.nossr50.api;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.player.UserManager;

public final class AbilityAPI {
    private AbilityAPI() {}

    public static boolean berserkEnabled(Player player) {
        return UserManager.getPlayer(player).getSkillManager(SkillType.UNARMED).getAbilityMode();
    }

    public static boolean gigaDrillBreakerEnabled(Player player) {
        return UserManager.getPlayer(player).getSkillManager(SkillType.EXCAVATION).getAbilityMode();
    }

    public static boolean greenTerraEnabled(Player player) {
        return UserManager.getPlayer(player).getSkillManager(SkillType.HERBALISM).getAbilityMode();
    }

    public static boolean serratedStrikesEnabled(Player player) {
        return UserManager.getPlayer(player).getSkillManager(SkillType.SWORDS).getAbilityMode();
    }

    public static boolean skullSplitterEnabled(Player player) {
        return UserManager.getPlayer(player).getSkillManager(SkillType.AXES).getAbilityMode();
    }

    public static boolean superBreakerEnabled(Player player) {
        return UserManager.getPlayer(player).getSkillManager(SkillType.MINING).getAbilityMode();
    }

    public static boolean treeFellerEnabled(Player player) {
        return UserManager.getPlayer(player).getSkillManager(SkillType.WOODCUTTING).getAbilityMode();
    }

    public static boolean isAnyAbilityEnabled(Player player) {
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        for (SkillManager skillManager : mcMMOPlayer.getSkillManagers().values()) {
            if (skillManager.getAbilityMode()) {
                return true;
            }
        }

        return false;
    }
}
