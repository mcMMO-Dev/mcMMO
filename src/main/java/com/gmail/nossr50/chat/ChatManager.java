package com.gmail.nossr50.chat;

import com.gmail.nossr50.chat.author.Author;
import com.gmail.nossr50.chat.author.ConsoleAuthor;
import com.gmail.nossr50.chat.mailer.AdminChatMailer;
import com.gmail.nossr50.chat.mailer.PartyChatMailer;
import com.gmail.nossr50.config.ChatConfig;
import com.gmail.nossr50.datatypes.chat.ChatChannel;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.text.StringUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

//TODO: Micro optimization - Cache audiences and update cache when needed
public class ChatManager {

    private final @NotNull AdminChatMailer adminChatMailer;
    private final @NotNull PartyChatMailer partyChatMailer;

    private final @NotNull ConsoleAuthor consoleAuthor;
    private final @NotNull Audience consoleAudience;

    private final boolean isChatEnabled;

    public ChatManager(@NotNull mcMMO pluginRef) {
        adminChatMailer = new AdminChatMailer(pluginRef);
        partyChatMailer = new PartyChatMailer(pluginRef);

        this.consoleAuthor = new ConsoleAuthor(LocaleLoader.getString("Chat.Identity.Console"));
        this.consoleAudience = mcMMO.getAudiences().filter((cs) -> cs instanceof ConsoleCommandSender);
        this.isChatEnabled = ChatConfig.getInstance().isChatEnabled();
    }

    /**
     * Handles player messaging when they are either in party chat or admin chat modes
     *
     * @param mmoPlayer target player
     * @param rawMessage the raw message from the player as it was typed
     * @param isAsync whether this is getting processed via async
     */
    public void processPlayerMessage(@NotNull McMMOPlayer mmoPlayer, @NotNull String rawMessage, boolean isAsync) {
        processPlayerMessage(mmoPlayer, mmoPlayer.getChatChannel(), rawMessage, isAsync);
    }

    /**
     * Handles player messaging for a specific chat channel
     *
     * @param mmoPlayer target player
     * @param args the raw command arguments from the player
     * @param chatChannel target channel
     */
    public void processPlayerMessage(@NotNull McMMOPlayer mmoPlayer, @NotNull String[] args, @NotNull ChatChannel chatChannel) {
        String chatMessageWithoutCommand = buildChatMessage(args);

        //Commands are never async
        processPlayerMessage(mmoPlayer, chatChannel, chatMessageWithoutCommand, false);
    }

    /**
     * Handles player messaging for a specific chat channel
     *
     * @param mmoPlayer target player
     * @param chatChannel target chat channel
     * @param rawMessage raw chat message as it was typed
     * @param isAsync whether this is getting processed via async
     */
    private void processPlayerMessage(@NotNull McMMOPlayer mmoPlayer, @NotNull ChatChannel chatChannel, @NotNull String rawMessage, boolean isAsync) {
        switch (chatChannel) {
            case ADMIN:
                adminChatMailer.processChatMessage(mmoPlayer.getPlayerAuthor(), rawMessage, isAsync, Permissions.colorChat(mmoPlayer.getPlayer()));
                break;
            case PARTY:
                partyChatMailer.processChatMessage(mmoPlayer.getPlayerAuthor(), rawMessage, mmoPlayer.getParty(), isAsync, Permissions.colorChat(mmoPlayer.getPlayer()), Misc.isPartyLeader(mmoPlayer));
                break;
            case PARTY_OFFICER:
            case NONE:
                break;
        }
    }

    /**
     * Handles console messaging to admins
     * @param rawMessage raw message from the console
     */
    public void processConsoleMessage(@NotNull String rawMessage) {
        adminChatMailer.processChatMessage(getConsoleAuthor(), rawMessage, false, true);
    }

    /**
     * Handles console messaging to admins
     * @param args raw command args from the console
     */
    public void processConsoleMessage(@NotNull String[] args) {
        processConsoleMessage(buildChatMessage(args));
    }

