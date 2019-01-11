package com.gmail.nossr50.listeners;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkill;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.datatypes.skills.subskills.interfaces.InteractType;
import com.gmail.nossr50.datatypes.skills.subskills.interfaces.Interaction;
import com.gmail.nossr50.events.skills.McMMOPlayerNotificationEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.TextComponentFactory;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.HashMap;

public class InteractionManager {
    private static HashMap<InteractType, ArrayList<Interaction>> interactRegister;
    private static HashMap<String, AbstractSubSkill> subSkillNameMap; //Used for mmoinfo optimization
    private static ArrayList<AbstractSubSkill> subSkillList;

    /**
     * Registers subskills with the Interaction registration
     * @param abstractSubSkill the target subskill to register
     */
    public static void registerSubSkill(AbstractSubSkill abstractSubSkill)
    {
        /* INIT MAPS */
        if(interactRegister == null)
            interactRegister = new HashMap<>();

        if(subSkillList == null)
            subSkillList = new ArrayList<>();

        if(subSkillNameMap == null)
            subSkillNameMap = new HashMap<>();

        //Store a unique copy of each subskill
        if(!subSkillList.contains(abstractSubSkill))
            subSkillList.add(abstractSubSkill);

        //Init ArrayList
        if(interactRegister.get(abstractSubSkill.getInteractType()) == null)
            interactRegister.put(abstractSubSkill.getInteractType(), new ArrayList<>());

        //Registration array reference
        ArrayList<Interaction> arrayRef = interactRegister.get(abstractSubSkill.getInteractType());

        //Register skill
        arrayRef.add(abstractSubSkill);

        String lowerCaseName = abstractSubSkill.getConfigKeyName().toLowerCase();

        //Register in name map
        if(subSkillNameMap.get(lowerCaseName) == null)
            subSkillNameMap.put(lowerCaseName, abstractSubSkill);

        System.out.println("[mcMMO] registered subskill: "+ abstractSubSkill.getConfigKeyName());
    }

    /**
     * Grabs the registered abstract skill by its name
     * Is not case sensitive
     * @param name name of subskill, not case sensitive
     * @return null if the subskill is not registered
     */
    public static AbstractSubSkill getAbstractByName(String name)
    {
        return subSkillNameMap.get(name.toLowerCase());
    }

    /**
     * Processes the associated Interactions for this event
     * @param event target event
     * @param plugin instance of mcMMO plugin
     * @param curInteractType the associated interaction type
     */
    public static void processEvent(Event event, mcMMO plugin, InteractType curInteractType)
    {
        for(Interaction interaction : interactRegister.get(curInteractType))
        {
            interaction.doInteraction(event, plugin);
        }
    }

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

    public static void sendOtherPlayersSkillInfo(Player source, NotificationType notificationType, String key, String... values)
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

        player.spigot().sendMessage(customEvent.getChatMessageType(), customEvent.getNotificationTextComponent());
    }

    private static McMMOPlayerNotificationEvent checkNotificationEvent(Player player, NotificationType notificationType, ChatMessageType destination, TextComponent message) {
        //Init event
        McMMOPlayerNotificationEvent customEvent = new McMMOPlayerNotificationEvent(player,
                notificationType, message, destination);

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
    public static void sendPlayerLevelUpNotification(McMMOPlayer mcMMOPlayer, PrimarySkill skillName, int newLevel)
    {
        ChatMessageType destination = AdvancedConfig.getInstance().doesNotificationUseActionBar(NotificationType.LEVEL_UP_MESSAGE) ? ChatMessageType.ACTION_BAR : ChatMessageType.SYSTEM;

        TextComponent levelUpTextComponent = TextComponentFactory.getNotificationLevelUpTextComponent(mcMMOPlayer, skillName, newLevel);
        McMMOPlayerNotificationEvent customEvent = checkNotificationEvent(mcMMOPlayer.getPlayer(), NotificationType.LEVEL_UP_MESSAGE, destination, levelUpTextComponent);

        sendNotification(mcMMOPlayer.getPlayer(), customEvent);
    }

    /**
     * Returns the list that contains all unique instances of registered Interaction classes
     * Interactions are extensions of abstract classes that represent modifying behaviours in Minecraft through events
     * @return the unique collection of all registered Interaction classes
     */
    public static ArrayList<AbstractSubSkill> getSubSkillList()
    {
        return subSkillList;
    }

    public static boolean hasSubSkill(String name)
    {
        return getAbstractByName(name) != null;
    }

    public static boolean hasSubSkill(SubSkillType subSkillType)
    {
        return hasSubSkill(subSkillType.getNiceNameNoSpaces(subSkillType));
    }

    /**
     * Returns the associative map which contains all registered interactions
     * @return the interact register
     */
    public static HashMap<InteractType, ArrayList<Interaction>> getInteractRegister() {
        return interactRegister;
    }
}
