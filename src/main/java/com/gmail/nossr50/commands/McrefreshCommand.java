package com.gmail.nossr50.commands;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import org.bukkit.command.CommandSender;

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
    protected void applyCommandAction(McMMOPlayer mmoPlayer) {
        mmoPlayer.setRecentlyHurt(0);
        mmoPlayer.resetCooldowns();
        mmoPlayer.resetToolPrepMode();
        mmoPlayer.resetAbilityMode();

        mmoPlayer.getPlayer().sendMessage(LocaleLoader.getString("Ability.Generic.Refresh"));
    }

    @Override
    protected void sendSuccessMessage(CommandSender sender, String playerName) {
        sender.sendMessage(LocaleLoader.getString("Commands.mcrefresh.Success", playerName));
    }
}
