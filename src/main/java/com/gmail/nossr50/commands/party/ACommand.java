package com.gmail.nossr50.commands.party;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.events.chat.McMMOAdminChatEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class ACommand implements CommandExecutor {
    private final mcMMO plugin;

    public ACommand (mcMMO plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PlayerProfile profile;
        String usage = ChatColor.RED + "Proper usage is /a <message>"; //TODO: Needs more locale.

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
            StringBuffer buffer = new StringBuffer();
            buffer.append(args[0]);

            for (int i = 1; i < args.length; i++) {
                buffer.append(" ");
                buffer.append(args[i]);
            }

            String message = buffer.toString();

            if (sender instanceof Player) {
                Player player = (Player) sender;

                McMMOAdminChatEvent chatEvent = new McMMOAdminChatEvent(player.getName(), message);
                plugin.getServer().getPluginManager().callEvent(chatEvent);

                if (chatEvent.isCancelled()) {
                    return true;
                }

                message = chatEvent.getMessage();
                String prefix = ChatColor.AQUA + "{" + ChatColor.WHITE + player.getName() + ChatColor.AQUA + "} ";

                plugin.getLogger().info("[A]<" + player.getName() + "> " + message);

                for (Player otherPlayer : plugin.getServer().getOnlinePlayers()) {
                    if (Permissions.getInstance().adminChat(otherPlayer) || otherPlayer.isOp()) {
                        otherPlayer.sendMessage(prefix + message);
                    }
                }
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
                    if (Permissions.getInstance().adminChat(player) || player.isOp()) {
                        player.sendMessage(prefix + message);
                    }
                }
            }

            return true;
        }
    }
}
