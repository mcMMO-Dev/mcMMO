package com.gmail.nossr50.commands.experience;

import org.bukkit.command.CommandSender;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.SkillUtils;

public class AddlevelsCommand extends ExperienceCommand {
    @Override
    protected boolean permissionsCheckSelf(CommandSender sender) {
        return Permissions.addlevels(sender);
    }

    @Override
    protected boolean permissionsCheckOthers(CommandSender sender) {
        return Permissions.addlevelsOthers(sender);
    }

    @Override
    protected void handleCommand(SkillType skill) {
        profile.addLevels(skill, value);
    }

    @Override
    protected void handlePlayerMessageAll() {
        player.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardAll.1", value));
    }

    @Override
    protected void handlePlayerMessageSkill() {
        player.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardSkill.1", value, SkillUtils.getSkillName(skill)));
        
    }
}
