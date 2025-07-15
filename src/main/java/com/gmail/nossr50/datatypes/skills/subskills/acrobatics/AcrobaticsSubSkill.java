package com.gmail.nossr50.datatypes.skills.subskills.acrobatics;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.datatypes.skills.subskills.interfaces.InteractType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.text.StringUtils;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

public abstract class AcrobaticsSubSkill extends AbstractSubSkill {

    protected EventPriority interactionPriority;

    public AcrobaticsSubSkill(String configKeySub, EventPriority interactionPriority,
            SubSkillType subSkillType) {
        super(configKeySub, "Acrobatics", subSkillType);

        this.interactionPriority = interactionPriority;
    }

    /**
     * The name of this subskill Core mcMMO skills will pull the name from Locale with this method
     *
     * @return the subskill name
     */
    @Override
    public String getNiceName() {
        return LocaleLoader.getString(
                getPrimaryKeyName() + ".SubSkill." + getConfigKeyName() + ".Name");
    }

    /**
     * This is the name that represents our subskill in the config
     *
     * @return the config key name
     */
    @Override
    public String getConfigKeyName() {
        return configKeySubSkill;
    }

    /**
     * Grabs tips for the subskill
     *
     * @return tips for the subskill
     */
    @Override
    public String getTips() {
        return LocaleLoader.getString(
                "JSON." + StringUtils.getCapitalized(getPrimarySkill().toString()) + ".SubSkill."
                        + getConfigKeyName() + ".Details.Tips");
    }

    /**
     * The name of the primary skill
     *
     * @return The name of the primary skill
     */
    @Override
    public PrimarySkillType getPrimarySkill() {
        return PrimarySkillType.ACROBATICS;
    }

    /**
     * Returns the key name used for this skill in conjunction with config files
     *
     * @return config file key name
     */
    @Override
    public String getPrimaryKeyName() {
        return configKeyPrimary;
    }

    /**
     * The type of event used for interaction in this subskill for Minecraft
     *
     * @return the event for interaction
     */
    @Override
    public InteractType getInteractType() {
        return InteractType.ON_ENTITY_DAMAGE;
    }

    /**
     * Executes the interaction between this subskill and Minecraft
     *
     * @param event the vector of interaction
     * @param plugin the mcMMO plugin instance
     * @return true if interaction wasn't cancelled
     */
    @Override
    public boolean doInteraction(Event event, mcMMO plugin) {
        return false;
    }

    /**
     * The priority for this interaction
     *
     * @return the priority for interaction
     */
    @Override
    public EventPriority getEventPriority() {
        return interactionPriority;
    }

    /**
     * Not all skills have ranks
     *
     * @return true if the skill has ranks
     */
    @Override
    public boolean hasRanks() {
        return (getNumRanks() > 0);
    }
}
