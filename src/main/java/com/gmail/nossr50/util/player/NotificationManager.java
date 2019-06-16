package com.gmail.nossr50.util.player;

import com.gmail.nossr50.config.hocon.notifications.PlayerNotificationSettings;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.notifications.SensitiveCommandType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerNotificationEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.TextComponentFactory;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Handles all messages sent to the player from mcMMO
 */
public class NotificationManager {

    private HashMap<NotificationType, PlayerNotificationSettings> playerNotificationHashMap;

    public NotificationManager() {
        playerNotificationHashMap = new HashMap<>();

        initMaps();
    }

    private void initMaps() {
        //Copy the map
        playerNotificationHashMap = new HashMap<>(mcMMO.getConfigManager().getConfigNotifications().getNotificationSettingHashMap());
    }


    public void setPlayerNotificationSettings(NotificationType notificationType, PlayerNotificationSettings playerNotificationSettings) {
        playerNotificationHashMap.put(notificationType, playerNotificationSettings);
    }

    /**
     * Grab the settings for a NotificationType
     * @param notificationType target notification type
     * @return the notification settings for this type
     */
    public PlayerNotificationSettings getPlayerNotificationSettings(NotificationType notificationType) {
        return playerNotificationHashMap.get(notificationType);
    }

    /**
     * Sends players notifications from mcMMO
     * Does so by sending out an event so other plugins can cancel it
     * @param player target player
     * @param notificationType notifications defined type
     * @param key              the locale key for the notifications defined message
     */
    public void sendPlayerInformation(Player player, NotificationType notificationType, String key) {
        if (UserManager.getPlayer(player) == null || !UserManager.getPlayer(player).useChatNotifications())
            return;

        TextComponent textComponent = TextComponentFactory.getNotificationTextComponentFromLocale(key);
        McMMOPlayerNotificationEvent customEvent = EventUtils.createAndCallNotificationEvent(player, notificationType, textComponent);

        sendNotification(customEvent);
    }

    /**
     * Builds a text component with one or more parameters
     * @param key locale key
     * @param values parameters
     * @return TextComponent for this message
     */
    public TextComponent buildTextComponent(String key, String... values) {
        return TextComponentFactory.getNotificationMultipleValues(key, values);
    }

    /**
     * Builds a text component without any parameters
     * @param key locale key
     * @return TextComponent for this message
     */
    public TextComponent buildTextComponent(String key) {
        return TextComponentFactory.getNotificationTextComponentFromLocale(key);
    }

    /**
     * Sends players notifications from mcMMO
     * This does this by sending out an event so other plugins can cancel it
     * This event in particular is provided with a source player, and players near the source player are sent the information
     * @param targetPlayer the recipient player for this message
     * @param notificationType type of notification
     * @param key              Locale Key for the string to use with this event
     * @param values           values to be injected into the locale string
     */
    public void sendNearbyPlayersInformation(Player targetPlayer, NotificationType notificationType, String key, String... values)
    {
        sendPlayerInformation(targetPlayer, notificationType, key, values);
    }

    public void sendPlayerInformationChatOnly(Player player, String key, String... values)
    {
        if(UserManager.getPlayer(player) == null || !UserManager.getPlayer(player).useChatNotifications())
            return;

        String preColoredString = LocaleLoader.getString(key, (Object[]) values);
        player.sendMessage(preColoredString);
    }

    public void sendPlayerInformation(Player player, NotificationType notificationType, String key, String... values)
    {
        if(UserManager.getPlayer(player) == null || !UserManager.getPlayer(player).useChatNotifications())
            return;

        TextComponent textComponent = buildTextComponent(key, values);
        McMMOPlayerNotificationEvent customEvent = EventUtils.createAndCallNotificationEvent(player, notificationType, textComponent);

        sendNotification(customEvent);
    }

    /**
     * Handles sending level up notifications to a mcMMOPlayer
     *
     * @param mcMMOPlayer target mcMMOPlayer
     * @param skillName   skill that leveled up
     * @param newLevel    new level of that skill
     */
    public void sendPlayerLevelUpNotification(McMMOPlayer mcMMOPlayer, PrimarySkillType skillName, int levelsGained, int newLevel) {
        if (!mcMMOPlayer.useChatNotifications())
            return;

        TextComponent levelUpTextComponent = TextComponentFactory.getNotificationLevelUpTextComponent(skillName, levelsGained, newLevel);
        McMMOPlayerNotificationEvent customEvent = EventUtils.createAndCallNotificationEvent(mcMMOPlayer.getPlayer(), NotificationType.LEVEL_UP_MESSAGE, levelUpTextComponent);

        sendNotification(customEvent);
    }

    private void sendNotification(McMMOPlayerNotificationEvent customEvent) {
        if (customEvent.isCancelled())
            return;

        Player player = customEvent.getRecipient();
        PlayerNotificationSettings playerNotificationSettings = customEvent.getPlayerNotificationSettings();

        //Text Component found
        if(customEvent.hasTextComponent()) {
            if(playerNotificationSettings.isSendToActionBar()) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, customEvent.getNotificationTextComponent());
            }

