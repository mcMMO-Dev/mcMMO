package com.gmail.nossr50.skills.utilities;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.StringUtils;

public enum SkillType {
    ACROBATICS(Config.getInstance().getLevelCapAcrobatics(), Config.getInstance().getFormulaMultiplierAcrobatics()),
    ARCHERY(Config.getInstance().getLevelCapArchery(), Config.getInstance().getFormulaMultiplierArchery()),
    AXES(AbilityType.SKULL_SPLITTER, Config.getInstance().getLevelCapAxes(), ToolType.AXE, Config.getInstance().getFormulaMultiplierAxes()),
    EXCAVATION(AbilityType.GIGA_DRILL_BREAKER, Config.getInstance().getLevelCapExcavation(), ToolType.SHOVEL, Config.getInstance().getFormulaMultiplierExcavation()),
    FISHING(Config.getInstance().getLevelCapFishing(), Config.getInstance().getFormulaMultiplierFishing()),
    HERBALISM(AbilityType.GREEN_TERRA, Config.getInstance().getLevelCapHerbalism(), ToolType.HOE, Config.getInstance().getFormulaMultiplierHerbalism()),
    MINING(AbilityType.SUPER_BREAKER, Config.getInstance().getLevelCapMining(), ToolType.PICKAXE, Config.getInstance().getFormulaMultiplierMining()),
    REPAIR(Config.getInstance().getLevelCapRepair(), Config.getInstance().getFormulaMultiplierRepair()),
    SMELTING(Config.getInstance().getLevelCapSmelting(), 0),
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

        return Integer.MAX_VALUE;
    }

    public ToolType getTool() {
        return tool;
    }

    public double getXpModifier() {
        return xpModifier;
    }

    public static SkillType getSkill(String skillName) {
        if (!Config.getInstance().getLocale().equalsIgnoreCase("en_US")) {
            for (SkillType type : values()) {
                if (skillName.equalsIgnoreCase(LocaleLoader.getString(StringUtils.getCapitalized(type.name()) + ".SkillName"))) {
                    return type;
                }
            }
        }

        for (SkillType type : values()) {
            if (type.name().equalsIgnoreCase(skillName)) {
                return type;
            }
        }

        mcMMO.p.getLogger().warning("[DEBUG] Invalid mcMMO skill (" + skillName + ")");
        return null;
    }

    // TODO: This is a little "hacky", we probably need to add something to distinguish child skills in the enum, or to use another enum for them
    public boolean isChildSkill() {
        switch (this) {
        case SMELTING:
            return true;

        default:
            return false;
        }
    }
}
