package com.gmail.nossr50.api;

import com.gmail.nossr50.datatypes.chat.ChatChannel;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;

public final class ChatAPI {
    private ChatAPI() {
    }

    /**
     * Check if a player is currently talking in party chat.
     *
     * @param player The player to check
     * @return true if the player is using party chat, false otherwise
     */
    public static boolean isUsingPartyChat(Player player) {
        return UserManager.getPlayer(player).getChatChannel() == ChatChannel.PARTY;
    }

    /**
     * Check if a player is currently talking in party chat.
     *
     * @param playerName The name of the player to check
     * @return true if the player is using party chat, false otherwise
     */
    public static boolean isUsingPartyChat(String playerName) {
        return UserManager.getPlayer(playerName).getChatChannel() == ChatChannel.PARTY;
    }

    /**
     * Check if a player is currently talking in admin chat.
     *
     * @param player The player to check
     * @return true if the player is using admin chat, false otherwise
     */
    public static boolean isUsingAdminChat(Player player) {
        return UserManager.getPlayer(player).getChatChannel() == ChatChannel.ADMIN;
    }

    /**
     * Check if a player is currently talking in admin chat.
     *
     * @param playerName The name of the player to check
     * @return true if the player is using admin chat, false otherwise
     */
    public static boolean isUsingAdminChat(String playerName) {
        return UserManager.getPlayer(playerName).getChatChannel() == ChatChannel.ADMIN;
    }

    /**
     * Toggle the party chat mode of a player.
     *
     * @param player The player to toggle party chat on.
     */
    public static void togglePartyChat(Player player) {
        mcMMO.p.getChatManager()
                .setOrToggleChatChannel(UserManager.getPlayer(player), ChatChannel.PARTY);
    }

    /**
     * Toggle the party chat mode of a player.
     *
     * @param playerName The name of the player to toggle party chat on.
     */
    public static void togglePartyChat(String playerName) {
        mcMMO.p.getChatManager()
                .setOrToggleChatChannel(UserManager.getPlayer(playerName), ChatChannel.PARTY);
    }

    /**
     * Toggle the admin chat mode of a player.
     *
     * @param player The player to toggle admin chat on.
     */
    public static void toggleAdminChat(Player player) {
        mcMMO.p.getChatManager()
                .setOrToggleChatChannel(UserManager.getPlayer(player), ChatChannel.ADMIN);
    }

    /**
     * Toggle the admin chat mode of a player.
     *
     * @param playerName The name of the player to toggle party chat on.
     */
    public static void toggleAdminChat(String playerName) {
        mcMMO.p.getChatManager()
                .setOrToggleChatChannel(UserManager.getPlayer(playerName), ChatChannel.ADMIN);
    }
}
