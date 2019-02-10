package com.gmail.nossr50.commands.experience;

import com.gmail.nossr50.core.datatypes.experience.XPGainReason;
import com.gmail.nossr50.core.datatypes.player.PlayerProfile;
import com.gmail.nossr50.core.locale.LocaleLoader;
import com.gmail.nossr50.core.skills.PrimarySkillType;
import com.gmail.nossr50.core.util.EventUtils;
import com.gmail.nossr50.core.util.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
    protected void handleCommand(Player player, PlayerProfile profile, PrimarySkillType skill, int value) {
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
    protected void handlePlayerMessageSkill(Player player, int value, PrimarySkillType skill) {
        player.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardSkill.1", value, skill.getName()));
    }
}
