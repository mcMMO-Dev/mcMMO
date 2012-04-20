package com.gmail.nossr50.commands.party;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
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

        // Console message?
        if (!(sender instanceof Player) && args.length >= 1) {
            String aMessage = args[0];
            for (int i = 1; i <= args.length - 1; i++) {
                aMessage = aMessage + " " + args[i];
            }

            McMMOAdminChatEvent chatEvent = new McMMOAdminChatEvent("Console", aMessage);
            plugin.getServer().getPluginManager().callEvent(chatEvent);

            if(chatEvent.isCancelled()) return true;

            aMessage = chatEvent.getMessage();

            String aPrefix = ChatColor.AQUA + "{" + ChatColor.WHITE + "*Console*" + ChatColor.AQUA + "} ";

            plugin.getLogger().info("[A]<*Console*> " + aMessage);

            for (Player herp : plugin.getServer().getOnlinePlayers()) {
                if (mcPermissions.getInstance().adminChat(herp) || herp.isOp())
                    herp.sendMessage(aPrefix + aMessage);
            }
            return true;
        }

        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (player != null && !mcPermissions.getInstance().adminChat(player) && !player.isOp()) {
            player.sendMessage(ChatColor.YELLOW + "[mcMMO] " + ChatColor.DARK_RED + mcLocale.getString("mcPlayerListener.NoPermission"));
            return true;
        }

        // Not a toggle, a message

        if (args.length >= 1) {
            String aMessage = args[0];
            for (int i = 1; i <= args.length - 1; i++) {
                aMessage = aMessage + " " + args[i];
            }
            
            Users.getProfile(player).toggleAdminChat();
            player.chat(aMessage);
            Users.getProfile(player).toggleAdminChat();

            return true;
        }

        if(player != null)
        {
            PlayerProfile PP = Users.getProfile(player);
            
            if (PP.getPartyChatMode())
                PP.togglePartyChat();
    
            PP.toggleAdminChat();
    
            if (PP.getAdminChatMode()) {
                player.sendMessage(mcLocale.getString("mcPlayerListener.AdminChatOn"));
            } else {
                player.sendMessage(mcLocale.getString("mcPlayerListener.AdminChatOff"));
            }
        }
        return true;
    }
}
