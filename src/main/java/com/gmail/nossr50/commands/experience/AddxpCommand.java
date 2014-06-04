package com.gmail.nossr50.commands.experience;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.XPGainReason;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;

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
    protected void handleCommand(Player player, PlayerProfile profile, SkillType skill, int value) {
        if (player != null) {
            UserManager.getPlayer(player).applyXpGain(skill, value, XPGainReason.COMMAND);
        }
        else {
            profile.addXp(skill, value);
            profile.scheduleAsyncSave();
        }
    }

    @Override
    protected void handlePlayerMessageAll(Player player, int value) {
        player.sendMessage(LocaleLoader.getString("Commands.addxp.AwardAll", value));
    }

    @Override
    protected void handlePlayerMessageSkill(Player player, int value, SkillType skill) {
        player.sendMessage(LocaleLoader.getString("Commands.addxp.AwardSkill", value, skill.getName()));
    }
}
