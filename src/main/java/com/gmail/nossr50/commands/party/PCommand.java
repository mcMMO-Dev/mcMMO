package com.gmail.nossr50.commands.party;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.events.chat.McMMOPartyChatEvent;
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
        String usage = ChatColor.RED + "Proper usage is /p <party-name> <message>"; //TODO: Needs more locale.

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

                StringBuffer buffer = new StringBuffer();
                buffer.append(args[0]);

                for (int i = 1; i < args.length; i++) {
                    buffer.append(" ");
                    buffer.append(args[i]);
                }

                String message = buffer.toString();

                McMMOPartyChatEvent chatEvent = new McMMOPartyChatEvent(player.getName(), party.getName(), message);
                plugin.getServer().getPluginManager().callEvent(chatEvent);

                if (chatEvent.isCancelled()) {
                    return true;
                }

                message = chatEvent.getMessage();
                String prefix = ChatColor.GREEN + "(" + ChatColor.WHITE + player.getName() + ChatColor.GREEN + ") ";

                plugin.getLogger().info("[P](" + party.getName() + ")" + "<" + player.getName() + "> " + message);

                for (Player member : party.getOnlineMembers()) {
                    member.sendMessage(prefix + message);
                }
            }
            else {
                if (args.length < 2) {
                    sender.sendMessage(usage);
                    return true;
                }

                if (!PartyManager.getInstance().isParty(args[0])) {
                    sender.sendMessage(LocaleLoader.getString("Party.InvalidName"));
                    return true;
                }

                StringBuffer buffer = new StringBuffer();
                buffer.append(args[1]);

                for (int i = 2; i < args.length; i++) {
                    buffer.append(" ");
                    buffer.append(args[i]);
                }

                String message = buffer.toString();

                McMMOPartyChatEvent chatEvent = new McMMOPartyChatEvent("Console", args[0], message);
                plugin.getServer().getPluginManager().callEvent(chatEvent);

                if (chatEvent.isCancelled()) {
                    return true;
                }

                message = chatEvent.getMessage();
                String prefix = ChatColor.GREEN + "(" + ChatColor.WHITE + "*Console*" + ChatColor.GREEN + ") ";

                plugin.getLogger().info("[P](" + args[0] + ")" + "<*Console*> " + message);

                for (Player member : PartyManager.getInstance().getOnlineMembers(args[0])) {
                    member.sendMessage(prefix + message);
                }
            }

            return true;
        }
    }
}
