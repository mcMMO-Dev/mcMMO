package com.gmail.nossr50.commands.chat;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.chat.ChatManager;
import com.gmail.nossr50.chat.ChatMode;
import com.gmail.nossr50.locale.LocaleLoader;

public class AdminChatCommand extends ChatCommand {
    public AdminChatCommand() {
        super(ChatMode.ADMIN);
    }

    @Override
    protected void handleChatSending(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ChatManager.handleAdminChat(mcMMO.p, player.getName(), player.getDisplayName(), buildChatMessage(args, 0));
        }
        else {
            ChatManager.handleAdminChat(mcMMO.p, LocaleLoader.getString("Commands.Chat.Console"), buildChatMessage(args, 0));
        }
    }
}
