package com.gmail.nossr50.commands.hardcore;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;

public class HardcoreCommand extends HardcoreModeCommand {
    @Override
    protected boolean checkTogglePermissions() {
        return Permissions.hardcoreToggle(sender);
    }

    @Override
    protected boolean checkModifyPermissions() {
        return Permissions.hardcoreModify(sender);
    }

    @Override
    protected boolean checkEnabled(String skill) {
        if (skill.equalsIgnoreCase("ALL")) {
            for (SkillType skillType : SkillType.values()) {
                if (!skillType.getHardcoreStatLossEnabled()) {
                    return false;
                }
            }

            return true;
        }

        return SkillType.getSkill(skill).getHardcoreStatLossEnabled();
    }

    @Override
    protected void enable(String skill) {
        toggle(true);
    }

    @Override
    protected void disable(String skill) {
        toggle(false);
    }

    @Override
    protected void modify() {
        Config.getInstance().setHardcoreDeathStatPenaltyPercentage(newPercent);
        sender.sendMessage(LocaleLoader.getString("Hardcore.DeathStatLoss.PercentageChanged", percent.format(newPercent / 100D)));
    }

    private void toggle(boolean enable) {
        if (skill.equalsIgnoreCase("ALL")) {
            for (SkillType skillType : SkillType.nonChildSkills()) {
                skillType.setHardcoreStatLossEnabled(enable);
            }
        }
        else {
            SkillType.getSkill(skill).setHardcoreStatLossEnabled(enable);
        }

        mcMMO.p.getServer().broadcastMessage(LocaleLoader.getString("Hardcore.Mode." + (enable ? "Enabled" : "Disabled"), LocaleLoader.getString("Hardcore.DeathStatLoss.Name"), skill));
    }
}