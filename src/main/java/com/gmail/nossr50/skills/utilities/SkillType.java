package com.gmail.nossr50.skills.utilities;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.StringUtils;

public enum SkillType {
    ACROBATICS,
    ARCHERY,
    AXES(AbilityType.SKULL_SPLITTER, ToolType.AXE),
    EXCAVATION(AbilityType.GIGA_DRILL_BREAKER, ToolType.SHOVEL),
    FISHING,
    HERBALISM(AbilityType.GREEN_TERRA, ToolType.HOE),
    MINING(AbilityType.SUPER_BREAKER, ToolType.PICKAXE),
    REPAIR,
    SMELTING,
    SWORDS(AbilityType.SERRATED_STRIKES, ToolType.SWORD),
    TAMING,
    UNARMED(AbilityType.BERSERK, ToolType.FISTS),
    WOODCUTTING(AbilityType.TREE_FELLER, ToolType.AXE);

    private AbilityType ability;
    private ToolType tool;

    private SkillType() {
        this.ability = null;
        this.tool = null;
    }

    private SkillType(AbilityType ability, ToolType tool) {
        this.ability = ability;
        this.tool = tool;
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
        return Config.getInstance().getLevelCap(this);
    }

    public boolean getPVPEnabled() {
        return Config.getInstance().getPVPEnabled(this);
    }

    public boolean getPVEEnabled() {
        return Config.getInstance().getPVEEnabled(this);
    }

    public boolean getDoubleDropsDisabled() {
        return Config.getInstance().getDoubleDropsDisabled(this);
    }

    public ToolType getTool() {
        return tool;
    }

    public double getXpModifier() {
        return Config.getInstance().getForumulaMultiplier(this);
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
