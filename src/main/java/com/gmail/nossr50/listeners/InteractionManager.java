package com.gmail.nossr50.listeners;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.datatypes.skills.subskills.interfaces.InteractType;
import com.gmail.nossr50.datatypes.skills.subskills.interfaces.Interaction;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class InteractionManager {
    private static HashMap<InteractType, ArrayList<Interaction>> interactRegister;
    private static HashMap<String, AbstractSubSkill> subSkillNameMap; //Used for mmoinfo optimization
    private static ArrayList<AbstractSubSkill> subSkillList;

    public static void initMaps() {
        /* INIT MAPS */
        if (interactRegister == null)
            interactRegister = new HashMap<>();

        if (subSkillList == null)
            subSkillList = new ArrayList<>();

        if (subSkillNameMap == null)
            subSkillNameMap = new HashMap<>();
    }

    /**
     * Registers subskills with the Interaction registration
     * @param abstractSubSkill the target subskill to register
     */
    public static void registerSubSkill(AbstractSubSkill abstractSubSkill) {
        //Store a unique copy of each subskill
        if (!subSkillList.contains(abstractSubSkill))
            subSkillList.add(abstractSubSkill);

        //Init ArrayList
        interactRegister.computeIfAbsent(abstractSubSkill.getInteractType(), k -> new ArrayList<>());

        //Registration array reference
        ArrayList<Interaction> arrayRef = interactRegister.get(abstractSubSkill.getInteractType());

        //Register skill
        arrayRef.add(abstractSubSkill);

        String lowerCaseName = abstractSubSkill.getConfigKeyName().toLowerCase(Locale.ENGLISH);

        //Register in name map
        subSkillNameMap.putIfAbsent(lowerCaseName, abstractSubSkill);

        LogUtils.debug(mcMMO.p.getLogger(), "Registered subskill: "+ abstractSubSkill.getConfigKeyName());
    }

    /**
     * Grabs the registered abstract skill by its name
     * Is not case sensitive
     * @param name name of subskill, not case sensitive
     * @return null if the subskill is not registered
     */
    public static AbstractSubSkill getAbstractByName(String name) {
        return subSkillNameMap.get(name.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Processes the associated Interactions for this event
     * @param event target event
     * @param plugin instance of mcMMO plugin
     * @param curInteractType the associated interaction type
     */
    public static void processEvent(Event event, mcMMO plugin, InteractType curInteractType) {
        if (interactRegister.get(curInteractType) == null)
            return;

        for(Interaction interaction : interactRegister.get(curInteractType)) {
            interaction.doInteraction(event, plugin);
        }
    }

    /**
     * Returns the list that contains all unique instances of registered Interaction classes
     * Interactions are extensions of abstract classes that represent modifying behaviours in Minecraft through events
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
     * @return the interact register
     */
    public static HashMap<InteractType, ArrayList<Interaction>> getInteractRegister() {
        return interactRegister;
    }
}
