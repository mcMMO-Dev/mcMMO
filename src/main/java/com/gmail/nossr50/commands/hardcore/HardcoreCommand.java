package com.gmail.nossr50.commands.hardcore;

import org.bukkit.command.CommandSender;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.skills.SkillType;
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
    protected boolean checkEnabled(SkillType skill) {
        if (skill == null) {
            for (SkillType skillType : SkillType.skillList) {
                if (!skillType.getHardcoreStatLossEnabled()) {
                    return false;
                }
            }

            return true;
        }

        return skill.getHardcoreStatLossEnabled();
    }

    @Override
    protected void enable(SkillType skill) {
        toggle(true, skill);
    }

    @Override
    protected void disable(SkillType skill) {
        toggle(false, skill);
    }

    @Override
    protected void modify(CommandSender sender, double newPercentage) {
        Config.getInstance().setHardcoreDeathStatPenaltyPercentage(newPercentage);
        sender.sendMessage(LocaleLoader.getString("Hardcore.DeathStatLoss.PercentageChanged", percent.format(newPercentage / 100.0D)));
    }

    private void toggle(boolean enable, SkillType skill) {
        if (skill == null) {
            for (SkillType skillType : SkillType.nonChildSkills) {
                skillType.setHardcoreStatLossEnabled(enable);
            }
        }
        else {
            skill.setHardcoreStatLossEnabled(enable);
        }

        mcMMO.p.getServer().broadcastMessage(LocaleLoader.getString("Hardcore.Mode." + (enable ? "Enabled" : "Disabled"), LocaleLoader.getString("Hardcore.DeathStatLoss.Name"), (skill == null ? "all skills" : skill.getLocalizedName())));
    }
}