package com.gmail.nossr50.chat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.chat.ChatManager;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Users;

public class ACommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlayerProfile profile;
        String usage = LocaleLoader.getString("Commands.Usage.1", "a", "<" + LocaleLoader.getString("Commands.Usage.Message") + ">");

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.chat.adminchat")) {
            return true;
        }

        switch (args.length) {
        case 0:
            if (sender instanceof Player) {
                profile = Users.getProfile((Player) sender);

                if (profile.getPartyChatMode()) {
                    profile.togglePartyChat();
                }

                profile.toggleAdminChat();

                if (profile.getAdminChatMode()) {
                    sender.sendMessage(LocaleLoader.getString("Commands.AdminChat.On"));
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.AdminChat.Off"));
                }
            }
            else {
                sender.sendMessage(usage);
            }

            return true;

        default:
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
