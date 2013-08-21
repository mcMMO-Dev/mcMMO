package com.gmail.nossr50.commands.hardcore;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.HardcoreManager;
import com.gmail.nossr50.util.Permissions;

public class VampirismCommand extends HardcoreModeCommand {
    @Override
    protected boolean checkTogglePermissions() {
        return Permissions.vampirismToggle(sender);
    }

    @Override
    protected boolean checkModifyPermissions() {
        return Permissions.vampirismModify(sender);
    }

    @Override
    protected boolean checkEnabled(String skill) {
        if (skill.equalsIgnoreCase("ALL")) {
            return !HardcoreManager.getHardcoreVampirismDisabled();
        }
        else {
            return SkillType.getSkill(skill).getHardcoreVampirismEnabled();
        }
    }

    @Override
    protected void enable(String skill) {
        if (skill.equalsIgnoreCase("ALL")) {
            for (SkillType skillType : SkillType.nonChildSkills()) {
                Config.getInstance().setHardcoreVampirismEnabled(skillType, true);
            }
        }
        else {
            Config.getInstance().setHardcoreVampirismEnabled(SkillType.getSkill(skill), true);
        }

        mcMMO.p.getServer().broadcastMessage(LocaleLoader.getString("Hardcore.Mode.Enabled", LocaleLoader.getString("Hardcore.Vampirism.Name"), skill));
    }

    @Override
    protected void disable(String skill) {
        if (skill.equalsIgnoreCase("ALL")) {
            for (SkillType skillType : SkillType.nonChildSkills()) {
                Config.getInstance().setHardcoreVampirismEnabled(skillType, false);
            }
        }
        else {
            Config.getInstance().setHardcoreVampirismEnabled(SkillType.getSkill(skill), false);
        }

        mcMMO.p.getServer().broadcastMessage(LocaleLoader.getString("Hardcore.Mode.Disabled", LocaleLoader.getString("Hardcore.Vampirism.Name"), skill));
    }

    @Override
    protected void modify() {
        Config.getInstance().setHardcoreVampirismStatLeechPercentage(newPercent);
        sender.sendMessage(LocaleLoader.getString("Hardcore.Vampirism.PercentageChanged", percent.format(newPercent / 100D)));
    }
}