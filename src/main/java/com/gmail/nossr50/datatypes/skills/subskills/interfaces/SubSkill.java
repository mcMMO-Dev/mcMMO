package com.gmail.nossr50.datatypes.skills.subskills.interfaces;

import com.gmail.nossr50.datatypes.skills.interfaces.Skill;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

public interface SubSkill extends Skill {
    /**
     * Grabs the permission node for this skill
     * @return permission node address
     */
    String getPermissionNode();

    /**
     * Checks if a player has permission to use this skill
     * @param player target player
     * @return true if player has permission
     */
    boolean hasPermission(Player player);

    /**
     * The name of this subskill
     * It's a good idea for this to return the localized name
     * @return the subskill name
     */
    String getNiceName();

    /**
     * This is the name that represents our subskill in the config
     * @return the config key name
     */
    String getConfigKeyName();

    /**
     * Returns the simple description of this subskill
     * @return the simple description of this subskill
     */
    String getDescription();

    /**
     * Grabs tips for the subskill
     * @return tips for the subskill
     */
    String getTips();

    /**
     * Adds detailed stats specific to this skill
     * @param componentBuilder target component builder
     * @param player owner of this skill
     */
    void addStats(ComponentBuilder componentBuilder, Player player);

    /**
     * Whether or not this subskill is enabled
     * @return true if enabled
     */
    boolean isEnabled();
}