    /**
     * Handles console messaging to a specific party
     * @param rawMessage raw message from the console
     * @param party target party
     */
    public void processConsoleMessage(@NotNull String rawMessage, @NotNull Party party) {
        partyChatMailer.processChatMessage(getConsoleAuthor(), rawMessage, party, false, true, false);
    }

    /**
     * Gets a console author
     * @return a {@link ConsoleAuthor}
     */
    private @NotNull Author getConsoleAuthor() {
        return consoleAuthor;
    }

    /**
     * Change the chat channel of a {@link McMMOPlayer}
     *  Targeting the channel a player is already in will remove that player from the chat channel
     * @param mmoPlayer target player
     * @param targetChatChannel target chat channel
     */
    public void setOrToggleChatChannel(@NotNull McMMOPlayer mmoPlayer, @NotNull ChatChannel targetChatChannel) {
        if (targetChatChannel == mmoPlayer.getChatChannel()) {
            //Disabled message
            mmoPlayer.getPlayer().sendMessage(LocaleLoader.getString("Chat.Channel.Off", StringUtils.getCapitalized(targetChatChannel.toString())));
            mmoPlayer.setChatMode(ChatChannel.NONE);
        } else {
            mmoPlayer.setChatMode(targetChatChannel);
            mmoPlayer.getPlayer().sendMessage(LocaleLoader.getString("Chat.Channel.On", StringUtils.getCapitalized(targetChatChannel.toString())));
        }
    }

    /**
     * Create a chat message from an array of {@link String}
     * @param args array of {@link String}
     * @return a String built from the array
     */
    private @NotNull String buildChatMessage(@NotNull String[] args) {
        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < args.length; i++) {
            if (i + 1 >= args.length) {
                stringBuilder.append(args[i]);
            } else {
                stringBuilder.append(args[i]).append(" ");
            }
        }

        return stringBuilder.toString();
    }

    /**
     * Whether the player is allowed to send a message to the chat channel they are targeting
     * @param mmoPlayer target player
     * @return true if the player can send messages to that chat channel
     */
    public boolean isMessageAllowed(@NotNull McMMOPlayer mmoPlayer) {
        switch (mmoPlayer.getChatChannel()) {
            case ADMIN:
                if (mmoPlayer.getPlayer().isOp() || Permissions.adminChat(mmoPlayer.getPlayer())) {
                    return true;
                }
                break;
            case PARTY:
                if (mmoPlayer.getParty() != null && Permissions.partyChat(mmoPlayer.getPlayer())) {
                    return true;
                }
                break;
            case PARTY_OFFICER:
            case NONE:
                return false;
        }

        return false;
    }

    /**
     * Sends just the console a message
     * @param author author of the message
     * @param message message contents in component form
     */
    public void sendConsoleMessage(@NotNull Author author, @NotNull TextComponent message) {
        consoleAudience.sendMessage(author, message);
    }

    /**
     * Whether the mcMMO chat system which handles party and admin chat is enabled or disabled
     * @return true if mcMMO chat processing (for party/admin chat) is enabled
     */
    public boolean isChatEnabled() {
        return isChatEnabled;
    }

    /**
     * Whether a specific chat channel is enabled
     * ChatChannels are enabled/disabled via user config
     *
     * If chat is disabled, this always returns false
     * If NONE is passed as a {@link ChatChannel} it will return true
     * @param chatChannel target chat channel
     * @return true if the chat channel is enabled
     */
    public boolean isChatChannelEnabled(@NotNull ChatChannel chatChannel) {
        if (!isChatEnabled) {
            return false;
        } else {
            switch(chatChannel) {
                case ADMIN:
                case PARTY:
                case PARTY_OFFICER:
                    return ChatConfig.getInstance().isChatChannelEnabled(chatChannel);
                case NONE:
                    return true;
                default:
                    return false;
            }
        }
    }

}