package com.gmail.nossr50.datatypes.skills.progression;

import com.gmail.nossr50.datatypes.skills.subskills.interfaces.InteractType;
import org.bukkit.event.Event;

public interface Progression {
    /**
     * The interaction vector for gaining XP
     *
     * @return the interaction vector for gaining XP
     */
    InteractType getXpGainInteractType();

    /**
     * Executes the interaction for gaining XP
     */
    void doXpGainInteraction(Event event);
}