            //Chat (System)
            if(playerNotificationSettings.isSendToChat()) {
                if(customEvent.hasTextComponent()) {
                    player.spigot().sendMessage(ChatMessageType.SYSTEM, customEvent.getNotificationTextComponent());
                }
            }
        } else {
            //Chat but without a text component
            if(playerNotificationSettings.isSendToChat()) {
                player.sendMessage(customEvent.getNotificationText());
            }
        }
    }

    public void broadcastTitle(Server server, String title, String subtitle, int i1, int i2, int i3) {
        for (Player player : server.getOnlinePlayers()) {
            player.sendTitle(title, subtitle, i1, i2, i3);
        }
    }

    public void sendPlayerUnlockNotification(McMMOPlayer mcMMOPlayer, SubSkillType subSkillType) {
        if (!mcMMOPlayer.useChatNotifications())
            return;

        //CHAT MESSAGE
        mcMMOPlayer.getPlayer().spigot().sendMessage(TextComponentFactory.getSubSkillUnlockedNotificationComponents(mcMMOPlayer.getPlayer(), subSkillType));

        //Unlock Sound Effect
        SoundManager.sendCategorizedSound(mcMMOPlayer.getPlayer(), mcMMOPlayer.getPlayer().getLocation(), SoundType.SKILL_UNLOCKED, SoundCategory.MASTER);
    }

    /**
     * Convenience method to report info about a command sender using a sensitive command
     *
     * @param commandSender        the command user
     * @param sensitiveCommandType type of command issued
     */
    public void processSensitiveCommandNotification(CommandSender commandSender, SensitiveCommandType sensitiveCommandType, String... args) {
        /*
         * Determine the 'identity' of the one who executed the command to pass as a parameters
         */
        String senderName = LocaleLoader.getString("Server.ConsoleName");

        if (commandSender instanceof Player) {
            senderName = ((Player) commandSender).getDisplayName() + ChatColor.RESET + "-" + ((Player) commandSender).getUniqueId();
        }

        //Send the notification
        switch (sensitiveCommandType) {
            case XPRATE_MODIFY:
                sendAdminNotification(LocaleLoader.getString("Notifications.Admin.XPRate.Start.Others", addItemToFirstPositionOfArray(senderName, args)));
                sendAdminCommandConfirmation(commandSender, LocaleLoader.getString("Notifications.Admin.XPRate.Start.Self", args));
                break;
            case XPRATE_END:
                sendAdminNotification(LocaleLoader.getString("Notifications.Admin.XPRate.End.Others", addItemToFirstPositionOfArray(senderName, args)));
                sendAdminCommandConfirmation(commandSender, LocaleLoader.getString("Notifications.Admin.XPRate.End.Self", args));
                break;
        }
    }

    /**
     * Sends a message to all admins with the admin notification formatting from the locale
     * Admins are currently players with either Operator status or Admin Chat permission
     *
     * @param msg message fetched from locale
     */
    private void sendAdminNotification(String msg) {
        //If its not enabled exit
        if (!mcMMO.getConfigManager().getConfigAdmin().isSendAdminNotifications())
            return;

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.isOp() || Permissions.adminChat(player)) {
                player.sendMessage(LocaleLoader.getString("Notifications.Admin.Format.Others", msg));
            }
        }

        //Copy it out to Console too
        mcMMO.p.getLogger().info(LocaleLoader.getString("Notifications.Admin.Format.Others", msg));
    }

    /**
     * Sends a confirmation message to the CommandSender who just executed an admin command
     *
     * @param commandSender target command sender
     * @param msg           message fetched from locale
     */
    private void sendAdminCommandConfirmation(CommandSender commandSender, String msg) {
        commandSender.sendMessage(LocaleLoader.getString("Notifications.Admin.Format.Self", msg));
    }

    /**
     * Takes an array and an object, makes a new array with object in the first position of the new array,
     * and the following elements in this new array being a copy of the existing array retaining their order
     *
     * @param itemToAdd     the string to put at the beginning of the new array
     * @param existingArray the existing array to be copied to the new array at position [0]+1 relative to their original index
     * @return the new array combining itemToAdd at the start and existing array elements following while retaining their order
     */
    public String[] addItemToFirstPositionOfArray(String itemToAdd, String... existingArray) {
        String[] newArray = new String[existingArray.length + 1];
        newArray[0] = itemToAdd;

        System.arraycopy(existingArray, 0, newArray, 1, existingArray.length);

        return newArray;
    }

    public boolean doesPlayerUseNotifications(Player player) {
        if (UserManager.getPlayer(player) == null)
            return false;
        else
            return UserManager.getPlayer(player).useChatNotifications();
    }
}
