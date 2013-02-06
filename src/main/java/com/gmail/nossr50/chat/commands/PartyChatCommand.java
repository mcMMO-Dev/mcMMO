package com.gmail.nossr50.chat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.chat.ChatManager;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.Users;

public class PartyChatCommand implements CommandExecutor {
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
            if (mcMMOPlayer.getAdminChatMode()) {
                mcMMOPlayer.toggleAdminChat();
            }

            mcMMOPlayer.togglePartyChat();

            if (mcMMOPlayer.getPartyChatMode()) {
                sender.sendMessage(LocaleLoader.getString("Commands.Party.Chat.On"));
            }
            else {
                sender.sendMessage(LocaleLoader.getString("Commands.Party.Chat.Off"));
            }

            return true;

        default:
            if (args.length == 1) {
                if (!(sender instanceof Player)) {
                    return false;
                }

                mcMMOPlayer = Users.getPlayer((Player) sender);

                if (args[0].equalsIgnoreCase("on")) {
                    mcMMOPlayer.setAdminChat(false);
                    mcMMOPlayer.setPartyChat(true);
                    sender.sendMessage(LocaleLoader.getString("Commands.Party.Chat.On"));
                    return true;
                }

                if (args[0].equalsIgnoreCase("off")) {
                    mcMMOPlayer.setPartyChat(false);
                    sender.sendMessage(LocaleLoader.getString("Commands.Party.Chat.Off"));
                    return true;
                }
            }

            if (sender instanceof Player) {
                Player player = (Player) sender;
                Party party = Users.getPlayer(player).getParty();

                if (party == null) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Party.None"));
                    return true;
                }

                StringBuilder builder = new StringBuilder();
                builder.append(args[0]);

                for (int i = 1; i < args.length; i++) {
                    builder.append(" ");
                    builder.append(args[i]);
                }

                String message = builder.toString();
                ChatManager.handlePartyChat(mcMMO.p, party, player.getName(), player.getDisplayName(), message);
            }
            else {
                if (args.length < 2) {
                    sender.sendMessage(LocaleLoader.getString("Party.Specify"));
                    return true;
                }

                Party party = PartyManager.getParty(args[0]);

                if (party == null) {
                    sender.sendMessage(LocaleLoader.getString("Party.InvalidName"));
                    return true;
                }

                StringBuilder builder = new StringBuilder();
                builder.append(args[1]);

                for (int i = 2; i < args.length; i++) {
                    builder.append(" ");
                    builder.append(args[i]);
                }

                String consoleSender = LocaleLoader.getString("Commands.Chat.Console");
                String message = builder.toString();

                ChatManager.handlePartyChat(mcMMO.p, party, consoleSender, consoleSender, message);
            }

            return true;
        }
    }
}
