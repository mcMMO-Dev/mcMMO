package com.gmail.nossr50.datatypes.skills;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.acrobatics.AcrobaticsManager;
import com.gmail.nossr50.skills.alchemy.AlchemyManager;
import com.gmail.nossr50.skills.archery.ArcheryManager;
import com.gmail.nossr50.skills.axes.AxesManager;
import com.gmail.nossr50.skills.excavation.ExcavationManager;
import com.gmail.nossr50.skills.fishing.FishingManager;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.repair.RepairManager;
import com.gmail.nossr50.skills.salvage.SalvageManager;
import com.gmail.nossr50.skills.smelting.SmeltingManager;
import com.gmail.nossr50.skills.swords.SwordsManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.unarmed.UnarmedManager;
import com.gmail.nossr50.skills.woodcutting.WoodcuttingManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;

import com.google.common.collect.ImmutableList;

public enum PrimarySkill {
    ACROBATICS(AcrobaticsManager.class, Color.WHITE, ImmutableList.of(SubSkill.ACROBATICS_DODGE, SubSkill.ACROBATICS_GRACEFUL_ROLL, SubSkill.ACROBATICS_ROLL)),
    ALCHEMY(AlchemyManager.class, Color.FUCHSIA, ImmutableList.of(SubSkill.ALCHEMY_CATALYSIS, SubSkill.ALCHEMY_CONCOCTIONS)),
    ARCHERY(ArcheryManager.class, Color.MAROON, ImmutableList.of(SubSkill.ARCHERY_DAZE, SubSkill.ARCHERY_RETRIEVE, SubSkill.ARCHERY_SKILL_SHOT)),
    AXES(AxesManager.class, Color.AQUA, SuperAbility.SKULL_SPLITTER, ToolType.AXE, ImmutableList.of(SubSkill.AXES_ARMOR_IMPACT, SubSkill.AXES_AXE_MASTERY, SubSkill.AXES_CRITICAL_HIT, SubSkill.AXES_GREATER_IMPACT)),
    EXCAVATION(ExcavationManager.class, Color.fromRGB(139, 69, 19), SuperAbility.GIGA_DRILL_BREAKER, ToolType.SHOVEL, ImmutableList.of(SubSkill.EXCAVATION_TREASURE_HUNTER)),
    FISHING(FishingManager.class, Color.NAVY, ImmutableList.of(SubSkill.FISHING_FISHERMANS_DIET, SubSkill.FISHING_TREASURE_HUNTER, SubSkill.FISHING_ICE_FISHING, SubSkill.FISHING_MAGIC_HUNTER, SubSkill.FISHING_MASTER_ANGLER, SubSkill.FISHING_SHAKE)),
    HERBALISM(HerbalismManager.class, Color.GREEN, SuperAbility.GREEN_TERRA, ToolType.HOE, ImmutableList.of(SubSkill.HERBALISM_FARMERS_DIET, SubSkill.HERBALISM_GREEN_THUMB, SubSkill.HERBALISM_DOUBLE_DROPS, SubSkill.HERBALISM_HYLIAN_LUCK, SubSkill.HERBALISM_SHROOM_THUMB)),
    MINING(MiningManager.class, Color.GRAY, SuperAbility.SUPER_BREAKER, ToolType.PICKAXE, ImmutableList.of(SubSkill.MINING_DOUBLE_DROPS)),
    REPAIR(RepairManager.class, Color.SILVER, ImmutableList.of(SubSkill.REPAIR_ARCANE_FORGING, SubSkill.REPAIR_REPAIR_MASTERY, SubSkill.REPAIR_SUPER_REPAIR)),
    SALVAGE(SalvageManager.class, Color.ORANGE, ImmutableList.of(SubSkill.SALVAGE_ADVANCED_SALVAGE, SubSkill.SALVAGE_ARCANE_SALVAGE)),
    SMELTING(SmeltingManager.class, Color.YELLOW, ImmutableList.of(SubSkill.SMELTING_FLUX_MINING, SubSkill.SMELTING_FUEL_EFFICIENCY, SubSkill.SMELTING_SECOND_SMELT)),
    SWORDS(SwordsManager.class, Color.fromRGB(178, 34, 34), SuperAbility.SERRATED_STRIKES, ToolType.SWORD, ImmutableList.of(SubSkill.SWORDS_BLEED, SubSkill.SWORDS_COUNTER)),
    TAMING(TamingManager.class, Color.PURPLE, ImmutableList.of(SubSkill.TAMING_BEAST_LORE, SubSkill.TAMING_CALL_OF_THE_WILD, SubSkill.TAMING_ENVIRONMENTALLY_AWARE, SubSkill.TAMING_FAST_FOOD, SubSkill.TAMING_GORE, SubSkill.TAMING_HOLY_HOUND, SubSkill.TAMING_SHARPENED_CLAWS, SubSkill.TAMING_SHOCK_PROOF, SubSkill.TAMING_THICK_FUR, SubSkill.TAMING_PUMMEL)),
    UNARMED(UnarmedManager.class, Color.BLACK, SuperAbility.BERSERK, ToolType.FISTS, ImmutableList.of(SubSkill.UNARMED_BLOCK_CRACKER, SubSkill.UNARMED_DEFLECT, SubSkill.UNARMED_DISARM, SubSkill.UNARMED_IRON_ARM, SubSkill.UNARMED_IRON_GRIP)),
    WOODCUTTING(WoodcuttingManager.class, Color.OLIVE, SuperAbility.TREE_FELLER, ToolType.AXE, ImmutableList.of(SubSkill.WOODCUTTING_LEAF_BLOWER, SubSkill.WOODCUTTING_DOUBLE_DROPS));

