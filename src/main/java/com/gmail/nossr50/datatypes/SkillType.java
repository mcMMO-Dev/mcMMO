package com.gmail.nossr50.datatypes;

import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public enum SkillType {
    ACROBATICS(Config.getInstance().getLevelCapAcrobatics(), Config.getInstance().getFormulaMultiplierAcrobatics()),
    ALL, //This one is just for convenience
    ARCHERY(Config.getInstance().getLevelCapArchery(), Config.getInstance().getFormulaMultiplierArchery()),
    AXES(AbilityType.SKULL_SPLIITER, Config.getInstance().getLevelCapAxes(), ToolType.AXE, Config.getInstance().getFormulaMultiplierAxes()),
    EXCAVATION(AbilityType.GIGA_DRILL_BREAKER, Config.getInstance().getLevelCapExcavation(), ToolType.SHOVEL, Config.getInstance().getFormulaMultiplierExcavation()),
    FISHING(Config.getInstance().getLevelCapFishing(), Config.getInstance().getFormulaMultiplierFishing()),
    HERBALISM(AbilityType.GREEN_TERRA, Config.getInstance().getLevelCapHerbalism(), ToolType.HOE, Config.getInstance().getFormulaMultiplierHerbalism()),
    MINING(AbilityType.SUPER_BREAKER, Config.getInstance().getLevelCapMining(), ToolType.PICKAXE, Config.getInstance().getFormulaMultiplierMining()),
    REPAIR(Config.getInstance().getLevelCapRepair(), Config.getInstance().getFormulaMultiplierRepair()),
    SWORDS(AbilityType.SERRATED_STRIKES, Config.getInstance().getLevelCapSwords(), ToolType.SWORD, Config.getInstance().getFormulaMultiplierSwords()),
    TAMING(Config.getInstance().getLevelCapTaming(), Config.getInstance().getFormulaMultiplierTaming()),
    UNARMED(AbilityType.BERSERK, Config.getInstance().getLevelCapUnarmed(), ToolType.FISTS, Config.getInstance().getFormulaMultiplierUnarmed()),
    WOODCUTTING(AbilityType.TREE_FELLER, Config.getInstance().getLevelCapWoodcutting(), ToolType.AXE, Config.getInstance().getFormulaMultiplierWoodcutting());

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
            return Permissions.getInstance().acrobatics(player);

        case ARCHERY:
            return Permissions.getInstance().archery(player);

        case AXES:
            return Permissions.getInstance().axes(player);

        case EXCAVATION:
            return Permissions.getInstance().excavation(player);

        case FISHING:
            return Permissions.getInstance().fishing(player);

        case HERBALISM:
            return Permissions.getInstance().herbalism(player);

        case MINING:
            return Permissions.getInstance().mining(player);

        case REPAIR:
            return Permissions.getInstance().repair(player);

        case SWORDS:
            return Permissions.getInstance().swords(player);

        case TAMING:
            return Permissions.getInstance().taming(player);

        case UNARMED:
            return Permissions.getInstance().unarmed(player);

        case WOODCUTTING:
            return Permissions.getInstance().woodcutting(player);

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
