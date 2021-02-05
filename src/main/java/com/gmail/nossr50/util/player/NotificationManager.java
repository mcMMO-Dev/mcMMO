package com.gmail.nossr50.util.player;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.LevelUpBroadcastPredicate;
import com.gmail.nossr50.datatypes.PowerLevelUpBroadcastPredicate;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.notifications.SensitiveCommandType;
import com.gmail.nossr50.datatypes.skills.CoreSkills;
import com.gmail.nossr50.util.Misc;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerNotificationEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import com.gmail.nossr50.util.text.McMMOMessageType;
import com.gmail.nossr50.util.text.TextComponentFactory;
import com.neetgames.mcmmo.skill.RootSkill;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public class NotificationManager {

    public static final String HEX_BEIGE_COLOR = "#c2a66e";
    public static final String HEX_LIME_GREEN_COLOR = "#8ec26e";

    /**
     * Sends players notifications from mcMMO
     * Does so by sending out an event so other plugins can cancel it
     * @param player target player
     * @param notificationType notifications defined type
     * @param key the locale key for the notifications defined message
     */
    public static void sendPlayerInformation(@NotNull Player player, @NotNull NotificationType notificationType, @NotNull String key)
    {
        OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer(player);

        if(mmoPlayer == null || !mmoPlayer.hasSkillChatNotifications())
            return;

        McMMOMessageType destination = AdvancedConfig.getInstance().doesNotificationUseActionBar(notificationType) ? McMMOMessageType.ACTION_BAR : McMMOMessageType.SYSTEM;

        Component message = TextComponentFactory.getNotificationTextComponentFromLocale(key);
        McMMOPlayerNotificationEvent customEvent = checkNotificationEvent(player, notificationType, destination, message);

        sendNotification(player, customEvent);
    }


    public static boolean doesPlayerUseNotifications(@NotNull Player player)
    {
        OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer(player);

        if(mmoPlayer == null)
            return false;
        else
            return mmoPlayer.hasSkillChatNotifications();
    }

    /**
     * Sends players notifications from mcMMO
     * This does this by sending out an event so other plugins can cancel it
     * This event in particular is provided with a source player, and players near the source player are sent the information
     *
     * @param targetPlayer the recipient player for this message
     * @param notificationType type of notification
     * @param key Locale Key for the string to use with this event
     * @param values values to be injected into the locale string
     */
    public static void sendNearbyPlayersInformation(@NotNull Player targetPlayer, @NotNull NotificationType notificationType, @NotNull String key, String... values) {
        sendPlayerInformation(targetPlayer, notificationType, key, values);
    }

    public static void sendPlayerInformationChatOnly(@NotNull Player player, @NotNull String key, String... values) {
        OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer(player);

        //Don't send chat notifications if they are disabled
        if(mmoPlayer != null && !mmoPlayer.hasSkillChatNotifications())
            return;

        String preColoredString = LocaleLoader.getString(key, (Object[]) values);
        player.sendMessage(preColoredString);
    }

    public static void sendPlayerInformationChatOnlyPrefixed(@NotNull Player player, @NotNull String key, String... values)
    {
        OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer(player);

        //Don't send chat notifications if they are disabled
        if(mmoPlayer != null && !mmoPlayer.hasSkillChatNotifications())
            return;

        String preColoredString = LocaleLoader.getString(key, (Object[]) values);
        String prefixFormattedMessage = LocaleLoader.getString("mcMMO.Template.Prefix", preColoredString);
        player.sendMessage(prefixFormattedMessage);
    }

    public static void sendPlayerInformation(@NotNull Player player, @NotNull NotificationType notificationType, @NotNull String key, String... values) {
        OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer(player);

        //Don't send chat notifications if they are disabled
        if(mmoPlayer != null && !mmoPlayer.hasSkillChatNotifications())
            return;

        McMMOMessageType destination = AdvancedConfig.getInstance().doesNotificationUseActionBar(notificationType) ? McMMOMessageType.ACTION_BAR : McMMOMessageType.SYSTEM;

        Component message = TextComponentFactory.getNotificationMultipleValues(key, values);
        McMMOPlayerNotificationEvent customEvent = checkNotificationEvent(player, notificationType, destination, message);

        sendNotification(player, customEvent);
    }

    private static void sendNotification(@NotNull Player player, @NotNull McMMOPlayerNotificationEvent customEvent) {
        if (customEvent.isCancelled())
            return;

        final Audience audience = mcMMO.getAudiences().player(player);

        //If the message is being sent to the action bar we need to check if the copy if a copy is sent to the chat system
        if(customEvent.getChatMessageType() == McMMOMessageType.ACTION_BAR)
        {
            audience.sendActionBar(customEvent.getNotificationTextComponent());

            if(customEvent.isMessageAlsoBeingSentToChat())
            {
                //Send copy to chat system
                audience.sendMessage(Identity.nil(), customEvent.getNotificationTextComponent(), MessageType.SYSTEM);
            }
        } else {
            audience.sendMessage(Identity.nil(), customEvent.getNotificationTextComponent(), MessageType.SYSTEM);
        }
    }

    private static @NotNull McMMOPlayerNotificationEvent checkNotificationEvent(@NotNull Player player, @NotNull NotificationType notificationType, @NotNull McMMOMessageType destination, @NotNull Component message) {
        //Init event
        McMMOPlayerNotificationEvent customEvent = new McMMOPlayerNotificationEvent(player,
                notificationType, message, destination, AdvancedConfig.getInstance().doesNotificationSendCopyToChat(notificationType));

        //Call event
        Bukkit.getServer().getPluginManager().callEvent(customEvent);
        return customEvent;
    }

    /**
     * Handles sending level up notifications to a mmoPlayer
     * @param mmoPlayer target mmoPlayer
     * @param rootSkill skill that leveled up
     * @param newLevel new level of that skill
     */
    public static void sendPlayerLevelUpNotification(@NotNull OnlineMMOPlayer mmoPlayer, @NotNull RootSkill rootSkill, int levelsGained, int newLevel)
    {
        if(!mmoPlayer.hasSkillChatNotifications())
            return;

        McMMOMessageType destination = AdvancedConfig.getInstance().doesNotificationUseActionBar(NotificationType.LEVEL_UP_MESSAGE) ? McMMOMessageType.ACTION_BAR : McMMOMessageType.SYSTEM;

        Component levelUpTextComponent = TextComponentFactory.getNotificationLevelUpTextComponent(CoreSkills.getSkill(rootSkill), levelsGained, newLevel);
        McMMOPlayerNotificationEvent customEvent = checkNotificationEvent(Misc.adaptPlayer(mmoPlayer), NotificationType.LEVEL_UP_MESSAGE, destination, levelUpTextComponent);

        sendNotification(Misc.adaptPlayer(mmoPlayer), customEvent);
    }

    public static void broadcastTitle(@NotNull Server server, @NotNull String title, @NotNull String subtitle, int i1, int i2, int i3)
    {
        for(Player player : server.getOnlinePlayers())
        {
            player.sendTitle(title, subtitle, i1, i2, i3);
        }
    }

    public static void sendPlayerUnlockNotification(@NotNull OnlineMMOPlayer mmoPlayer, @NotNull SubSkillType subSkillType)
    {
        if(!mmoPlayer.hasSkillChatNotifications())
            return;

        //CHAT MESSAGE
        mcMMO.getAudiences().player(Misc.adaptPlayer(mmoPlayer)).sendMessage(Identity.nil(), TextComponentFactory.getSubSkillUnlockedNotificationComponents(mmoPlayer, subSkillType));

        //Unlock Sound Effect
        SoundManager.sendCategorizedSound(Misc.adaptPlayer(mmoPlayer), Misc.adaptPlayer(mmoPlayer).getLocation(), SoundType.SKILL_UNLOCKED, SoundCategory.MASTER);

        //ACTION BAR MESSAGE
        /*if(AdvancedConfig.getInstance().doesNotificationUseActionBar(NotificationType.SUBSKILL_UNLOCKED))
            Misc.adaptPlayer(mmoPlayer).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(LocaleLoader.getString("JSON.SkillUnlockMessage",
                    subSkillType.getLocaleName(),
                    String.valueOf(RankUtils.getRank(Misc.adaptPlayer(mmoPlayer),
                            subSkillType)))));*/
    }

    /**
     * Sends a message to all admins with the admin notification formatting from the locale
     * Admins are currently players with either Operator status or Admin Chat permission
     * @param msg message fetched from locale
     */
    private static void sendAdminNotification(@NotNull String msg) {
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
    private static void sendAdminCommandConfirmation(@NotNull CommandSender commandSender, @NotNull String msg) {
        commandSender.sendMessage(LocaleLoader.getString("Notifications.Admin.Format.Self", msg));
    }

    /**
     * Convenience method to report info about a command sender using a sensitive command
     * @param commandSender the command user
     * @param sensitiveCommandType type of command issued
     */
    public static void processSensitiveCommandNotification(@NotNull CommandSender commandSender, @NotNull SensitiveCommandType sensitiveCommandType, String... args) {
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
    public static @NotNull String[] addItemToFirstPositionOfArray(@NotNull String itemToAdd, @NotNull String... existingArray) {
        String[] newArray = new String[existingArray.length + 1];
        newArray[0] = itemToAdd;

        System.arraycopy(existingArray, 0, newArray, 1, existingArray.length);

        return newArray;
    }

    //TODO: Remove the code duplication, am lazy atm
    //TODO: Fix broadcasts being skipped for situations where a player skips over the milestone like with the addlevels command
    public static void processLevelUpBroadcasting(@NotNull McMMOPlayer mmoPlayer, @NotNull PrimarySkillType primarySkillType, int level) {
        if(level <= 0)
            return;

        //Check if broadcasting is enabled
        if(Config.getInstance().shouldLevelUpBroadcasts()) {
            //Permission check
            if(!Permissions.levelUpBroadcast(mmoPlayer.getPlayer())) {
                return;
            }

            int levelInterval = Config.getInstance().getLevelUpBroadcastInterval();
            int remainder = level % levelInterval;

            if(remainder == 0) {
                //Grab appropriate audience
                Audience audience = mcMMO.getAudiences().filter(getLevelUpBroadcastPredicate(mmoPlayer.getPlayer()));
                //TODO: Make prettier
                HoverEvent<Component> levelMilestoneHover = Component.text(mmoPlayer.getPlayer().getName())
                        .append(Component.newline())
                        .append(Component.text(LocalDate.now().toString()))
                        .append(Component.newline())
                        .append(Component.text(primarySkillType.getName()+" reached level "+level)).color(TextColor.fromHexString(HEX_BEIGE_COLOR))
                        .asHoverEvent();

                String localeMessage = LocaleLoader.getString("Broadcasts.LevelUpMilestone", mmoPlayer.getPlayer().getDisplayName(), level, primarySkillType.getName());
                Component message = Component.text(localeMessage).hoverEvent(levelMilestoneHover);

                Bukkit.getScheduler().runTaskLater(mcMMO.p, () -> audience.sendMessage(Identity.nil(), message), 0);
            }
        }
    }

    //TODO: Remove the code duplication, am lazy atm
    //TODO: Fix broadcasts being skipped for situations where a player skips over the milestone like with the addlevels command
    public static void processPowerLevelUpBroadcasting(@NotNull McMMOPlayer mmoPlayer, int powerLevel) {
        if(powerLevel <= 0)
            return;

        //Check if broadcasting is enabled
        if(Config.getInstance().shouldPowerLevelUpBroadcasts()) {
            //Permission check
            if(!Permissions.levelUpBroadcast(mmoPlayer.getPlayer())) {
                return;
            }

            int levelInterval = Config.getInstance().getPowerLevelUpBroadcastInterval();
            int remainder = powerLevel % levelInterval;

            if(remainder == 0) {
                //Grab appropriate audience
                Audience audience = mcMMO.getAudiences().filter(getPowerLevelUpBroadcastPredicate(mmoPlayer.getPlayer()));
                //TODO: Make prettier
                HoverEvent<Component> levelMilestoneHover = Component.text(mmoPlayer.getPlayer().getName())
                        .append(Component.newline())
                        .append(Component.text(LocalDate.now().toString()))
                        .append(Component.newline())
                        .append(Component.text("Power level has reached "+powerLevel)).color(TextColor.fromHexString(HEX_BEIGE_COLOR))
                        .asHoverEvent();

                String localeMessage = LocaleLoader.getString("Broadcasts.PowerLevelUpMilestone", mmoPlayer.getPlayer().getDisplayName(), powerLevel);
                Component message = Component.text(localeMessage).hoverEvent(levelMilestoneHover);

                Bukkit.getScheduler().runTaskLater(mcMMO.p, () -> audience.sendMessage(Identity.nil(), message), 0);
            }
        }
    }

    //TODO: Could cache
    public static @NotNull LevelUpBroadcastPredicate<CommandSender> getLevelUpBroadcastPredicate(@NotNull CommandSender levelUpPlayer) {
        return new LevelUpBroadcastPredicate<>(levelUpPlayer);
    }

    public static @NotNull PowerLevelUpBroadcastPredicate<CommandSender> getPowerLevelUpBroadcastPredicate(@NotNull CommandSender levelUpPlayer) {
        return new PowerLevelUpBroadcastPredicate<>(levelUpPlayer);
    }

}
