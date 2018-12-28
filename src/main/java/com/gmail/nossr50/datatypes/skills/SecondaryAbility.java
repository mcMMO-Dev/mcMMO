package com.gmail.nossr50.datatypes.skills;

import static com.gmail.nossr50.datatypes.skills.SkillType.*;
public enum SecondaryAbility {
    /* !! Warning -- Do not let subskills share a name with any existing SkillType as it will clash with the static import !! */

    /* ACROBATICS */
    DODGE(ACROBATICS),
    GRACEFUL_ROLL(ACROBATICS),
    ROLL(ACROBATICS),

    /* ALCHEMY */
    CATALYSIS(ALCHEMY),
    CONCOCTIONS(ALCHEMY),

    /* ARCHERY */
    DAZE(ARCHERY),
    RETRIEVE(ARCHERY),
    SKILL_SHOT(ARCHERY),

    /* Axes */
    ARMOR_IMPACT(AXES),
    AXE_MASTERY(AXES),
    CRITICAL_HIT(AXES),
    GREATER_IMPACT(AXES),

    /* Excavation */
    EXCAVATION_TREASURE_HUNTER(EXCAVATION),

    /* Fishing */
    FISHERMANS_DIET(FISHING),
    FISHING_TREASURE_HUNTER(FISHING),
    ICE_FISHING(FISHING),
    MAGIC_HUNTER(FISHING),
    MASTER_ANGLER(FISHING),
    SHAKE(FISHING),

    /* Herbalism */
    FARMERS_DIET(HERBALISM),
    GREEN_THUMB_PLANT(HERBALISM),
    GREEN_THUMB_BLOCK(HERBALISM),
    HERBALISM_DOUBLE_DROPS(HERBALISM),
    HYLIAN_LUCK(HERBALISM),
    SHROOM_THUMB(HERBALISM),

    /* Mining */
    MINING_DOUBLE_DROPS(MINING),

    /* Repair */
    ARCANE_FORGING(REPAIR),
    REPAIR_MASTERY(REPAIR),
    SUPER_REPAIR(REPAIR),

    /* Salvage */
    ADVANCED_SALVAGE(SALVAGE),
    ARCANE_SALVAGE(SALVAGE),

    /* Smelting */
    FLUX_MINING(SMELTING),
    FUEL_EFFICIENCY(SMELTING),
    SECOND_SMELT(SMELTING),

    /* Swords */
    BLEED(SWORDS),
    COUNTER(SWORDS),

    /* Taming */
    BEAST_LORE(TAMING),
    CALL_OF_THE_WILD(TAMING),
    ENVIRONMENTALLY_AWARE(TAMING),
    FAST_FOOD(TAMING),
    GORE(TAMING),
    HOLY_HOUND(TAMING),
    SHARPENED_CLAWS(TAMING),
    SHOCK_PROOF(TAMING),
    THICK_FUR(TAMING),
    PUMMEL(TAMING),

    /* Unarmed */
    BLOCK_CRACKER(UNARMED),
    DEFLECT(UNARMED),
    DISARM(UNARMED),
    IRON_ARM(UNARMED),
    IRON_GRIP(UNARMED),

    /* Woodcutting */
    WOODCUTTING_TREE_FELLER(WOODCUTTING),
    WOODCUTTING_LEAF_BLOWER(WOODCUTTING),
    WOODCUTTING_SURGEON(WOODCUTTING),
    WOODCUTTING_NATURES_BOUNTY(WOODCUTTING),
    WOODCUTTING_SPLINTER(WOODCUTTING),
    WOODCUTTING_HARVEST(WOODCUTTING);

    private final SkillType parentSkill;

    SecondaryAbility(SkillType parentSkill)
    {
        this.parentSkill = parentSkill;
    }

    public SkillType getParentSkill() { return parentSkill; }
}
