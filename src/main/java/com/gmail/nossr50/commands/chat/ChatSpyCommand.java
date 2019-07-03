package com.gmail.nossr50.commands.chat;

import com.gmail.nossr50.commands.ToggleCommand;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import org.bukkit.command.CommandSender;

public class ChatSpyCommand extends ToggleCommand {

    private mcMMO pluginRef;

    public ChatSpyCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    protected boolean hasOtherPermission(CommandSender sender) {
        return Permissions.adminChatSpyOthers(sender);
    }

    @Override
    protected boolean hasSelfPermission(CommandSender sender) {
        return Permissions.adminChatSpy(sender);
    }

    @Override
    protected void applyCommandAction(McMMOPlayer mcMMOPlayer) {
        mcMMOPlayer.getPlayer().sendMessage(pluginRef.getLocaleManager().getString("Commands.AdminChatSpy." + (mcMMOPlayer.isPartyChatSpying() ? "Disabled" : "Enabled")));
        mcMMOPlayer.togglePartyChatSpying();
    }

    @Override
    protected void sendSuccessMessage(CommandSender sender, String playerName) {
        sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.AdminChatSpy.Toggle", playerName));
    }
}
