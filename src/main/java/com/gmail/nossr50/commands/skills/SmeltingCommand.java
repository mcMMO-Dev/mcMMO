package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.smelting.Smelting;
import com.gmail.nossr50.skills.smelting.Smelting.Tier;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.TextComponentFactory;
import com.gmail.nossr50.util.player.UserManager;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SmeltingCommand extends SkillCommand {
    private String burnTimeModifier;
    private String secondSmeltChance;
    private String secondSmeltChanceLucky;
    private String fluxMiningChance;
    private String fluxMiningChanceLucky;

    private boolean canFuelEfficiency;
    private boolean canSecondSmelt;
    private boolean canFluxMine;
    private boolean canVanillaXPBoost;

    public SmeltingCommand() {
        super(PrimarySkillType.SMELTING);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue, boolean isLucky) {
        // FUEL EFFICIENCY
        if (canFuelEfficiency) {
            burnTimeModifier = decimal.format(1 + ((skillValue / Smelting.burnModifierMaxLevel) * Smelting.burnTimeMultiplier));
        }

        // SECOND SMELT
        if (canSecondSmelt) {
            String[] secondSmeltStrings = calculateAbilityDisplayValues(skillValue, SubSkillType.SMELTING_SECOND_SMELT, isLucky);
            secondSmeltChance = secondSmeltStrings[0];
            secondSmeltChanceLucky = secondSmeltStrings[1];
        }

        // FLUX MINING
        if (canFluxMine) {
            String[] fluxMiningStrings = calculateAbilityDisplayValues(Smelting.fluxMiningChance, isLucky);
            fluxMiningChance = fluxMiningStrings[0];
            fluxMiningChanceLucky = fluxMiningStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canFuelEfficiency = canUseSubskill(player, SubSkillType.SMELTING_FUEL_EFFICIENCY);
        canSecondSmelt = canUseSubskill(player, SubSkillType.SMELTING_SECOND_SMELT);
        canFluxMine = canUseSubskill(player, SubSkillType.SMELTING_FLUX_MINING);
        canVanillaXPBoost = Permissions.vanillaXpBoost(player, skill);
    }

    @Override
    protected List<String> effectsDisplay() {
        List<String> messages = new ArrayList<String>();

        if (canFuelEfficiency) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Smelting.Effect.0"), LocaleLoader.getString("Smelting.Effect.1")));
        }

        if (canSecondSmelt) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Smelting.Effect.2"), LocaleLoader.getString("Smelting.Effect.3")));
        }

        if (canVanillaXPBoost) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Smelting.Effect.4"), LocaleLoader.getString("Smelting.Effect.5")));
        }

        if (canFluxMine) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Smelting.Effect.6"), LocaleLoader.getString("Smelting.Effect.7")));
        }

        return messages;
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<String>();

        if (canFuelEfficiency) {
            messages.add(LocaleLoader.getString("Smelting.Ability.FuelEfficiency", burnTimeModifier));
        }

        if (canSecondSmelt) {
            messages.add(LocaleLoader.getString("Smelting.Ability.SecondSmelt", secondSmeltChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", secondSmeltChanceLucky) : ""));
        }

        if (canVanillaXPBoost) {
            int unlockLevel = AdvancedConfig.getInstance().getSmeltingRankLevel(Tier.ONE);

            if (skillValue < unlockLevel) {
                messages.add(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Smelting.Ability.Locked.0", unlockLevel)));
            }
            else {
                messages.add(LocaleLoader.getString("Smelting.Ability.VanillaXPBoost", UserManager.getPlayer(player).getSmeltingManager().getVanillaXpMultiplier()));
            }
        }

        if (canFluxMine) {
            if (skillValue < Smelting.fluxMiningUnlockLevel) {
                messages.add(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Smelting.Ability.Locked.1", Smelting.fluxMiningUnlockLevel)));
            }
            else {
                messages.add(LocaleLoader.getString("Smelting.Ability.FluxMining", fluxMiningChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", fluxMiningChanceLucky) : ""));
            }
        }

        return messages;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        List<TextComponent> textComponents = new ArrayList<>();

        TextComponentFactory.getSubSkillTextComponents(player, textComponents, PrimarySkillType.SMELTING);

        return textComponents;
    }
}
