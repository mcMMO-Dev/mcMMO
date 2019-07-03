package com.gmail.nossr50.commands;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import org.bukkit.command.CommandSender;

public class GodModeCommand extends ToggleCommand {

    private mcMMO pluginRef;

    public GodModeCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    protected boolean hasOtherPermission(CommandSender sender) {
        return Permissions.mcgodOthers(sender);
    }

    @Override
    protected boolean hasSelfPermission(CommandSender sender) {
        return Permissions.mcgod(sender);
    }

    @Override
    protected void applyCommandAction(McMMOPlayer mcMMOPlayer) {
        mcMMOPlayer.getPlayer().sendMessage(pluginRef.getLocaleManager().getString("Commands.GodMode." + (mcMMOPlayer.getGodMode() ? "Disabled" : "Enabled")));
        mcMMOPlayer.toggleGodMode();
    }

    @Override
    protected void sendSuccessMessage(CommandSender sender, String playerName) {
        sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.GodMode.Toggle", playerName));
    }
}
