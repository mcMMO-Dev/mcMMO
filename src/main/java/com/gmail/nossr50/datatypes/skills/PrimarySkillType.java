package com.gmail.nossr50.datatypes.skills;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
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
import com.gmail.nossr50.util.skills.RankUtils;
import com.google.common.collect.ImmutableList;
import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum PrimarySkillType {
    ACROBATICS(AcrobaticsManager.class, Color.WHITE,
            ImmutableList.of(SubSkillType.ACROBATICS_DODGE, SubSkillType.ACROBATICS_ROLL)),
    ALCHEMY(AlchemyManager.class, Color.FUCHSIA,
            ImmutableList.of(SubSkillType.ALCHEMY_CATALYSIS, SubSkillType.ALCHEMY_CONCOCTIONS)),
    ARCHERY(ArcheryManager.class, Color.MAROON,
            ImmutableList.of(SubSkillType.ARCHERY_DAZE, SubSkillType.ARCHERY_ARCHERY_LIMIT_BREAK, SubSkillType.ARCHERY_ARROW_RETRIEVAL, SubSkillType.ARCHERY_SKILL_SHOT)),
    AXES(AxesManager.class, Color.AQUA, SuperAbilityType.SKULL_SPLITTER, ToolType.AXE,
            ImmutableList.of(SubSkillType.AXES_SKULL_SPLITTER, SubSkillType.AXES_AXES_LIMIT_BREAK, SubSkillType.AXES_ARMOR_IMPACT, SubSkillType.AXES_AXE_MASTERY, SubSkillType.AXES_CRITICAL_STRIKES, SubSkillType.AXES_GREATER_IMPACT)),
    EXCAVATION(ExcavationManager.class, Color.fromRGB(139, 69, 19), SuperAbilityType.GIGA_DRILL_BREAKER, ToolType.SHOVEL,
            ImmutableList.of(SubSkillType.EXCAVATION_GIGA_DRILL_BREAKER, SubSkillType.EXCAVATION_ARCHAEOLOGY)),
    FISHING(FishingManager.class, Color.NAVY,
            ImmutableList.of(SubSkillType.FISHING_FISHERMANS_DIET, SubSkillType.FISHING_TREASURE_HUNTER, SubSkillType.FISHING_ICE_FISHING, SubSkillType.FISHING_MAGIC_HUNTER, SubSkillType.FISHING_MASTER_ANGLER, SubSkillType.FISHING_SHAKE)),
    HERBALISM(HerbalismManager.class, Color.GREEN, SuperAbilityType.GREEN_TERRA, ToolType.HOE,
            ImmutableList.of(SubSkillType.HERBALISM_GREEN_TERRA, SubSkillType.HERBALISM_FARMERS_DIET, SubSkillType.HERBALISM_GREEN_THUMB, SubSkillType.HERBALISM_DOUBLE_DROPS, SubSkillType.HERBALISM_HYLIAN_LUCK, SubSkillType.HERBALISM_SHROOM_THUMB)),
    MINING(MiningManager.class, Color.GRAY, SuperAbilityType.SUPER_BREAKER, ToolType.PICKAXE,
            ImmutableList.of(SubSkillType.MINING_SUPER_BREAKER, SubSkillType.MINING_DEMOLITIONS_EXPERTISE, SubSkillType.MINING_BIGGER_BOMBS, SubSkillType.MINING_BLAST_MINING, SubSkillType.MINING_DOUBLE_DROPS)),
    REPAIR(RepairManager.class, Color.SILVER,
            ImmutableList.of(SubSkillType.REPAIR_ARCANE_FORGING, SubSkillType.REPAIR_REPAIR_MASTERY, SubSkillType.REPAIR_SUPER_REPAIR)),
    SALVAGE(SalvageManager.class, Color.ORANGE,
            ImmutableList.of(SubSkillType.SALVAGE_SCRAP_COLLECTOR, SubSkillType.SALVAGE_ARCANE_SALVAGE)),
    SMELTING(SmeltingManager.class, Color.YELLOW,
            ImmutableList.of(SubSkillType.SMELTING_UNDERSTANDING_THE_ART, /*SubSkillType.SMELTING_FLUX_MINING,*/ SubSkillType.SMELTING_FUEL_EFFICIENCY, SubSkillType.SMELTING_SECOND_SMELT)),
    SWORDS(SwordsManager.class, Color.fromRGB(178, 34, 34), SuperAbilityType.SERRATED_STRIKES, ToolType.SWORD,
            ImmutableList.of(SubSkillType.SWORDS_SERRATED_STRIKES, SubSkillType.SWORDS_SWORDS_LIMIT_BREAK, SubSkillType.SWORDS_STAB, SubSkillType.SWORDS_RUPTURE, SubSkillType.SWORDS_COUNTER_ATTACK)),
    TAMING(TamingManager.class, Color.PURPLE,
            ImmutableList.of(SubSkillType.TAMING_BEAST_LORE, SubSkillType.TAMING_CALL_OF_THE_WILD, SubSkillType.TAMING_ENVIRONMENTALLY_AWARE, SubSkillType.TAMING_FAST_FOOD_SERVICE, SubSkillType.TAMING_GORE, SubSkillType.TAMING_HOLY_HOUND, SubSkillType.TAMING_SHARPENED_CLAWS, SubSkillType.TAMING_SHOCK_PROOF, SubSkillType.TAMING_THICK_FUR, SubSkillType.TAMING_PUMMEL)),
    UNARMED(UnarmedManager.class, Color.BLACK, SuperAbilityType.BERSERK, ToolType.FISTS,
            ImmutableList.of(SubSkillType.UNARMED_BERSERK, SubSkillType.UNARMED_UNARMED_LIMIT_BREAK, SubSkillType.UNARMED_BLOCK_CRACKER, SubSkillType.UNARMED_ARROW_DEFLECT, SubSkillType.UNARMED_DISARM, SubSkillType.UNARMED_IRON_ARM_STYLE, SubSkillType.UNARMED_IRON_GRIP)),
    WOODCUTTING(WoodcuttingManager.class, Color.OLIVE, SuperAbilityType.TREE_FELLER, ToolType.AXE,
            ImmutableList.of(SubSkillType.WOODCUTTING_LEAF_BLOWER, SubSkillType.WOODCUTTING_TREE_FELLER, SubSkillType.WOODCUTTING_HARVEST_LUMBER));

    private Class<? extends SkillManager> managerClass;
    private Color skillColor;
    private SuperAbilityType ability;
    private ToolType tool;
    private List<SubSkillType> subSkillTypes;

    public static final List<String> SKILL_NAMES;
    public static final List<String> SUBSKILL_NAMES;

    public static final List<PrimarySkillType> CHILD_SKILLS;
    public static final List<PrimarySkillType> NON_CHILD_SKILLS;

    public static final List<PrimarySkillType> COMBAT_SKILLS = ImmutableList.of(ARCHERY, AXES, SWORDS, TAMING, UNARMED);
    public static final List<PrimarySkillType> GATHERING_SKILLS = ImmutableList.of(EXCAVATION, FISHING, HERBALISM, MINING, WOODCUTTING);
    public static final List<PrimarySkillType> MISC_SKILLS = ImmutableList.of(ACROBATICS, ALCHEMY, REPAIR, SALVAGE, SMELTING);

    static {
        List<PrimarySkillType> childSkills = new ArrayList<PrimarySkillType>();
        List<PrimarySkillType> nonChildSkills = new ArrayList<PrimarySkillType>();
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> subSkillNames = new ArrayList<>();

        for (PrimarySkillType skill : values()) {
            if (skill.isChildSkill()) {
                childSkills.add(skill);
            }
            else {
                nonChildSkills.add(skill);
            }

            for(SubSkillType subSkillType : skill.subSkillTypes)
            {
                subSkillNames.add(subSkillType.getNiceNameNoSpaces(subSkillType));
            }
            names.add(skill.getName());
        }

        Collections.sort(names);
        SKILL_NAMES = ImmutableList.copyOf(names);
        SUBSKILL_NAMES = ImmutableList.copyOf(subSkillNames);

        CHILD_SKILLS = ImmutableList.copyOf(childSkills);
        NON_CHILD_SKILLS = ImmutableList.copyOf(nonChildSkills);
    }

    private PrimarySkillType(Class<? extends SkillManager> managerClass, Color skillColor, List<SubSkillType> subSkillTypes) {
        this(managerClass, skillColor, null, null, subSkillTypes);
    }

    private PrimarySkillType(Class<? extends SkillManager> managerClass, Color skillColor, SuperAbilityType ability, ToolType tool, List<SubSkillType> subSkillTypes) {
        this.managerClass = managerClass;
        this.skillColor = skillColor;
        this.ability = ability;
        this.tool = tool;
        this.subSkillTypes = subSkillTypes;
    }

    public Class<? extends SkillManager> getManagerClass() {
        return managerClass;
    }

    public SuperAbilityType getAbility() {
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

    public boolean isSuperAbilityUnlocked(Player player) { return RankUtils.getRank(player, getAbility().getSubSkillTypeDefinition()) >= 1; }

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

    public List<SubSkillType> getSkillAbilities() {
        return subSkillTypes;
    }

    public double getXpModifier() {
        return ExperienceConfig.getInstance().getFormulaSkillModifier(this);
    }

    public static PrimarySkillType getSkill(String skillName) {
        if (!Config.getInstance().getLocale().equalsIgnoreCase("en_US")) {
            for (PrimarySkillType type : values()) {
                if (skillName.equalsIgnoreCase(LocaleLoader.getString(StringUtils.getCapitalized(type.name()) + ".SkillName"))) {
                    return type;
                }
            }
        }

        for (PrimarySkillType type : values()) {
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

    public static PrimarySkillType bySecondaryAbility(SubSkillType subSkillType) {
        for (PrimarySkillType type : values()) {
            if (type.getSkillAbilities().contains(subSkillType)) {
                return type;
            }
        }
        return null;
    }

    public static PrimarySkillType byAbility(SuperAbilityType ability) {
        for (PrimarySkillType type : values()) {
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

/*    public void celebrateLevelUp(Player player) {
        ParticleEffectUtils.fireworkParticleShower(player, skillColor);
    }*/

    public boolean shouldProcess(Entity target) {
        return (target instanceof Player || (target instanceof Tameable && ((Tameable) target).isTamed())) ? getPVPEnabled() : getPVEEnabled();
    }
}
