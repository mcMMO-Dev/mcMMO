package com.gmail.nossr50.commands.hardcore;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
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
            for (SkillType skillType : SkillType.values()) {
                if (!skillType.getHardcoreVampirismEnabled()) {
                    return false;
                }
            }

            return true;
        }

        return SkillType.getSkill(skill).getHardcoreVampirismEnabled();
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
        Config.getInstance().setHardcoreVampirismStatLeechPercentage(newPercent);
        sender.sendMessage(LocaleLoader.getString("Hardcore.Vampirism.PercentageChanged", percent.format(newPercent / 100D)));
    }

    private void toggle(boolean enabled) {
        if (skill.equalsIgnoreCase("ALL")) {
            for (SkillType skillType : SkillType.nonChildSkills()) {
                Config.getInstance().setHardcoreVampirismEnabled(skillType, enabled);
            }
        }
        else {
            Config.getInstance().setHardcoreVampirismEnabled(SkillType.getSkill(skill), enabled);
        }

        mcMMO.p.getServer().broadcastMessage(LocaleLoader.getString("Hardcore.Mode." + (enabled ? "Enabled" : "Disabled"), LocaleLoader.getString("Hardcore.Vampirism.Name"), skill));
    }
}