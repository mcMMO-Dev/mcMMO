package com.gmail.nossr50.chat.mailer;

import com.gmail.nossr50.chat.author.Author;
import com.gmail.nossr50.chat.message.ChatMessage;
import com.gmail.nossr50.chat.message.PartyChatMessage;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.events.chat.McMMOChatEvent;
import com.gmail.nossr50.events.chat.McMMOPartyChatEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PartyChatMailer extends AbstractChatMailer {

    public PartyChatMailer(@NotNull Plugin pluginRef) {
        super(pluginRef);
    }

    public void processChatMessage(@NotNull Author author, @NotNull String rawString, @NotNull Party party, boolean isAsync, boolean canColor, boolean isLeader) {
        PartyChatMessage chatMessage = new PartyChatMessage(pluginRef, author, constructPartyAudience(party), rawString, addStyle(author, rawString, canColor, isLeader), party);

        McMMOChatEvent chatEvent = new McMMOPartyChatEvent(pluginRef, chatMessage, party, isAsync);
        Bukkit.getPluginManager().callEvent(chatEvent);

        if(!chatEvent.isCancelled()) {
            sendMail(chatMessage);
        }
    }

    public @NotNull Audience constructPartyAudience(@NotNull Party party) {
        return mcMMO.getAudiences().filter(party.getSamePartyPredicate());
    }

    /**
     * Styles a string using a locale entry
     * @param author message author
     * @param message message contents
     * @param canColor whether to replace colors codes with colors in the raw message
     * @return the styled string, based on a locale entry
     */
    public @NotNull TextComponent addStyle(@NotNull Author author, @NotNull String message, boolean canColor, boolean isLeader) {
        if(canColor) {
            message = LocaleLoader.addColors(message);
        }

        if(isLeader) {
            return Component.text(LocaleLoader.getString("Chat.Style.Party.Leader", author.getAuthoredName(), message));
        } else {
            return Component.text(LocaleLoader.getString("Chat.Style.Party", author.getAuthoredName(), message));
        }
    }

    @Override
    public void sendMail(@NotNull ChatMessage chatMessage) {
        chatMessage.sendMessage();
    }
}
