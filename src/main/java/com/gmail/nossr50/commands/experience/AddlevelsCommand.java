package com.gmail.nossr50.commands.experience;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Permissions;
import com.neetgames.mcmmo.skill.RootSkill;
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
    protected void handleCommand(Player player, PlayerProfile profile, RootSkill rootSkill, int value) {
        float xpRemoved = profile.getSkillXpLevelRaw(rootSkill);
        profile.addLevels(rootSkill, value);

        if (player == null) {
            profile.scheduleAsyncSave();
            return;
        }

        EventUtils.tryLevelChangeEvent(player, rootSkill, value, xpRemoved, true, XPGainReason.COMMAND);
    }

    @Override
    protected void handlePlayerMessageAll(Player player, int value, boolean isSilent) {
        if(isSilent)
            return;

        player.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardAll.1", value));
    }

    @Override
    protected void handlePlayerMessageSkill(Player player, int value, RootSkill rootSkill, boolean isSilent) {
        if(isSilent)
            return;

        player.sendMessage(LocaleLoader.getString("Commands.addlevels.AwardSkill.1", value, rootSkill.getLocalizedName()));
    }
}
