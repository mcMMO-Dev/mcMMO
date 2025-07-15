package com.gmail.nossr50.events.chat;

import com.gmail.nossr50.chat.message.PartyChatMessage;
import com.gmail.nossr50.datatypes.party.Party;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a chat is sent to a party channel
 */
public class McMMOPartyChatEvent extends McMMOChatEvent {
    private final @NotNull String party; //Not going to break the API to rename this for now
    private final @NotNull Party targetParty;

    public McMMOPartyChatEvent(@NotNull Plugin pluginRef, @NotNull PartyChatMessage chatMessage,
            @NotNull Party party, boolean isAsync) {
        super(pluginRef, chatMessage, isAsync);
        this.party = party.getName();
        this.targetParty = party;
    }

    /**
     * @return String name of the party the message will be sent to
     * @deprecated this will be removed in the future
     */
    @Deprecated
    public @NotNull String getParty() {
        return party;
    }

    public @NotNull PartyChatMessage getPartyChatMessage() {
        return (PartyChatMessage) chatMessage;
    }

    /**
     * The authors party
     *
     * @return the party that this message will be delivered to
     */
    public @NotNull Party getAuthorParty() {
        return targetParty;
    }
}
