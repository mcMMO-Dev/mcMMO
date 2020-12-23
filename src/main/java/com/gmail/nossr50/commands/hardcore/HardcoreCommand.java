package com.gmail.nossr50.commands.hardcore;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.neetgames.mcmmo.skill.RootSkill;
import org.bukkit.command.CommandSender;

public class HardcoreCommand extends HardcoreModeCommand {
    @Override
    protected boolean checkTogglePermissions(CommandSender sender) {
        return Permissions.hardcoreToggle(sender);
    }

    @Override
    protected boolean checkModifyPermissions(CommandSender sender) {
        return Permissions.hardcoreModify(sender);
    }

    @Override
    protected boolean checkEnabled(RootSkill rootSkill) {
        if (skill == null) {
            for (RootSkill rootSkill : PrimarySkillType.values()) {
                if (!primarySkillType.getHardcoreStatLossEnabled()) {
                    return false;
                }
            }

            return true;
        }

        return skill.getHardcoreStatLossEnabled();
    }

    @Override
    protected void enable(RootSkill rootSkill) {
        toggle(true, skill);
    }

    @Override
    protected void disable(RootSkill rootSkill) {
        toggle(false, skill);
    }

    @Override
    protected void modify(CommandSender sender, double newPercentage) {
        Config.getInstance().setHardcoreDeathStatPenaltyPercentage(newPercentage);
        sender.sendMessage(LocaleLoader.getString("Hardcore.DeathStatLoss.PercentageChanged", percent.format(newPercentage / 100.0D)));
    }

    private void toggle(boolean enable, RootSkill rootSkill) {
        if (skill == null) {
            for (RootSkill rootSkill : PrimarySkillType.NON_CHILD_SKILLS) {
                primarySkillType.setHardcoreStatLossEnabled(enable);
            }
        }
        else {
            skill.setHardcoreStatLossEnabled(enable);
        }

        mcMMO.p.getServer().broadcastMessage(LocaleLoader.getString("Hardcore.Mode." + (enable ? "Enabled" : "Disabled"), LocaleLoader.getString("Hardcore.DeathStatLoss.Name"), (skill == null ? "all skills" : skill.getName())));
    }
}