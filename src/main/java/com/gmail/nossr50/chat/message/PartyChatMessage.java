package com.gmail.nossr50.chat.message;

import com.gmail.nossr50.chat.author.Author;
import com.gmail.nossr50.config.ChatConfig;
import com.gmail.nossr50.datatypes.chat.ChatChannel;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import com.google.common.base.Objects;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PartyChatMessage extends AbstractChatMessage {

    private final @NotNull Party party;

    public PartyChatMessage(@NotNull Plugin pluginRef, @NotNull Author author,
            @NotNull Audience audience,
            @NotNull String rawMessage, @NotNull TextComponent componentMessage,
            @NotNull Party party) {
        super(pluginRef, author, audience, rawMessage, componentMessage);
        this.party = party;
    }

    /**
     * The party that this chat message was intended for
     *
     * @return the party that this message was intended for
     */
    public @NotNull Party getParty() {
        return party;
    }

    @Override
    public @NotNull String getAuthorDisplayName() {
        return author.getAuthoredName(ChatChannel.PARTY);
    }

    @Override
    public void sendMessage() {
        /*
         * It should be noted that Party messages don't include console as part of the audience to avoid double messaging
         * The console gets a message that has the party name included, player parties do not
         */

        //Sends to everyone but console
        audience.sendMessage(author, componentMessage);
        final TextComponent spyMessage = LocaleLoader.getTextComponent(
                "Chat.Spy.Party",
                author.getAuthoredName(ChatChannel.PARTY), rawMessage, party.getName());

        //Relay to spies
        messagePartyChatSpies(spyMessage);

        //Console message
        if (ChatConfig.getInstance().isConsoleIncludedInAudience(ChatChannel.PARTY)) {
            mcMMO.p.getChatManager().sendConsoleMessage(author, spyMessage);
        }
    }

    /**
     * Console and Party Chat Spies get a more verbose version of the message Party Chat Spies will
     * get a copy of the message as well
     *
     * @param spyMessage the message to copy to spies
     */
    private void messagePartyChatSpies(@NotNull TextComponent spyMessage) {
        //Find the people with permissions
        for (McMMOPlayer mmoPlayer : UserManager.getPlayers()) {
            final Player player = mmoPlayer.getPlayer();

            //Check for toggled players
            if (mmoPlayer.isPartyChatSpying()) {
                Party adminParty = mmoPlayer.getParty();

                //Only message admins not part of this party
                if (adminParty == null || adminParty != getParty()) {
                    //TODO: Hacky, rewrite later
                    Audience audience = mcMMO.getAudiences().player(player);
                    audience.sendMessage(spyMessage);
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final PartyChatMessage that = (PartyChatMessage) o;
        return Objects.equal(party, that.party);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), party);
    }
}
