package com.gmail.nossr50.commands;

import org.bukkit.command.CommandSender;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;

public class McrefreshCommand extends ToggleCommand {
    @Override
    protected boolean hasOtherPermission(CommandSender sender) {
        return Permissions.mcrefreshOthers(sender);
    }

    @Override
    protected boolean hasSelfPermission(CommandSender sender) {
        return Permissions.mcrefresh(sender);
    }

    @Override
    protected void applyCommandAction() {
        mcMMOPlayer.setRecentlyHurt(0);
        mcMMOPlayer.getProfile().resetCooldowns();
        mcMMOPlayer.resetToolPrepMode();
        mcMMOPlayer.resetAbilityMode();

        player.sendMessage(LocaleLoader.getString("Ability.Generic.Refresh"));
    }

    @Override
    protected void sendSuccessMessage(CommandSender sender) {
        sender.sendMessage(LocaleLoader.getString("Commands.mcrefresh.Success", player.getName()));
    }
}
