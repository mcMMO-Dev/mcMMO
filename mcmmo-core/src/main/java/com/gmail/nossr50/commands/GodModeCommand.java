package com.gmail.nossr50.commands;

import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.mcMMO;
import org.bukkit.command.CommandSender;

public class GodModeCommand extends ToggleCommand {

    public GodModeCommand(mcMMO pluginRef) {
        super(pluginRef);
    }

    @Override
    protected boolean hasOtherPermission(CommandSender sender) {
        return pluginRef.getPermissionTools().mcgodOthers(sender);
    }

    @Override
    protected boolean hasSelfPermission(CommandSender sender) {
        return pluginRef.getPermissionTools().mcgod(sender);
    }

    @Override
    protected void applyCommandAction(BukkitMMOPlayer mcMMOPlayer) {
        mcMMOPlayer.getNative().sendMessage(pluginRef.getLocaleManager().getString("Commands.GodMode." + (mcMMOPlayer.getGodMode() ? "Disabled" : "Enabled")));
        mcMMOPlayer.toggleGodMode();
    }

    @Override
    protected void sendSuccessMessage(CommandSender sender, String playerName) {
        sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.GodMode.Toggle", playerName));
    }
}
