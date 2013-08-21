package com.gmail.nossr50.commands.hardcore;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.HardcoreManager;
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
            return !HardcoreManager.getHardcoreStatLossDisabled();
        }
        else {
            return SkillType.getSkill(skill).getHardcoreStatLossEnabled();
        }
    }

    @Override
    protected void enable(String skill) {
        if (skill.equalsIgnoreCase("ALL")) {
            for (SkillType skillType : SkillType.nonChildSkills()) {
                Config.getInstance().setHardcoreStatLossEnabled(skillType, true);
            }
        }
        else {
            Config.getInstance().setHardcoreStatLossEnabled(SkillType.getSkill(skill), true);
        }

        mcMMO.p.getServer().broadcastMessage(LocaleLoader.getString("Hardcore.Mode.Enabled", LocaleLoader.getString("Hardcore.DeathStatLoss.Name"), skill));
    }

    @Override
    protected void disable(String skill) {
        if (skill.equalsIgnoreCase("ALL")) {
            for (SkillType skillType : SkillType.nonChildSkills()) {
                Config.getInstance().setHardcoreStatLossEnabled(skillType, false);
            }
        }
        else {
            Config.getInstance().setHardcoreStatLossEnabled(SkillType.getSkill(skill), false);
        }

        mcMMO.p.getServer().broadcastMessage(LocaleLoader.getString("Hardcore.Mode.Disabled", LocaleLoader.getString("Hardcore.DeathStatLoss.Name"), skill));
    }

    @Override
    protected void modify() {
        Config.getInstance().setHardcoreDeathStatPenaltyPercentage(newPercent);
        sender.sendMessage(LocaleLoader.getString("Hardcore.DeathStatLoss.PercentageChanged", percent.format(newPercent / 100D)));
    }
}