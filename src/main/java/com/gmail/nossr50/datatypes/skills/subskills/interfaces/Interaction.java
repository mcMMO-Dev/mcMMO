package com.gmail.nossr50.datatypes.skills.subskills.interfaces;

import com.gmail.nossr50.mcMMO;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

public interface Interaction {
    /**
     * The type of interaction this subskill has with Minecraft
     *
     * @return the interaction type
     */
    InteractType getInteractType();

    /**
     * Executes the interaction between this subskill and Minecraft
     *
     * @param event the vector of interaction
     * @param plugin the mcMMO plugin instance
     * @return true if interaction wasn't cancelled
     */
    boolean doInteraction(Event event, mcMMO plugin);

    /**
     * The priority for this interaction
     *
     * @return the priority for interaction
     */
    EventPriority getEventPriority();
}
