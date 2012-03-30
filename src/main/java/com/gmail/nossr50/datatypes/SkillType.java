package com.gmail.nossr50.datatypes;

import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;

public enum SkillType {
    ACROBATICS(LoadProperties.levelCapAcrobatics, LoadProperties.acrobaticsxpmodifier),
    ALL, //This one is just for convenience
    ARCHERY(LoadProperties.levelCapArchery, LoadProperties.archeryxpmodifier),
    AXES(AbilityType.SKULL_SPLIITER, LoadProperties.levelCapAxes, ToolType.AXE, LoadProperties.axesxpmodifier),
    EXCAVATION(AbilityType.GIGA_DRILL_BREAKER, LoadProperties.levelCapExcavation, ToolType.SHOVEL, LoadProperties.excavationxpmodifier),
    FISHING(LoadProperties.levelCapFishing, LoadProperties.fishingxpmodifier),
    HERBALISM(AbilityType.GREEN_TERRA, LoadProperties.levelCapHerbalism, ToolType.HOE, LoadProperties.herbalismxpmodifier),
    MINING(AbilityType.SUPER_BREAKER, LoadProperties.levelCapMining, ToolType.PICKAXE, LoadProperties.miningxpmodifier),
    REPAIR(LoadProperties.levelCapRepair, LoadProperties.repairxpmodifier),
    SWORDS(AbilityType.SERRATED_STRIKES, LoadProperties.levelCapSwords, ToolType.SWORD, LoadProperties.swordsxpmodifier),
    TAMING(LoadProperties.levelCapTaming, LoadProperties.tamingxpmodifier),
    UNARMED(AbilityType.BERSERK, LoadProperties.levelCapUnarmed, ToolType.FISTS, LoadProperties.unarmedxpmodifier),
    WOODCUTTING(AbilityType.TREE_FELLER, LoadProperties.levelCapWoodcutting, ToolType.AXE, LoadProperties.woodcuttingxpmodifier);

    private AbilityType ability;
    private int maxLevel;
    private ToolType tool;
    private double xpModifier;

    private SkillType() {
        this.ability = null;
        this.maxLevel = 0;
        this.tool = null;
        this.xpModifier = 0;
    }

    private SkillType(AbilityType ability, int maxLevel, ToolType tool, double xpModifier) {
        this.ability = ability;
        this.maxLevel = maxLevel;
        this.tool = tool;
        this.xpModifier = xpModifier;
    }

    private SkillType(int maxLevel, double xpModifier) {
        this(null, maxLevel, null, xpModifier);
    }

    public AbilityType getAbility() {
        return ability;
    }

    /**
     * Get the max level of this skill.
     *
     * @return the max level of this skill
     */
    public int getMaxLevel() {
        if (maxLevel > 0) {
            return maxLevel;
        }
        else {
            return Integer.MAX_VALUE;
        }
    }

    public ToolType getTool() {
        return tool;
    }

    /**
     * Get the base permissions associated with this skill.
     *
     * @param player The player to check the permissions for
     * @return true if the player has permissions, false otherwise
     */
    public boolean getPermissions(Player player) {
        switch (this) {
        case ACROBATICS:
            return mcPermissions.getInstance().acrobatics(player);

        case ARCHERY:
            return mcPermissions.getInstance().archery(player);

        case AXES:
            return mcPermissions.getInstance().axes(player);

        case EXCAVATION:
            return mcPermissions.getInstance().excavation(player);

        case FISHING:
            return mcPermissions.getInstance().fishing(player);

        case HERBALISM:
            return mcPermissions.getInstance().herbalism(player);

        case MINING:
            return mcPermissions.getInstance().mining(player);

        case REPAIR:
            return mcPermissions.getInstance().repair(player);

        case SWORDS:
            return mcPermissions.getInstance().swords(player);

        case TAMING:
            return mcPermissions.getInstance().taming(player);

        case UNARMED:
            return mcPermissions.getInstance().unarmed(player);

        case WOODCUTTING:
            return mcPermissions.getInstance().woodcutting(player);

        default:
            return false;
        }
    }

    public double getXpModifier() {
        return xpModifier;
    }

    /**
     * Get the skill level for this skill.
     *
     * @param player The player to check
     * @return
     */
    public int getSkillLevel(Player player) {
        return Users.getProfile(player).getSkillLevel(this);
    }
}
