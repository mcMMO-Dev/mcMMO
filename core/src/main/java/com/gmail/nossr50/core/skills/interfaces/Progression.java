package com.gmail.nossr50.core.skills.interfaces;

import com.gmail.nossr50.core.skills.subskills.interfaces.InteractType;

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
