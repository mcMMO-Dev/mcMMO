package com.gmail.nossr50.api;

import com.gmail.nossr50.datatypes.chat.ChatChannel;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class ChatAPI {
    private ChatAPI() {}

    /**
     * Check if a {@link Player} is in the Party chat channel
     *
     * @param player target player
     * @return true if the player is targeting the party chat channel
     * @deprecated Use {@link #isUsingPartyChat(McMMOPlayer)} instead
     */
    @Deprecated
    public static boolean isUsingPartyChat(@NotNull Player player) {
        McMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer(player);

        if(mmoPlayer != null)
            return mmoPlayer.getChatChannel() == ChatChannel.PARTY;
        else
            return false;
    }

    /**
     * Check if a {@link McMMOPlayer} is in the Party chat channel
     *
     * @param mmoPlayer target player
     * @return true if the player is targeting the party chat channel
     */
    public static boolean isUsingPartyChat(@NotNull McMMOPlayer mmoPlayer) {
        return mmoPlayer.getChatChannel() == ChatChannel.PARTY;
    }

    /**
     * Check if a player is currently talking in party chat.
     *
     * @param playerName The name of the player to check
     * @return true if the player is using party chat, false otherwise
     * @deprecated use {@link #isUsingPartyChat(McMMOPlayer)} instead for performance reasons
     */
    @Deprecated
    public static boolean isUsingPartyChat(String playerName) {
        if(mcMMO.getUserManager().queryMcMMOPlayer(playerName) != null) {
            return mcMMO.getUserManager().queryMcMMOPlayer(playerName).getChatChannel() == ChatChannel.PARTY;
        } else {
            return false;
        }
    }

    /**
     * Check if a {@link Player} is in the Admin chat channel
     *
     * @param player target player
     * @return true if the player is targeting the admin chat channel
     * @deprecated Use {@link #isUsingAdminChat(McMMOPlayer)} instead
     */
    @Deprecated
    public static boolean isUsingAdminChat(@NotNull Player player) {
        McMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer(player);

        if(mmoPlayer != null)
            return mmoPlayer.getChatChannel() == ChatChannel.ADMIN;
        else
            return false;
    }

    /**
     * Check if a {@link McMMOPlayer} is in the Admin chat channel
     *
     * @param mmoPlayer target player
     * @return true if the player is targeting the admin chat channel
     */
    public static boolean isUsingAdminChat(@NotNull McMMOPlayer mmoPlayer) {
        return mmoPlayer.getChatChannel() == ChatChannel.ADMIN;
    }

    /**
     * Check if a player is currently talking in admin chat.
     *
     * @param playerName The name of the player to check
     * @return true if the player is using admin chat, false otherwise
     * @deprecated use {@link #isUsingAdminChat(McMMOPlayer)} instead for performance reasons
     */
    @Deprecated
    public static boolean isUsingAdminChat(String playerName) {
        if(mcMMO.getUserManager().queryMcMMOPlayer(playerName) != null) {
            return mcMMO.getUserManager().queryMcMMOPlayer(playerName).getChatChannel() == ChatChannel.ADMIN;
        } else {
            return false;
        }
    }

    /**
     * Toggle the party chat channel of a {@link McMMOPlayer}
     *
     * @param mmoPlayer The player to toggle party chat on.
     */
    public static void togglePartyChat(@NotNull McMMOPlayer mmoPlayer) {
        mcMMO.p.getChatManager().setOrToggleChatChannel(mmoPlayer, ChatChannel.PARTY);
    }

    /**
     * Toggle the party chat mode of a player.
     *
     * @param player The player to toggle party chat on.
     * @deprecated use {@link #togglePartyChat(McMMOPlayer)}
     */
    @Deprecated
    public static void togglePartyChat(Player player) throws NullPointerException {
        mcMMO.p.getChatManager().setOrToggleChatChannel(Objects.requireNonNull(mcMMO.getUserManager().queryPlayer(player)), ChatChannel.PARTY);
    }

    /**
     * Toggle the party chat mode of a player.
     *
     * @param playerName The name of the player to toggle party chat on.
     * @deprecated Use {@link #togglePartyChat(McMMOPlayer)} instead
     */
    @Deprecated
    public static void togglePartyChat(String playerName) throws NullPointerException {
        mcMMO.p.getChatManager().setOrToggleChatChannel(Objects.requireNonNull(mcMMO.getUserManager().queryMcMMOPlayer(playerName)), ChatChannel.PARTY);
    }

    /**
     * Toggle the admin chat channel of a {@link McMMOPlayer}
     *
     * @param mmoPlayer The player to toggle admin chat on.
     */
    public static void toggleAdminChat(@NotNull McMMOPlayer mmoPlayer) {
        mcMMO.p.getChatManager().setOrToggleChatChannel(mmoPlayer, ChatChannel.ADMIN);
    }

    /**
     * Toggle the admin chat mode of a player.
     *
     * @param player The player to toggle admin chat on.
     * @deprecated Use {@link #toggleAdminChat(McMMOPlayer)} instead
     */
    @Deprecated
    public static void toggleAdminChat(Player player) throws NullPointerException {
        mcMMO.p.getChatManager().setOrToggleChatChannel(Objects.requireNonNull(mcMMO.getUserManager().queryPlayer(player)), ChatChannel.ADMIN);
    }

    /**
     * Toggle the admin chat mode of a player.
     *
     * @param playerName The name of the player to toggle party chat on.
     * @deprecated Use {@link #toggleAdminChat(McMMOPlayer)} instead
     */
    @Deprecated
    public static void toggleAdminChat(String playerName) throws NullPointerException {
        mcMMO.p.getChatManager().setOrToggleChatChannel(Objects.requireNonNull(mcMMO.getUserManager().queryMcMMOPlayer(playerName)), ChatChannel.ADMIN);
    }
}
