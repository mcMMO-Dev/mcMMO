package com.gmail.nossr50.chat;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.events.chat.McMMOPartyChatEvent;

public class PartyChatManager extends ChatManager {
    private Party party;

    protected PartyChatManager(Plugin plugin) {
        super(plugin, Config.getInstance().getPartyDisplayNames(), "Commands.Party.Chat.Prefix");
    }

    public void setParty(Party party) {
        this.party = party;
    }

    @Override
    public void handleChat(String senderName, String displayName, String message, boolean isAsync) {
        handleChat(new McMMOPartyChatEvent(plugin, senderName, displayName, party.getName(), message, isAsync));
    }

    @Override
    protected void sendMessage() {
        for (Player member : party.getOnlineMembers()) {
            member.sendMessage(message);
        }

        plugin.getLogger().info("[P]<" + party.getName() + ">" + message);
    }
}
