package com.gmail.nossr50.chat;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.events.chat.McMMOPartyChatEvent;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.events.chat.McMMOChatEvent;
import com.gmail.nossr50.locale.LocaleLoader;

public abstract class ChatManager {
    protected Plugin plugin;
    protected boolean useDisplayNames;
    protected String chatPrefix;

    protected String senderName;
    protected String displayName;
    protected String message;

    protected ChatManager(Plugin plugin, boolean useDisplayNames, String chatPrefix) {
        this.plugin = plugin;
        this.useDisplayNames = useDisplayNames;
        this.chatPrefix = chatPrefix;
    }

    protected void handleChat(McMMOChatEvent event) {
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        senderName = event.getSender();
        displayName = useDisplayNames ? event.getDisplayName() : senderName;
        message = LocaleLoader.formatString(chatPrefix, displayName) + " " + event.getMessage();

        sendMessage();

        /*
         * Party Chat Spying
         * Party messages will be copied to people with the mcmmo.admin.chatspy permission node
         */
        if(event instanceof McMMOPartyChatEvent)
        {
            //We need to grab the party chat name
            McMMOPartyChatEvent partyChatEvent = (McMMOPartyChatEvent) event;

            //Find the people with permissions
            for(Player player : event.getPlugin().getServer().getOnlinePlayers())
            {
                //Check for toggled players
                if(UserManager.getPlayer(player).isPartyChatSpying())
                {
                    Party adminParty = UserManager.getPlayer(player).getParty();

                    //Only message admins not part of this party
                    if(adminParty != null && !adminParty.getName().equalsIgnoreCase(partyChatEvent.getParty()))
                    {
                        //TODO: Incorporate JSON
                        player.sendMessage(ChatColor.GOLD+"[SPY: "+ChatColor.GREEN+partyChatEvent.getParty()+ChatColor.GOLD+"] "+message);
                    }
                }
            }
        }
    }

    public void handleChat(String senderName, String message) {
        handleChat(senderName, senderName, message, false);
    }

    public void handleChat(Player player, String message, boolean isAsync) {
        handleChat(player.getName(), player.getDisplayName(), message, isAsync);
    }

    public void handleChat(String senderName, String displayName, String message) {
        handleChat(senderName, displayName, message, false);
    }

    public abstract void handleChat(String senderName, String displayName, String message, boolean isAsync);

    protected abstract void sendMessage();
}
