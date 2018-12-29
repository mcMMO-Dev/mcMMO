package com.gmail.nossr50.commands.hardcore;

import com.gmail.nossr50.datatypes.skills.PrimarySkill;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;

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
    protected boolean checkEnabled(PrimarySkill skill) {
        if (skill == null) {
            for (PrimarySkill primarySkill : PrimarySkill.values()) {
                if (!primarySkill.getHardcoreStatLossEnabled()) {
                    return false;
                }
            }

            return true;
        }

        return skill.getHardcoreStatLossEnabled();
    }

    @Override
    protected void enable(PrimarySkill skill) {
        toggle(true, skill);
    }

    @Override
    protected void disable(PrimarySkill skill) {
        toggle(false, skill);
    }

    @Override
    protected void modify(CommandSender sender, double newPercentage) {
        Config.getInstance().setHardcoreDeathStatPenaltyPercentage(newPercentage);
        sender.sendMessage(LocaleLoader.getString("Hardcore.DeathStatLoss.PercentageChanged", percent.format(newPercentage / 100.0D)));
    }

    private void toggle(boolean enable, PrimarySkill skill) {
        if (skill == null) {
            for (PrimarySkill primarySkill : PrimarySkill.NON_CHILD_SKILLS) {
                primarySkill.setHardcoreStatLossEnabled(enable);
            }
        }
        else {
            skill.setHardcoreStatLossEnabled(enable);
        }

        mcMMO.p.getServer().broadcastMessage(LocaleLoader.getString("Hardcore.Mode." + (enable ? "Enabled" : "Disabled"), LocaleLoader.getString("Hardcore.DeathStatLoss.Name"), (skill == null ? "all skills" : skill.getName())));
    }
}