package com.gmail.nossr50.commands.experience;

import org.bukkit.command.CommandSender;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.SkillUtils;

public class MmoeditCommand extends ExperienceCommand {
    @Override
    protected boolean permissionsCheckSelf(CommandSender sender) {
        return Permissions.mmoedit(sender);
    }

    @Override
    protected boolean permissionsCheckOthers(CommandSender sender) {
        return Permissions.mmoeditOthers(sender);
    }

    @Override
    protected void handleCommand(SkillType skill) {
        profile.modifySkill(skill, value);
    }

    @Override
    protected void handlePlayerMessageAll() {
        player.sendMessage(LocaleLoader.getString("Commands.mmoedit.AllSkills.1", value));
    }

    @Override
    protected void handlePlayerMessageSkill() {
        player.sendMessage(LocaleLoader.getString("Commands.mmoedit.Modified.1", SkillUtils.getSkillName(skill), value));
    }
}
