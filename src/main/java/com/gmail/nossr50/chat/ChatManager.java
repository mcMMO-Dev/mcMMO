package com.gmail.nossr50.chat;

import com.gmail.nossr50.chat.author.Author;
import com.gmail.nossr50.chat.author.ConsoleAuthor;
import com.gmail.nossr50.chat.mailer.AdminChatMailer;
import com.gmail.nossr50.chat.mailer.PartyChatMailer;
import com.gmail.nossr50.datatypes.chat.ChatChannel;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//TODO: Micro optimization - Cache audiences and update cache when needed
public class ChatManager {

    private final @NotNull AdminChatMailer adminChatMailer;
    private final @NotNull PartyChatMailer partyChatMailer;

    private @Nullable ConsoleAuthor consoleAuthor;

    public ChatManager(@NotNull mcMMO pluginRef) {
        adminChatMailer = new AdminChatMailer(pluginRef);
        partyChatMailer = new PartyChatMailer(pluginRef);
    }

    /**
     * Handles player messaging when they are either in party chat or admin chat modes
     * @param mmoPlayer target player
     * @param rawMessage the raw message from the player as it was typed
     * @param isAsync whether or not this is getting processed via async
     */
    public void processPlayerMessage(@NotNull McMMOPlayer mmoPlayer, @NotNull String rawMessage, boolean isAsync) {
        processPlayerMessage(mmoPlayer, mmoPlayer.getChatChannel(), rawMessage, isAsync);
    }

    /**
     * Handles player messaging for a specific chat channel
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
     * @param mmoPlayer target player
     * @param chatChannel target chat channel
     * @param rawMessage raw chat message as it was typed
     * @param isAsync whether or not this is getting processed via async
     */
    private void processPlayerMessage(@NotNull McMMOPlayer mmoPlayer, @NotNull ChatChannel chatChannel, @NotNull String rawMessage, boolean isAsync) {
        switch (chatChannel) {
            case ADMIN:
                adminChatMailer.processChatMessage(mmoPlayer.getAdminAuthor(), rawMessage, isAsync, Permissions.colorChat(mmoPlayer.getPlayer()));
                break;
            case PARTY:
                partyChatMailer.processChatMessage(mmoPlayer.getPartyAuthor(), rawMessage, mmoPlayer.getParty(), isAsync, Permissions.colorChat(mmoPlayer.getPlayer()));
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
        partyChatMailer.processChatMessage(getConsoleAuthor(), rawMessage, party, false, true);
    }

    /**
     * Handles console messaging to a specific party
     * @param args raw command args from the console
     * @param party target party
     */
    public void processConsoleMessage(@NotNull String[] args, @NotNull Party party) {
        String chatMessageWithoutCommand = buildChatMessage(args);

        processConsoleMessage(chatMessageWithoutCommand, party);
    }

    /**
     * Gets a console author
     * Constructs one if it doesn't already exist
     * @return a {@link ConsoleAuthor}
     */
    private @NotNull Author getConsoleAuthor() {
        if (consoleAuthor == null) {
            consoleAuthor = new ConsoleAuthor(LocaleLoader.getString("Chat.Identity.Console"));
        }

        return consoleAuthor;
    }

    /**
     * Change the chat channel of a {@link McMMOPlayer}
     *  Targeting the channel a player is already in will remove that player from the chat channel
     * @param mmoPlayer target player
     * @param targetChatChannel target chat channel
     */
    public void setOrToggleChatChannel(@NotNull McMMOPlayer mmoPlayer, @NotNull ChatChannel targetChatChannel) {
        if(targetChatChannel == mmoPlayer.getChatChannel()) {
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
            if(i + 1 >= args.length) {
                stringBuilder.append(args[i]);
            } else {
                stringBuilder.append(args[i]).append(" ");
            }
        }

        return stringBuilder.toString();
    }

    /**
     * Whether or not the player is allowed to send a message to the chat channel they are targeting
     * @param mmoPlayer target player
     * @return true if the player can send messages to that chat channel
     */
    public boolean isMessageAllowed(@NotNull McMMOPlayer mmoPlayer) {
        switch (mmoPlayer.getChatChannel()) {
            case ADMIN:
                if(mmoPlayer.getPlayer().isOp() || Permissions.adminChat(mmoPlayer.getPlayer())) {
                    return true;
                }
                break;
            case PARTY:
                if(mmoPlayer.getParty() != null) {
                    return true;
                }
                break;
            case PARTY_OFFICER:
            case NONE:
                return false;
        }

        return false;
    }
}

