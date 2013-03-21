package com.gmail.nossr50.datatypes.skills;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.acrobatics.AcrobaticsManager;
import com.gmail.nossr50.skills.archery.ArcheryManager;
import com.gmail.nossr50.skills.axes.AxesManager;
import com.gmail.nossr50.skills.excavation.ExcavationManager;
import com.gmail.nossr50.skills.fishing.FishingManager;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.repair.RepairManager;
import com.gmail.nossr50.skills.smelting.SmeltingManager;
import com.gmail.nossr50.skills.swords.SwordsManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.unarmed.UnarmedManager;
import com.gmail.nossr50.skills.woodcutting.WoodcuttingManager;
import com.gmail.nossr50.util.StringUtils;

public enum SkillType {
    ACROBATICS(AcrobaticsManager.class),
    ARCHERY(ArcheryManager.class),
    AXES(AxesManager.class, AbilityType.SKULL_SPLITTER, ToolType.AXE),
    EXCAVATION(ExcavationManager.class, AbilityType.GIGA_DRILL_BREAKER, ToolType.SHOVEL),
    FISHING(FishingManager.class),
    HERBALISM(HerbalismManager.class, AbilityType.GREEN_TERRA, ToolType.HOE),
    MINING(MiningManager.class, AbilityType.SUPER_BREAKER, ToolType.PICKAXE),
    REPAIR(RepairManager.class),
    SMELTING(SmeltingManager.class),
    SWORDS(SwordsManager.class, AbilityType.SERRATED_STRIKES, ToolType.SWORD),
    TAMING(TamingManager.class),
    UNARMED(UnarmedManager.class, AbilityType.BERSERK, ToolType.FISTS),
    WOODCUTTING(WoodcuttingManager.class, AbilityType.TREE_FELLER, ToolType.AXE);

    private Class<? extends SkillManager> managerClass;
    private AbilityType ability;
    private ToolType tool;

    private SkillType(Class<? extends SkillManager> managerClass) {
        this.managerClass = managerClass;
        ability = null;
        tool = null;
    }

    private SkillType(Class<? extends SkillManager> managerClass, AbilityType ability, ToolType tool) {
        this.managerClass = managerClass;
        this.ability = ability;
        this.tool = tool;
    }

    public Class<? extends SkillManager> getManagerClass() {
        return managerClass;
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

        mcMMO.p.getLogger().warning("[Debug] Invalid mcMMO skill (" + skillName + ")");
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
