package com.gmail.nossr50.commands.party;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.events.chat.McMMOPartyChatEvent;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;

public class PCommand implements CommandExecutor {
    private final mcMMO plugin;

    public PCommand (mcMMO plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlayerProfile PP;
        String usage = ChatColor.RED + "Proper usage is /p <party-name> <message>"; //TODO: Needs more locale.

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.commands.party")) {
            return true;
        }


        switch (args.length) {
        case 0:
            if (sender instanceof Player) {
                PP = Users.getProfile((Player) sender);

                if (PP.getAdminChatMode()) {
                    PP.toggleAdminChat();
                }

                PP.togglePartyChat();

                if (PP.getPartyChatMode()) {
                    sender.sendMessage(mcLocale.getString("Commands.Party.Chat.On"));
                }
                else {
                    sender.sendMessage(mcLocale.getString("Commands.Party.Chat.Off"));
                }
            }
            else {
                sender.sendMessage(usage);
            }

            return true;

        default:
            if (sender instanceof Player) {
                Player player = (Player) sender;
                PP = Users.getProfile(player);

                if (!PP.inParty()) {
                    player.sendMessage(mcLocale.getString("Commands.Party.None"));
                    return true;
                }

                String message = args[0];

                for (int i = 1; i < args.length; i++) {
                    message = message + " " + args [i];
                }

                if (PP.getPartyChatMode()) {
                    player.chat(message);
                }
                else {
                    PP.togglePartyChat();
                    player.chat(message);
                    PP.togglePartyChat();
                }
            }
            else {
                if (args.length < 2) {
                    sender.sendMessage(usage);
                    return true;
                }

                if (!Party.getInstance().isParty(args[0])) {
                    sender.sendMessage(mcLocale.getString("Party.InvalidName"));
                    return true;
                }

                String message = args[1];

                for (int i = 2; i < args.length; i++) {
                    message = message + " " + args [i];
                }

                McMMOPartyChatEvent chatEvent = new McMMOPartyChatEvent("Console", args[0], message);
                plugin.getServer().getPluginManager().callEvent(chatEvent);

                if (chatEvent.isCancelled()) {
                    return true;
                }

                message = chatEvent.getMessage();
                String prefix = ChatColor.GREEN + "(" + ChatColor.WHITE + "*Console*" + ChatColor.GREEN + ") ";

                plugin.getLogger().info("[P](" + args[0] + ")" + "<*Console*> " + message);

                for (Player player : Party.getInstance().getOnlineMembers(args[0])) {
                    player.sendMessage(prefix + message);
                }
            }

            return true;
        }
    }
}
