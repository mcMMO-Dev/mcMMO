package com.gmail.nossr50.chat.message;

import com.gmail.nossr50.chat.author.Author;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PartyChatMessage extends AbstractChatMessage {

    private final @NotNull Party party;

    public PartyChatMessage(@NotNull Plugin pluginRef, @NotNull Author author, @NotNull Audience audience, @NotNull String rawMessage, @NotNull TextComponent componentMessage, @NotNull Party party) {
        super(pluginRef, author, audience, rawMessage, componentMessage);
        this.party = party;
    }

    /**
     * The party that this chat message was intended for
     * @return the party that this message was intended for
     */
    public @NotNull Party getParty() {
        return party;
    }

    @Override
    public void sendMessage() {
        audience.sendMessage(author, componentMessage);

        //Relay to spies
        TextComponent textComponent = Component.text("[" + getParty().getName() + "] ->" ).append(getChatMessage());
        relayChatToSpies(textComponent);
    }

    /**
     * Party Chat Spies will get a copy of the message as well
     * @param spyMessage the message to copy to spies
     */
    private void relayChatToSpies(@NotNull TextComponent spyMessage) {
        //Find the people with permissions
        for(McMMOPlayer mcMMOPlayer : UserManager.getPlayers()) {
            Player player = mcMMOPlayer.getPlayer();

            //Check for toggled players
            if(mcMMOPlayer.isPartyChatSpying()) {
                Party adminParty = mcMMOPlayer.getParty();

                //Only message admins not part of this party
                if(adminParty == null || adminParty != getParty()) {
                    //TODO: Hacky, rewrite later
                    Audience audience = mcMMO.getAudiences().player(player);
                    audience.sendMessage(spyMessage);
                }
            }
        }
    }
}
