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
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.Users;

public class PCommand implements CommandExecutor {
    private final mcMMO plugin;

    public PCommand (mcMMO plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlayerProfile profile;
        String usage = LocaleLoader.getString("Commands.Usage.2", new Object[] {"p", "<" + LocaleLoader.getString("Commands.Usage.PartyName") + ">", "<" + LocaleLoader.getString("Commands.Usage.Message") + ">"});

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.party")) {
            return true;
        }

        switch (args.length) {
        case 0:
            if (sender instanceof Player) {
                profile = Users.getProfile((Player) sender);

                if (profile.getAdminChatMode()) {
                    profile.toggleAdminChat();
                }

                profile.togglePartyChat();

                if (profile.getPartyChatMode()) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Party.Chat.On"));
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.Party.Chat.Off"));
                }
            }
            else {
                sender.sendMessage(usage);
            }

            return true;

        default:
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Party party = Users.getProfile(player).getParty();

                if (party == null) {
                    player.sendMessage(LocaleLoader.getString("Commands.Party.None"));
                    return true;
                }

                StringBuilder builder = new StringBuilder();
                builder.append(args[0]);

                for (int i = 1; i < args.length; i++) {
                    builder.append(" ");
                    builder.append(args[i]);
                }

                String message = builder.toString();
                ChatManager.handlePartyChat(plugin, party, player.getName(), player.getDisplayName(), message);
            }
            else {
                if (args.length < 2) {
                    sender.sendMessage(usage);
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

                String ssender = LocaleLoader.getString("Commands.Chat.Console");
                String message = builder.toString();

                ChatManager.handlePartyChat(plugin, party, ssender, ssender, message);
            }

            return true;
        }
    }
}
