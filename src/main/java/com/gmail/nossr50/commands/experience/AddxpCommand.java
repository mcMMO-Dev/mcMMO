package com.gmail.nossr50.commands.experience;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.neetgames.mcmmo.skill.RootSkill;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
    protected void handleCommand(Player player, PlayerProfile profile, RootSkill rootSkill, int value) {
        if (player != null) {
            //Check if player profile is loaded
            if(mcMMO.getUserManager().getPlayer(player) == null)
                return;

            mcMMO.getUserManager().getPlayer(player).applyXpGain(rootSkill, value, XPGainReason.COMMAND, XPGainSource.COMMAND);
        }
        else {
            profile.addXp(rootSkill, value);
            profile.scheduleAsyncSave();
        }
    }

    @Override
    protected void handlePlayerMessageAll(Player player, int value, boolean isSilent) {
        if(isSilent)
            return;

        player.sendMessage(LocaleLoader.getString("Commands.addxp.AwardAll", value));
    }

    @Override
    protected void handlePlayerMessageSkill(Player player, int value, RootSkill rootSkill, boolean isSilent) {
        if(isSilent)
            return;

        player.sendMessage(LocaleLoader.getString("Commands.addxp.AwardSkill", value, rootSkill.getName()));
    }
}
