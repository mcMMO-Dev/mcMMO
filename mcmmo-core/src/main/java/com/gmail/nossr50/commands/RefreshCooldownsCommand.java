package com.gmail.nossr50.commands;

import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.mcMMO;
import org.bukkit.command.CommandSender;

public class RefreshCooldownsCommand extends ToggleCommand {

    public RefreshCooldownsCommand(mcMMO pluginRef) {
        super(pluginRef);
    }

    @Override
    protected boolean hasOtherPermission(CommandSender sender) {
        return pluginRef.getPermissionTools().mcrefreshOthers(sender);
    }

    @Override
    protected boolean hasSelfPermission(CommandSender sender) {
        return pluginRef.getPermissionTools().mcrefresh(sender);
    }

    @Override
    protected void applyCommandAction(BukkitMMOPlayer mcMMOPlayer) {
        mcMMOPlayer.setRecentlyHurt(0);
        mcMMOPlayer.resetCooldowns();
        mcMMOPlayer.resetToolPrepMode();
        mcMMOPlayer.resetSuperAbilityMode();

        mcMMOPlayer.getPlayer().sendMessage(pluginRef.getLocaleManager().getString("Ability.Generic.Refresh"));
    }

    @Override
    protected void sendSuccessMessage(CommandSender sender, String playerName) {
        sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.mcrefresh.Success", playerName));
    }
}
