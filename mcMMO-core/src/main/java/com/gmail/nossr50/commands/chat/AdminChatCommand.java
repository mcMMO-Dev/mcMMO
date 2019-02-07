package com.gmail.nossr50.commands.chat;

import com.gmail.nossr50.datatypes.chat.ChatMode;
import org.bukkit.command.CommandSender;

public class AdminChatCommand extends ChatCommand {
    public AdminChatCommand() {
        super(ChatMode.ADMIN);
    }

    @Override
    protected void handleChatSending(CommandSender sender, String[] args) {
        chatManager.handleChat(sender.getName(), getDisplayName(sender), buildChatMessage(args, 0));
    }
}
