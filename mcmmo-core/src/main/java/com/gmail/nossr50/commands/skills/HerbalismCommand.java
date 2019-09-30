package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HerbalismCommand extends SkillCommand {
    private String greenTerraLength;
    private String greenTerraLengthEndurance;
    private String greenThumbChance;
    private String greenThumbChanceLucky;
    private int greenThumbStage;
    private int farmersDietRank;
    private String doubleDropChance;
    private String doubleDropChanceLucky;
    private String hylianLuckChance;
    private String hylianLuckChanceLucky;
    private String shroomThumbChance;
    private String shroomThumbChanceLucky;

    private boolean hasHylianLuck;
    private boolean canGreenTerra;
    private boolean canGreenThumbPlants;
    private boolean canGreenThumbBlocks;
    private boolean canFarmersDiet;
    private boolean canDoubleDrop;
    private boolean canShroomThumb;

    public HerbalismCommand(mcMMO pluginRef) {
        super(PrimarySkillType.HERBALISM, pluginRef);
    }

    @Override
    protected void dataCalculations(Player player, double skillValue) {

        // DOUBLE DROPS
        if (canDoubleDrop) {
            String[] doubleDropStrings = getAbilityDisplayValues(player, SubSkillType.HERBALISM_DOUBLE_DROPS);
            doubleDropChance = doubleDropStrings[0];
            doubleDropChanceLucky = doubleDropStrings[1];
        }

        // FARMERS DIET
        if (canFarmersDiet) {
            farmersDietRank = pluginRef.getRankTools().getRank(player, SubSkillType.HERBALISM_FARMERS_DIET);
        }

        // GREEN TERRA
        if (canGreenTerra) {
            String[] greenTerraStrings = formatLengthDisplayValues(player, skillValue);
            greenTerraLength = greenTerraStrings[0];
            greenTerraLengthEndurance = greenTerraStrings[1];
        }

        // GREEN THUMB
        if (canGreenThumbBlocks || canGreenThumbPlants) {
            greenThumbStage = pluginRef.getRankTools().getRank(player, SubSkillType.HERBALISM_GREEN_THUMB);

            String[] greenThumbStrings = getAbilityDisplayValues(player, SubSkillType.HERBALISM_GREEN_THUMB);
            greenThumbChance = greenThumbStrings[0];
            greenThumbChanceLucky = greenThumbStrings[1];
        }

        // HYLIAN LUCK
        if (hasHylianLuck) {
            String[] hylianLuckStrings = getAbilityDisplayValues(player, SubSkillType.HERBALISM_HYLIAN_LUCK);
            hylianLuckChance = hylianLuckStrings[0];
            hylianLuckChanceLucky = hylianLuckStrings[1];
        }

        // SHROOM THUMB
        if (canShroomThumb) {
            String[] shroomThumbStrings = getAbilityDisplayValues(player, SubSkillType.HERBALISM_SHROOM_THUMB);
            shroomThumbChance = shroomThumbStrings[0];
            shroomThumbChanceLucky = shroomThumbStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        hasHylianLuck = canUseSubSkill(player, SubSkillType.HERBALISM_HYLIAN_LUCK);
        canGreenTerra = pluginRef.getPermissionTools().greenTerra(player);
        canGreenThumbPlants = pluginRef.getRankTools().hasUnlockedSubskill(player, SubSkillType.HERBALISM_GREEN_THUMB) && (pluginRef.getPermissionTools().greenThumbPlant(player, Material.WHEAT) || pluginRef.getPermissionTools().greenThumbPlant(player, Material.CARROT) || pluginRef.getPermissionTools().greenThumbPlant(player, Material.POTATO) || pluginRef.getPermissionTools().greenThumbPlant(player, Material.BEETROOT) || pluginRef.getPermissionTools().greenThumbPlant(player, Material.NETHER_WART) || pluginRef.getPermissionTools().greenThumbPlant(player, Material.COCOA));
        canGreenThumbBlocks = pluginRef.getRankTools().hasUnlockedSubskill(player, SubSkillType.HERBALISM_GREEN_THUMB) && (pluginRef.getPermissionTools().greenThumbBlock(player, Material.DIRT) || pluginRef.getPermissionTools().greenThumbBlock(player, Material.COBBLESTONE) || pluginRef.getPermissionTools().greenThumbBlock(player, Material.COBBLESTONE_WALL) || pluginRef.getPermissionTools().greenThumbBlock(player, Material.STONE_BRICKS));
        canFarmersDiet = canUseSubSkill(player, SubSkillType.HERBALISM_FARMERS_DIET);
        canDoubleDrop = canUseSubSkill(player, SubSkillType.HERBALISM_DOUBLE_DROPS);
        canShroomThumb = canUseSubSkill(player, SubSkillType.HERBALISM_SHROOM_THUMB);
    }

    @Override
    protected List<String> statsDisplay(Player player, double skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<>();

        if (canDoubleDrop) {
            messages.add(getStatMessage(SubSkillType.HERBALISM_DOUBLE_DROPS, doubleDropChance)
                    + (isLucky ? pluginRef.getLocaleManager().getString("Perks.Lucky.Bonus", doubleDropChanceLucky) : ""));
        }

        if (canFarmersDiet) {
            messages.add(getStatMessage(false, true, SubSkillType.HERBALISM_FARMERS_DIET, String.valueOf(farmersDietRank)));
        }

        if (canGreenTerra) {
            messages.add(getStatMessage(SubSkillType.HERBALISM_GREEN_TERRA, greenTerraLength)
                    + (hasEndurance ? pluginRef.getLocaleManager().getString("Perks.ActivationTime.Bonus", greenTerraLengthEndurance) : ""));

            //messages.add(pluginRef.getLocaleManager().getString("Herbalism.Ability.GTe.Length", greenTerraLength) + (hasEndurance ? pluginRef.getLocaleManager().getString("Perks.ActivationTime.Bonus", greenTerraLengthEndurance) : ""));
        }

        if (canGreenThumbBlocks || canGreenThumbPlants) {
            messages.add(getStatMessage(SubSkillType.HERBALISM_GREEN_THUMB, greenThumbChance)
                    + (isLucky ? pluginRef.getLocaleManager().getString("Perks.Lucky.Bonus", greenThumbChanceLucky) : ""));
            //messages.add(pluginRef.getLocaleManager().getString("Herbalism.Ability.GTh.Chance", greenThumbChance) + (isLucky ? pluginRef.getLocaleManager().getString("Perks.Lucky.Bonus", greenThumbChanceLucky) : ""));
        }

        if (canGreenThumbPlants) {
            messages.add(getStatMessage(true, true, SubSkillType.HERBALISM_GREEN_THUMB, String.valueOf(greenThumbStage)));
        }

        if (hasHylianLuck) {
            messages.add(getStatMessage(SubSkillType.HERBALISM_HYLIAN_LUCK, hylianLuckChance)
                    + (isLucky ? pluginRef.getLocaleManager().getString("Perks.Lucky.Bonus", hylianLuckChanceLucky) : ""));
        }

        if (canShroomThumb) {
            messages.add(getStatMessage(SubSkillType.HERBALISM_SHROOM_THUMB, shroomThumbChance)
                    + (isLucky ? pluginRef.getLocaleManager().getString("Perks.Lucky.Bonus", shroomThumbChanceLucky) : ""));
        }

        return messages;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        List<TextComponent> textComponents = new ArrayList<>();

        pluginRef.getTextComponentFactory().getSubSkillTextComponents(player, textComponents, PrimarySkillType.HERBALISM);

        return textComponents;
    }
}