    private Class<? extends SkillManager> managerClass;
    private Color runescapeColor;
    private SuperAbility ability;
    private ToolType tool;
    private List<SubSkill> subSkills;

    public static final List<String> SKILL_NAMES;

    public static final List<PrimarySkill> CHILD_SKILLS;
    public static final List<PrimarySkill> NON_CHILD_SKILLS;

    public static final List<PrimarySkill> COMBAT_SKILLS = ImmutableList.of(ARCHERY, AXES, SWORDS, TAMING, UNARMED);
    public static final List<PrimarySkill> GATHERING_SKILLS = ImmutableList.of(EXCAVATION, FISHING, HERBALISM, MINING, WOODCUTTING);
    public static final List<PrimarySkill> MISC_SKILLS = ImmutableList.of(ACROBATICS, ALCHEMY, REPAIR, SALVAGE, SMELTING);

    static {
        List<PrimarySkill> childSkills = new ArrayList<PrimarySkill>();
        List<PrimarySkill> nonChildSkills = new ArrayList<PrimarySkill>();
        ArrayList<String> names = new ArrayList<String>();

        for (PrimarySkill skill : values()) {
            if (skill.isChildSkill()) {
                childSkills.add(skill);
            }
            else {
                nonChildSkills.add(skill);
            }

            names.add(skill.getName());
        }

        Collections.sort(names);
        SKILL_NAMES = ImmutableList.copyOf(names);

        CHILD_SKILLS = ImmutableList.copyOf(childSkills);
        NON_CHILD_SKILLS = ImmutableList.copyOf(nonChildSkills);
    }

    private PrimarySkill(Class<? extends SkillManager> managerClass, Color runescapeColor, List<SubSkill> subSkills) {
        this(managerClass, runescapeColor, null, null, subSkills);
    }

    private PrimarySkill(Class<? extends SkillManager> managerClass, Color runescapeColor, SuperAbility ability, ToolType tool, List<SubSkill> subSkills) {
        this.managerClass = managerClass;
        this.runescapeColor = runescapeColor;
        this.ability = ability;
        this.tool = tool;
        this.subSkills = subSkills;
    }

    public Class<? extends SkillManager> getManagerClass() {
        return managerClass;
    }

    public SuperAbility getAbility() {
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

    public int getSkillAbilityGate() { return Config.getInstance().getSkillAbilityGate(this); }

    public boolean getPVPEnabled() {
        return Config.getInstance().getPVPEnabled(this);
    }

    public boolean getPVEEnabled() {
        return Config.getInstance().getPVEEnabled(this);
    }

    public boolean getDoubleDropsDisabled() {
        return Config.getInstance().getDoubleDropsDisabled(this);
    }

    public boolean getHardcoreStatLossEnabled() {
        return Config.getInstance().getHardcoreStatLossEnabled(this);
    }

    public void setHardcoreStatLossEnabled(boolean enable) {
        Config.getInstance().setHardcoreStatLossEnabled(this, enable);
    }

    public boolean getHardcoreVampirismEnabled() {
        return Config.getInstance().getHardcoreVampirismEnabled(this);
    }

    public void setHardcoreVampirismEnabled(boolean enable) {
        Config.getInstance().setHardcoreVampirismEnabled(this, enable);
    }

    public ToolType getTool() {
        return tool;
    }

    public List<SubSkill> getSkillAbilities() {
        return subSkills;
    }

    public double getXpModifier() {
        return ExperienceConfig.getInstance().getFormulaSkillModifier(this);
    }

    public static PrimarySkill getSkill(String skillName) {
        if (!Config.getInstance().getLocale().equalsIgnoreCase("en_US")) {
            for (PrimarySkill type : values()) {
                if (skillName.equalsIgnoreCase(LocaleLoader.getString(StringUtils.getCapitalized(type.name()) + ".SkillName"))) {
                    return type;
                }
            }
        }

        for (PrimarySkill type : values()) {
            if (type.name().equalsIgnoreCase(skillName)) {
                return type;
            }
        }

        if (!skillName.equalsIgnoreCase("all")) {
            mcMMO.p.getLogger().warning("Invalid mcMMO skill (" + skillName + ")"); //TODO: Localize
        }

        return null;
    }

    // TODO: This is a little "hacky", we probably need to add something to distinguish child skills in the enum, or to use another enum for them
    public boolean isChildSkill() {
        switch (this) {
            case SALVAGE:
            case SMELTING:
                return true;

            default:
                return false;
        }
    }

    public static PrimarySkill bySecondaryAbility(SubSkill subSkill) {
        for (PrimarySkill type : values()) {
            if (type.getSkillAbilities().contains(subSkill)) {
                return type;
            }
        }
        return null;
    }

    public static PrimarySkill byAbility(SuperAbility ability) {
        for (PrimarySkill type : values()) {
            if (type.getAbility() == ability) {
                return type;
            }
        }

        return null;
    }

    public String getName() {
        return Config.getInstance().getLocale().equalsIgnoreCase("en_US") ? StringUtils.getCapitalized(this.toString()) : StringUtils.getCapitalized(LocaleLoader.getString(StringUtils.getCapitalized(this.toString()) + ".SkillName"));
    }

    public boolean getPermissions(Player player) {
        return Permissions.skillEnabled(player, this);
    }

    public void celebrateLevelUp(Player player) {
        ParticleEffectUtils.fireworkParticleShower(player, runescapeColor);
    }

    public boolean shouldProcess(Entity target) {
        return (target instanceof Player || (target instanceof Tameable && ((Tameable) target).isTamed())) ? getPVPEnabled() : getPVEEnabled();
    }
}
