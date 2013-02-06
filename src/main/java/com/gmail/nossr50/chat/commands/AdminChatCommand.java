package com.gmail.nossr50.chat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.chat.ChatManager;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Users;

public class AdminChatCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        McMMOPlayer mcMMOPlayer;

        switch (args.length) {
        case 0:
            if (!(sender instanceof Player)) {
                return false;
            }

            mcMMOPlayer = Users.getPlayer((Player) sender);

            // Can't have both party & admin chat at the same time.
            if (mcMMOPlayer.getPartyChatMode()) {
                mcMMOPlayer.togglePartyChat();
            }

            mcMMOPlayer.toggleAdminChat();

            if (mcMMOPlayer.getAdminChatMode()) {
                sender.sendMessage(LocaleLoader.getString("Commands.AdminChat.On"));
            }
            else {
                sender.sendMessage(LocaleLoader.getString("Commands.AdminChat.Off"));
            }

            return true;

        default:
            if (args.length == 1) {
                if (!(sender instanceof Player)) {
                    return false;
                }

                mcMMOPlayer = Users.getPlayer((Player) sender);

                if (args[0].equalsIgnoreCase("on")) {
                    mcMMOPlayer.setPartyChat(false);
                    mcMMOPlayer.setAdminChat(true);
                    sender.sendMessage(LocaleLoader.getString("Commands.AdminChat.On"));
                    return true;
                }

                if (args[0].equalsIgnoreCase("off")) {
                    mcMMOPlayer.setAdminChat(false);
                    sender.sendMessage(LocaleLoader.getString("Commands.AdminChat.Off"));
                    return true;
                }
            }

            StringBuilder builder = new StringBuilder();
            builder.append(args[0]);

            for (int i = 1; i < args.length; i++) {
                builder.append(" ");
                builder.append(args[i]);
            }

            String message = builder.toString();

            if (sender instanceof Player) {
                Player player = (Player) sender;
                ChatManager.handleAdminChat(mcMMO.p, player.getName(), player.getDisplayName(), message);
            }
            else {
                String ssender = LocaleLoader.getString("Commands.Chat.Console");
                ChatManager.handleAdminChat(mcMMO.p, ssender, ssender, message);
            }

            return true;
        }
    }
}
