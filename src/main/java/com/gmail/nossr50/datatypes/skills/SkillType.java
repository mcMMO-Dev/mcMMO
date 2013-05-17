package com.gmail.nossr50.datatypes.skills;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Color;

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
import com.gmail.nossr50.util.skills.SkillUtils;
import com.google.common.collect.ImmutableList;

public enum SkillType {
    ACROBATICS(AcrobaticsManager.class, Color.WHITE),
    ARCHERY(ArcheryManager.class, Color.MAROON),
    AXES(AxesManager.class, Color.AQUA, AbilityType.SKULL_SPLITTER, ToolType.AXE),
    EXCAVATION(ExcavationManager.class, Color.fromRGB(139, 69, 19), AbilityType.GIGA_DRILL_BREAKER, ToolType.SHOVEL),
    FISHING(FishingManager.class, Color.NAVY),
    HERBALISM(HerbalismManager.class, Color.GREEN, AbilityType.GREEN_TERRA, ToolType.HOE),
    MINING(MiningManager.class, Color.GRAY, AbilityType.SUPER_BREAKER, ToolType.PICKAXE),
    REPAIR(RepairManager.class, Color.SILVER),
    SMELTING(SmeltingManager.class, Color.YELLOW),
    SWORDS(SwordsManager.class, Color.fromRGB(178, 34, 34), AbilityType.SERRATED_STRIKES, ToolType.SWORD),
    TAMING(TamingManager.class, Color.PURPLE),
    UNARMED(UnarmedManager.class, Color.BLACK, AbilityType.BERSERK, ToolType.FISTS),
    WOODCUTTING(WoodcuttingManager.class, Color.OLIVE, AbilityType.TREE_FELLER, ToolType.AXE);

    private Class<? extends SkillManager> managerClass;
    private Color runescapeColor;
    private AbilityType ability;
    private ToolType tool;

    public static final List<String> SKILL_NAMES;

    static {
        ArrayList<String> names = new ArrayList<String>();

        for (SkillType skill : values()) {
            names.add(SkillUtils.getSkillName(skill));
        }

        Collections.sort(names);
        SKILL_NAMES = ImmutableList.copyOf(names);
    }

    private SkillType(Class<? extends SkillManager> managerClass, Color runescapeColor) {
        this.managerClass = managerClass;
        this.runescapeColor = runescapeColor;
        ability = null;
        tool = null;
    }

    private SkillType(Class<? extends SkillManager> managerClass, Color runescapeColor, AbilityType ability, ToolType tool) {
        this.managerClass = managerClass;
        this.runescapeColor = runescapeColor;
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

    public static SkillType[] nonChildSkills() {
        return new SkillType[] {SkillType.ACROBATICS,
                SkillType.ARCHERY,
                SkillType.AXES,
                SkillType.EXCAVATION,
                SkillType.FISHING,
                SkillType.HERBALISM,
                SkillType.MINING,
                SkillType.REPAIR,
                SkillType.SWORDS,
                SkillType.TAMING,
                SkillType.UNARMED,
                SkillType.WOODCUTTING };
    }

    public static SkillType[] childSkills() {
        return new SkillType[] { SkillType.SMELTING };
    }

    public Color getRunescapeModeColor() {
        return runescapeColor;
    }

    public static SkillType byAbility(AbilityType ability) {
        for (SkillType type : values()) {
            if (type.getAbility() == ability) {
                return type;
            }
        }
        
        return null;
    }
}
