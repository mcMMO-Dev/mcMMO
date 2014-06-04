package com.gmail.nossr50.commands.experience;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.XPGainReason;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Permissions;

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
    protected void handleCommand(Player player, PlayerProfile profile, SkillType skill, int value) {
        float xpRemoved = profile.getSkillXpLevelRaw(skill);
        profile.addLevels(skill, value);

        if (player == null) {
            profile.scheduleAsyncSave();
            return;
        }

        EventUtils.handleLevelChangeEvent(player, skill, value, xpRemoved, true, XPGainReason.COMMAND);
    }

    @Override
    protected void handlePlayerMessageAll(Player player, int value) {
        player.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardAll.1", value));
    }

    @Override
    protected void handlePlayerMessageSkill(Player player, int value, SkillType skill) {
        player.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardSkill.1", value, skill.getName()));
    }
}
