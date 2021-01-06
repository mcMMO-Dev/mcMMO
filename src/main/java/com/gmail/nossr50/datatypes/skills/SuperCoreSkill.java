package com.gmail.nossr50.datatypes.skills;

import com.gmail.nossr50.util.skills.PerksUtils;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.neetgames.mcmmo.skill.RootSkill;
import com.neetgames.mcmmo.skill.SuperSkill;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SuperCoreSkill extends CoreSkill implements SuperSkill {

    private final int defaultCooldown;

    public SuperCoreSkill(@NotNull String pluginName, @NotNull String skillName, @Nullable String permission, @NotNull RootSkill parentSkill, int defaultCooldown) {
        super(pluginName, skillName, permission, parentSkill);
        this.defaultCooldown = defaultCooldown;
    }

    @Override
    public int getDefaultCooldown() {
        return defaultCooldown;
    }

    @Override
    public int getCooldown(@NotNull OnlineMMOPlayer onlineMMOPlayer) {
        return PerksUtils.handleCooldownPerks((Player) onlineMMOPlayer.getServerAPIPlayerImpl(), defaultCooldown);
    }
}
