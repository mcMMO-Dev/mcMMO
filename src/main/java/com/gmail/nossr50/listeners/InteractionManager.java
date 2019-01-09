package com.gmail.nossr50.listeners;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.datatypes.skills.subskills.interfaces.InteractType;
import com.gmail.nossr50.datatypes.skills.subskills.interfaces.Interaction;
import com.gmail.nossr50.events.skills.McMMOPlayerNotificationEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.TextComponentFactory;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.HashMap;

public class InteractionManager {
    private static HashMap<InteractType, ArrayList<Interaction>> interactRegister;
    private static ArrayList<AbstractSubSkill> subSkillList;

    /**
     * Registers subskills with the Interaction registration
     * @param abstractSubSkill the target subskill to register
     */
    public static void registerSubSkill(AbstractSubSkill abstractSubSkill)
    {
        //Init map
        if(interactRegister == null)
            interactRegister = new HashMap<>();

        if(subSkillList == null)
            subSkillList = new ArrayList<>();

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
        //TEST
        //interactRegister.put(abstractSubSkill.getInteractType(), arrayRef);

        System.out.println("[mcMMO] registered subskill: "+ abstractSubSkill.getConfigKeyName());
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
     * @param localeKey the locale key for the notifications defined message
     */
    public static void sendPlayerInformation(Player player, NotificationType notificationType, String localeKey)
    {
        //Init event
        McMMOPlayerNotificationEvent customEvent = new McMMOPlayerNotificationEvent(player, notificationType, LocaleLoader.getString(localeKey));
        //Call event
        Bukkit.getServer().getPluginManager().callEvent(customEvent);

        //Check to see if our custom event is cancelled
        if(!customEvent.isCancelled())
        {
            if(AdvancedConfig.getInstance().doesNotificationUseActionBar(notificationType))
            {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponentFactory.getNotificationTextComponent(customEvent.getNotificationMessage(), notificationType));
            } else {
                player.spigot().sendMessage(ChatMessageType.SYSTEM, TextComponentFactory.getNotificationTextComponent(customEvent.getNotificationMessage(), notificationType));
            }
        }
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

    /**
     * Returns the associative map which contains all registered interactions
     * @return the interact register
     */
    public static HashMap<InteractType, ArrayList<Interaction>> getInteractRegister() {
        return interactRegister;
    }
}
