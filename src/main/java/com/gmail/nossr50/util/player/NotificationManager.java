package com.gmail.nossr50.util.player;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.notifications.SensitiveCommandType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerNotificationEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
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

public class NotificationManager {
    /**
     * Sends players notifications from mcMMO
     * Does so by sending out an event so other plugins can cancel it
     * @param player target player
     * @param notificationType notifications defined type
     * @param key the locale key for the notifications defined message
     */
    public static void sendPlayerInformation(Player player, NotificationType notificationType, String key)
    {
        if(UserManager.getPlayer(player) == null || !UserManager.getPlayer(player).useChatNotifications())
            return;

        ChatMessageType destination = AdvancedConfig.getInstance().doesNotificationUseActionBar(notificationType) ? ChatMessageType.ACTION_BAR : ChatMessageType.SYSTEM;

        TextComponent message = TextComponentFactory.getNotificationTextComponentFromLocale(key);
        McMMOPlayerNotificationEvent customEvent = checkNotificationEvent(player, notificationType, destination, message);

        sendNotification(player, customEvent);
    }


    public static boolean doesPlayerUseNotifications(Player player)
    {
        if(UserManager.getPlayer(player) == null)
            return false;
        else
            return UserManager.getPlayer(player).useChatNotifications();
    }

    /**
     * Sends players notifications from mcMMO
     * This does this by sending out an event so other plugins can cancel it
     * This event in particular is provided with a source player, and players near the source player are sent the information
     * @param targetPlayer the recipient player for this message
     * @param notificationType type of notification
     * @param key Locale Key for the string to use with this event
     * @param values values to be injected into the locale string
     */
    public static void sendNearbyPlayersInformation(Player targetPlayer, NotificationType notificationType, String key, String... values)
    {
        sendPlayerInformation(targetPlayer, notificationType, key, values);
    }

    public static void sendPlayerInformationChatOnly(Player player, String key, String... values)
    {
        if(UserManager.getPlayer(player) == null || !UserManager.getPlayer(player).useChatNotifications())
            return;

        String preColoredString = LocaleLoader.getString(key, (Object[]) values);
        player.sendMessage(preColoredString);
    }

    public static void sendPlayerInformationChatOnlyPrefixed(Player player, String key, String... values)
    {
        if(UserManager.getPlayer(player) == null || !UserManager.getPlayer(player).useChatNotifications())
            return;

        String preColoredString = LocaleLoader.getString(key, (Object[]) values);
        String prefixFormattedMessage = LocaleLoader.getString("mcMMO.Template.Prefix", preColoredString);
        player.sendMessage(prefixFormattedMessage);
    }

    public static void sendPlayerInformation(Player player, NotificationType notificationType, String key, String... values)
    {
        if(UserManager.getPlayer(player) == null || !UserManager.getPlayer(player).useChatNotifications())
            return;

        ChatMessageType destination = AdvancedConfig.getInstance().doesNotificationUseActionBar(notificationType) ? ChatMessageType.ACTION_BAR : ChatMessageType.SYSTEM;

        TextComponent message = TextComponentFactory.getNotificationMultipleValues(key, values);
        McMMOPlayerNotificationEvent customEvent = checkNotificationEvent(player, notificationType, destination, message);

        sendNotification(player, customEvent);
    }

    private static void sendNotification(Player player, McMMOPlayerNotificationEvent customEvent) {
        if (customEvent.isCancelled())
            return;

        //If the message is being sent to the action bar we need to check if the copy if a copy is sent to the chat system
        if(customEvent.getChatMessageType() == ChatMessageType.ACTION_BAR)
        {
            player.spigot().sendMessage(customEvent.getChatMessageType(), customEvent.getNotificationTextComponent());

            if(customEvent.isMessageAlsoBeingSentToChat())
            {
                //Send copy to chat system
                player.spigot().sendMessage(ChatMessageType.SYSTEM, customEvent.getNotificationTextComponent());
            }
        } else {
            player.spigot().sendMessage(customEvent.getChatMessageType(), customEvent.getNotificationTextComponent());
        }
    }

    private static McMMOPlayerNotificationEvent checkNotificationEvent(Player player, NotificationType notificationType, ChatMessageType destination, TextComponent message) {
        //Init event
        McMMOPlayerNotificationEvent customEvent = new McMMOPlayerNotificationEvent(player,
                notificationType, message, destination, AdvancedConfig.getInstance().doesNotificationSendCopyToChat(notificationType));

        //Call event
        Bukkit.getServer().getPluginManager().callEvent(customEvent);
        return customEvent;
    }

