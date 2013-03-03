package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.smelting.Smelting;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;

public class SmeltingCommand extends SkillCommand {
    private String burnTimeModifier;
    private String secondSmeltChance;
    private String secondSmeltChanceLucky;
    private String fluxMiningChance;
    private String fluxMiningChanceLucky;

    private int vanillaXPModifier;

    private boolean canFuelEfficiency;
    private boolean canSecondSmelt;
    private boolean canFluxMine;
    private boolean canVanillaXPBoost;

    public SmeltingCommand() {
        super(SkillType.SMELTING);
    }

    @Override
    protected void dataCalculations() {
        // FUEL EFFICIENCY
        burnTimeModifier = decimal.format(1 + ((skillValue / Smelting.burnModifierMaxLevel) * Smelting.burnTimeMultiplier));

        // SECOND SMELT
        String[] secondSmeltStrings = calculateAbilityDisplayValues(Smelting.secondSmeltMaxLevel, Smelting.secondSmeltMaxChance);
        secondSmeltChance = secondSmeltStrings[0];
        secondSmeltChanceLucky = secondSmeltStrings[1];

        // FLUX MINING
        String[] fluxMiningStrings = calculateAbilityDisplayValues(Smelting.fluxMiningChance);
        fluxMiningChance = fluxMiningStrings[0];
        fluxMiningChanceLucky = fluxMiningStrings[1];

        // VANILLA XP BOOST
        vanillaXPModifier = UserManager.getPlayer(player).getSmeltingManager().getVanillaXpMultiplier();
    }

    @Override
    protected void permissionsCheck() {
        canFuelEfficiency = Permissions.fuelEfficiency(player);
        canSecondSmelt = Permissions.doubleDrops(player, skill);
        canFluxMine = Permissions.fluxMining(player);
        canVanillaXPBoost = Permissions.vanillaXpBoost(player, skill);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canFluxMine || canFuelEfficiency || canSecondSmelt || canVanillaXPBoost;
    }

    @Override
    protected void effectsDisplay() {
        luckyEffectsDisplay();

        if (canFuelEfficiency) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Smelting.Effect.0"), LocaleLoader.getString("Smelting.Effect.1")));
        }

        if (canSecondSmelt) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Smelting.Effect.2"), LocaleLoader.getString("Smelting.Effect.3")));
        }

        if (canVanillaXPBoost) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Smelting.Effect.4"), LocaleLoader.getString("Smelting.Effect.5")));
        }

        if (canFluxMine) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Smelting.Effect.6"), LocaleLoader.getString("Smelting.Effect.7")));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canFluxMine || canFuelEfficiency || canSecondSmelt || canVanillaXPBoost;
    }

    @Override
    protected void statsDisplay() {
        if (canFuelEfficiency) {
            player.sendMessage(LocaleLoader.getString("Smelting.Ability.FuelEfficiency", burnTimeModifier));
        }

        if (canSecondSmelt) {
            if (isLucky) {
                player.sendMessage(LocaleLoader.getString("Smelting.Ability.SecondSmelt", secondSmeltChance) + LocaleLoader.getString("Perks.lucky.bonus", secondSmeltChanceLucky));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Smelting.Ability.SecondSmelt", secondSmeltChance));
            }
        }

        if (canVanillaXPBoost) {
            if (skillValue < AdvancedConfig.getInstance().getSmeltingVanillaXPBoostRank1Level()) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Smelting.Ability.Locked.0", AdvancedConfig.getInstance().getSmeltingVanillaXPBoostRank1Level())));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Smelting.Ability.VanillaXPBoost", vanillaXPModifier));
            }
        }

        if (canFluxMine) {
            if (skillValue < Smelting.fluxMiningUnlockLevel) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Smelting.Ability.Locked.1", Smelting.fluxMiningUnlockLevel)));
            }
            else if (isLucky) {
                player.sendMessage(LocaleLoader.getString("Smelting.Ability.FluxMining", fluxMiningChance) + LocaleLoader.getString("Perks.lucky.bonus", fluxMiningChanceLucky));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Smelting.Ability.FluxMining", fluxMiningChance));
            }
        }
    }
}
