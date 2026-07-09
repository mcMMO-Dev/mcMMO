package com.gmail.nossr50.listeners;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.datatypes.skills.subskills.interfaces.InteractType;
import com.gmail.nossr50.datatypes.skills.subskills.interfaces.Interaction;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.bukkit.event.Event;

public class InteractionManager {
    private static final HashMap<InteractType, ArrayList<Interaction>> interactRegister =
            new HashMap<>();
    //Used for mmoinfo optimization
    private static final HashMap<String, AbstractSubSkill> subSkillNameMap = new HashMap<>();
    private static final ArrayList<AbstractSubSkill> subSkillList = new ArrayList<>();

    /**
     * @deprecated The registration maps are initialized statically; calling this is no longer
     * needed and has no effect.
     */
    @Deprecated(forRemoval = true, since = "2.2.055")
    public static void initMaps() {
    }

    /**
     * Registers subskills with the Interaction registration
     *
     * @param abstractSubSkill the target subskill to register
     */
    public static void registerSubSkill(AbstractSubSkill abstractSubSkill) {
        final String lowerCaseName = abstractSubSkill.getConfigKeyName()
                .toLowerCase(Locale.ENGLISH);

        // A second registration under the same name would double-fire doInteraction on every
        // matching event, so registration is idempotent per config key name
        if (subSkillNameMap.putIfAbsent(lowerCaseName, abstractSubSkill) != null) {
            mcMMO.p.getLogger().warning("Ignoring duplicate subskill registration: "
                    + abstractSubSkill.getConfigKeyName());
            return;
        }

        subSkillList.add(abstractSubSkill);
        interactRegister.computeIfAbsent(abstractSubSkill.getInteractType(),
                k -> new ArrayList<>()).add(abstractSubSkill);

        LogUtils.debug(mcMMO.p.getLogger(),
                "Registered subskill: " + abstractSubSkill.getConfigKeyName());
    }

    /**
     * Grabs the registered abstract skill by its name Is not case sensitive
     *
     * @param name name of subskill, not case sensitive
     * @return null if the subskill is not registered
     */
    public static AbstractSubSkill getAbstractByName(String name) {
        return subSkillNameMap.get(name.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Processes the associated Interactions for this event
     *
     * @param event target event
     * @param plugin instance of mcMMO plugin
     * @param curInteractType the associated interaction type
     */
    public static void processEvent(Event event, mcMMO plugin, InteractType curInteractType) {
        final ArrayList<Interaction> interactions = interactRegister.get(curInteractType);
        if (interactions == null) {
            return;
        }

        for (Interaction interaction : interactions) {
            interaction.doInteraction(event, plugin);
        }
    }

    /**
     * Returns the list that contains all unique instances of registered Interaction classes
     * Interactions are extensions of abstract classes that represent modifying behaviours in
     * Minecraft through events
     *
     * @return the unique collection of all registered Interaction classes
     */
    public static ArrayList<AbstractSubSkill> getSubSkillList() {
        return subSkillList;
    }

    public static boolean hasSubSkill(String name) {
        return getAbstractByName(name) != null;
    }

    public static boolean hasSubSkill(SubSkillType subSkillType) {
        return hasSubSkill(subSkillType.getNiceNameNoSpaces(subSkillType));
    }

    /**
     * Returns the associative map which contains all registered interactions
     *
     * @return the interact register
     */
    public static HashMap<InteractType, ArrayList<Interaction>> getInteractRegister() {
        return interactRegister;
    }
}
