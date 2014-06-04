package com.gmail.nossr50.commands.experience;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.XPGainReason;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Permissions;

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
    protected void handleCommand(Player player, PlayerProfile profile, SkillType skill, int value) {
        int skillLevel = profile.getSkillLevel(skill);
        float xpRemoved = profile.getSkillXpLevelRaw(skill);

        profile.modifySkill(skill, value);

        if (player == null) {
            profile.scheduleAsyncSave();
            return;
        }

        if (value == skillLevel) {
            return;
        }

        EventUtils.handleLevelChangeEvent(player, skill, value, xpRemoved, value > skillLevel, XPGainReason.COMMAND);
    }

    @Override
    protected void handlePlayerMessageAll(Player player, int value) {
        player.sendMessage(LocaleLoader.getString("Commands.mmoedit.AllSkills.1", value));
    }

    @Override
    protected void handlePlayerMessageSkill(Player player, int value, SkillType skill) {
        player.sendMessage(LocaleLoader.getString("Commands.mmoedit.Modified.1", skill.getName(), value));
    }
}
