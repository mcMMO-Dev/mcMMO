package com.gmail.nossr50.commands.party;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.events.chat.McMMOAdminChatEvent;
import com.gmail.nossr50.locale.mcLocale;

public class ACommand implements CommandExecutor {
    private final mcMMO plugin;

    public ACommand (mcMMO plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlayerProfile PP;
        String usage = ChatColor.RED + "Proper usage is /a [message]"; //TODO: Needs more locale.

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.chat.adminchat")) {
            return true;
        }

        switch (args.length) {
        case 0:
            if (sender instanceof Player) {
                PP = Users.getProfile((Player) sender);

                if (PP.getPartyChatMode()) {
                    PP.togglePartyChat();
                }

                PP.toggleAdminChat();

                if (PP.getAdminChatMode()) {
                    sender.sendMessage(mcLocale.getString("Commands.AdminChat.On"));
                }
                else {
                    sender.sendMessage(mcLocale.getString("Commands.AdminChat.Off"));
                }
            }
            else {
                sender.sendMessage(usage);
            }

            return true;

        default:
            String message = args[0];

            for (int i = 1; i < args.length; i++) {
                message = message + " " + args [i];
            }

            if (sender instanceof Player) {
                Player player = (Player) sender;
                PP = Users.getProfile(player);

                PP.toggleAdminChat();
                player.chat(message);
                PP.toggleAdminChat();
            }
            else {
                McMMOAdminChatEvent chatEvent = new McMMOAdminChatEvent("Console", message);
                plugin.getServer().getPluginManager().callEvent(chatEvent);

                if (chatEvent.isCancelled()) {
                    return true;
                }

                message = chatEvent.getMessage();

                String prefix = ChatColor.AQUA + "{" + ChatColor.WHITE + "*Console*" + ChatColor.AQUA + "} ";

                plugin.getLogger().info("[A]<*Console*> " + message);

                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (mcPermissions.getInstance().adminChat(player) || player.isOp()) {
                        player.sendMessage(prefix + message);
                    }
                }
            }

            return true;
        }
    }
}