    /**
     * Handles sending level up notifications to a mcMMOPlayer
     * @param mcMMOPlayer target mcMMOPlayer
     * @param skillName skill that leveled up
     * @param newLevel new level of that skill
     */
    public static void sendPlayerLevelUpNotification(McMMOPlayer mcMMOPlayer, PrimarySkillType skillName, int levelsGained, int newLevel)
    {
        if(!mcMMOPlayer.useChatNotifications())
            return;

        ChatMessageType destination = AdvancedConfig.getInstance().doesNotificationUseActionBar(NotificationType.LEVEL_UP_MESSAGE) ? ChatMessageType.ACTION_BAR : ChatMessageType.SYSTEM;

        TextComponent levelUpTextComponent = TextComponentFactory.getNotificationLevelUpTextComponent(skillName, levelsGained, newLevel);
        McMMOPlayerNotificationEvent customEvent = checkNotificationEvent(mcMMOPlayer.getPlayer(), NotificationType.LEVEL_UP_MESSAGE, destination, levelUpTextComponent);

        sendNotification(mcMMOPlayer.getPlayer(), customEvent);
    }

    public static void broadcastTitle(Server server, String title, String subtitle, int i1, int i2, int i3)
    {
        for(Player player : server.getOnlinePlayers())
        {
            player.sendTitle(title, subtitle, i1, i2, i3);
        }
    }

    public static void sendPlayerUnlockNotification(McMMOPlayer mcMMOPlayer, SubSkillType subSkillType)
    {
        if(!mcMMOPlayer.useChatNotifications())
            return;

        //CHAT MESSAGE
        mcMMOPlayer.getPlayer().spigot().sendMessage(TextComponentFactory.getSubSkillUnlockedNotificationComponents(mcMMOPlayer.getPlayer(), subSkillType));

        //Unlock Sound Effect
        SoundManager.sendCategorizedSound(mcMMOPlayer.getPlayer(), mcMMOPlayer.getPlayer().getLocation(), SoundType.SKILL_UNLOCKED, SoundCategory.MASTER);

        //ACTION BAR MESSAGE
        /*if(AdvancedConfig.getInstance().doesNotificationUseActionBar(NotificationType.SUBSKILL_UNLOCKED))
            mcMMOPlayer.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(LocaleLoader.getString("JSON.SkillUnlockMessage",
                    subSkillType.getLocaleName(),
                    String.valueOf(RankUtils.getRank(mcMMOPlayer.getPlayer(),
                            subSkillType)))));*/
    }

    /**
     * Sends a message to all admins with the admin notification formatting from the locale
     * Admins are currently players with either Operator status or Admin Chat permission
     * @param msg message fetched from locale
     */
    private static void sendAdminNotification(String msg) {
        //If its not enabled exit
        if(!Config.getInstance().adminNotifications())
            return;

        for(Player player : Bukkit.getServer().getOnlinePlayers())
        {
            if(player.isOp() || Permissions.adminChat(player))
            {
                player.sendMessage(LocaleLoader.getString("Notifications.Admin.Format.Others", msg));
            }
        }

        //Copy it out to Console too
        mcMMO.p.getLogger().info(LocaleLoader.getString("Notifications.Admin.Format.Others", msg));
    }

    /**
     * Sends a confirmation message to the CommandSender who just executed an admin command
     * @param commandSender target command sender
     * @param msg message fetched from locale
     */
    private static void sendAdminCommandConfirmation(CommandSender commandSender, String msg) {
        commandSender.sendMessage(LocaleLoader.getString("Notifications.Admin.Format.Self", msg));
    }

    /**
     * Convenience method to report info about a command sender using a sensitive command
     * @param commandSender the command user
     * @param sensitiveCommandType type of command issued
     */
    public static void processSensitiveCommandNotification(CommandSender commandSender, SensitiveCommandType sensitiveCommandType, String... args) {
        /*
         * Determine the 'identity' of the one who executed the command to pass as a parameters
         */
        String senderName = LocaleLoader.getString("Server.ConsoleName");

        if(commandSender instanceof Player)
        {
            senderName = ((Player) commandSender).getDisplayName() + ChatColor.RESET + "-" + ((Player) commandSender).getUniqueId();
        }

        //Send the notification
        switch(sensitiveCommandType)
        {
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
     * Takes an array and an object, makes a new array with object in the first position of the new array,
     * and the following elements in this new array being a copy of the existing array retaining their order
     * @param itemToAdd the string to put at the beginning of the new array
     * @param existingArray the existing array to be copied to the new array at position [0]+1 relative to their original index
     * @return the new array combining itemToAdd at the start and existing array elements following while retaining their order
     */
    public static String[] addItemToFirstPositionOfArray(String itemToAdd, String... existingArray) {
        String[] newArray = new String[existingArray.length + 1];
        newArray[0] = itemToAdd;

        System.arraycopy(existingArray, 0, newArray, 1, existingArray.length);

        return newArray;
    }

}
