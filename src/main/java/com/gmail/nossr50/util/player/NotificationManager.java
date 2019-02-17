package com.gmail.nossr50.util.player;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerNotificationEvent;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.TextComponentFactory;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class NotificationManager {
    /**
     * Sends players notifications from mcMMO
     * This does this by sending out an event so other plugins can cancel it
     * @param player target player
     * @param notificationType notifications defined type
     * @param key the locale key for the notifications defined message
     */
    public static void sendPlayerInformation(Player player, NotificationType notificationType, String key)
    {
        ChatMessageType destination = AdvancedConfig.getInstance().doesNotificationUseActionBar(notificationType) ? ChatMessageType.ACTION_BAR : ChatMessageType.SYSTEM;

        TextComponent message = TextComponentFactory.getNotificationTextComponentFromLocale(key, notificationType);
        McMMOPlayerNotificationEvent customEvent = checkNotificationEvent(player, notificationType, destination, message);

        sendNotification(player, customEvent);
    }

    /**
     * Sends players notifications from mcMMO
     * This does this by sending out an event so other plugins can cancel it
     * This event in particular is provided with a source player, and players near the source player are sent the information
     * @param source the source player for this event
     * @param notificationType type of notification
     * @param key Locale Key for the string to use with this event
     * @param values values to be injected into the locale string
     */
    public static void sendNearbyPlayersInformation(Player source, NotificationType notificationType, String key, String... values)
    {
        Location location = source.getLocation();
        for (Player otherPlayer : source.getWorld().getPlayers()) {
            if (otherPlayer != source && Misc.isNear(location, otherPlayer.getLocation(), Misc.SKILL_MESSAGE_MAX_SENDING_DISTANCE)) {
                sendPlayerInformation(otherPlayer, notificationType, key, values);
            }
        }
    }

    public static void sendPlayerInformation(Player player, NotificationType notificationType, String key, String... values)
    {
        ChatMessageType destination = AdvancedConfig.getInstance().doesNotificationUseActionBar(notificationType) ? ChatMessageType.ACTION_BAR : ChatMessageType.SYSTEM;


        TextComponent message = TextComponentFactory.getNotificationMultipleValues(key, notificationType, values);
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
}
