package com.gmail.nossr50.commands.experience;

import org.bukkit.command.CommandSender;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.SkillUtils;

public class AddxpCommand extends ExperienceCommand {
    @Override
    protected boolean permissionsCheckSelf(CommandSender sender) {
        return Permissions.addxp(sender);
    }

    @Override
    protected boolean permissionsCheckOthers(CommandSender sender) {
        return Permissions.addxpOthers(sender);
    }

    @Override
    protected void handleCommand(SkillType skill) {
        if (player != null) {
            mcMMOPlayer.applyXpGain(skill, value);
        }
        else {
            profile.setSkillXpLevel(skill, value);
        }
    }

    @Override
    protected void handlePlayerMessageAll() {
        player.sendMessage(LocaleLoader.getString("Commands.addxp.AwardAll", value));
    }

    @Override
    protected void handlePlayerMessageSkill() {
        player.sendMessage(LocaleLoader.getString("Commands.addxp.AwardSkill", value, SkillUtils.getSkillName(skill)));
    }
}
