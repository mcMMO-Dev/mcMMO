package com.gmail.nossr50.datatypes.skills;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.text.StringUtils;
import com.neetgames.mcmmo.player.MMOPlayer;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.neetgames.mcmmo.skill.AbstractRootSkill;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class CoreRootSkill extends AbstractRootSkill {

    @NotNull private final Class<? extends SkillManager> skillManagerClass;

    public CoreRootSkill(@NotNull String skillName, @NotNull Class<? extends SkillManager> clazz) {
        super(mcMMO.p.getName(), StringUtils.getCapitalized(skillName), "mcmmo.skills." + skillName.toLowerCase(Locale.ENGLISH));
        this.skillManagerClass = clazz;
    }

    public @NotNull Class<? extends SkillManager> getSkillManagerClass() {
        return skillManagerClass;
    }

    @Override
    public boolean isPVPPermitted() {
        return Config.getInstance().getPVPEnabled(this);
    }

    @Override
    public boolean isPVEPermitted() {
        return Config.getInstance().getPVEEnabled(this);
    }

    @Override
    public boolean isAffectedByHardcoreModeStatLoss() {
        return Config.getInstance().getHardcoreStatLossEnabled(this);
    }

    @Override
    public boolean getHardcoreVampirismEnabled() {
        return Config.getInstance().getHardcoreVampirismEnabled(this);
    }

    @Override
    public boolean isRootSkillPermitted(@NotNull OnlineMMOPlayer mmoPlayer) {
        return Permissions.skillEnabled((Player) mmoPlayer.getServerAPIPlayerImpl(), this);
    }

    @Override
    public boolean isOffensiveActionAllowed(@NotNull Object victim) {
        return (victim instanceof Player || (victim instanceof Tameable && ((Tameable) victim).isTamed())) ? isPVPPermitted() : isPVEPermitted();
    }
}
