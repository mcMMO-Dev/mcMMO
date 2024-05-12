package com.gmail.nossr50.datatypes.skills.subskills.interfaces;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.interfaces.Skill;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;

public interface SubSkill extends Skill {
    /**
     * Grabs the permission node for this skill
     * @return permission node address
     */
    String getPermissionNode();

    /**
     * Returns a collection of strings about how a skill works
     * @return
     */
    String getMechanics();

    /**
     * Get an array of various stats for a player
     * @param mmoPlayer target player
     * @return stat array for target player for this skill
     */
    Double[] getStats(McMMOPlayer mmoPlayer);

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
     * @param mmoPlayer owner of this skill
     */
    void addStats(TextComponent.Builder componentBuilder, McMMOPlayer mmoPlayer);

    /**
     * Whether this subskill is enabled
     * @return true if enabled
     */
    boolean isEnabled();

    /**
     * Prints detailed info about this subskill to the player
     * @param mmoPlayer the target player
     */
    void printInfo(McMMOPlayer mmoPlayer);
}
