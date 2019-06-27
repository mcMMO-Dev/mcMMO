package com.gmail.nossr50.commands.chat;

import com.gmail.nossr50.datatypes.chat.ChatMode;
import com.gmail.nossr50.mcMMO;
import org.bukkit.command.CommandSender;

public class AdminChatCommand extends ChatCommand {
    public AdminChatCommand(mcMMO pluginRef) {
        super(ChatMode.ADMIN, pluginRef);
    }

    @Override
    protected void handleChatSending(CommandSender sender, String[] args) {
        pluginRef.getChatManager().processAdminChat(sender.getName(), getDisplayName(sender), buildChatMessage(args, 0));
    }
}
