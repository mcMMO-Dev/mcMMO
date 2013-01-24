package com.gmail.nossr50.skills.smelting;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillCommand;
import com.gmail.nossr50.skills.SkillType;
import com.gmail.nossr50.util.Permissions;

public class SmeltingCommand extends SkillCommand {
    private String burnTimeModifier;
    private String secondSmeltChance;
    private String secondSmeltChanceLucky;
    private String fluxMiningChance;
    private String fluxMiningChanceLucky;
    private String vanillaXPModifier;

    private boolean canFuelEfficiency;
    private boolean canSecondSmelt;
    private boolean canFluxMine;
    private boolean canVanillaXPBoost;

    public SmeltingCommand() {
        super(SkillType.SMELTING);
    }

    @Override
    protected void dataCalculations() {
        //FUEL EFFICIENCY
        burnTimeModifier = String.valueOf(1 + ((skillValue / Smelting.burnModifierMaxLevel) * Smelting.burnTimeMultiplier));

        //SECOND SMELT
        String[] secondSmeltStrings = calculateAbilityDisplayValues(Smelting.secondSmeltMaxLevel, Smelting.secondSmeltMaxChance);
        secondSmeltChance = secondSmeltStrings[0];
        secondSmeltChanceLucky = secondSmeltStrings[1];

        //FLUX MINING
        String[] fluxMiningStrings = calculateAbilityDisplayValues(Smelting.fluxMiningChance);
        fluxMiningChance = fluxMiningStrings[0];
        fluxMiningChanceLucky = fluxMiningStrings[1];

        //VANILLA XP BOOST
        if (skillValue >= Smelting.vanillaXPBoostRank5Level) {
            vanillaXPModifier = String.valueOf(Smelting.vanillaXPBoostRank5Multiplier);
        }
        else if (skillValue >= Smelting.vanillaXPBoostRank4Level) {
            vanillaXPModifier = String.valueOf(Smelting.vanillaXPBoostRank4Multiplier);
        }
        else if (skillValue >= Smelting.vanillaXPBoostRank3Level) {
            vanillaXPModifier = String.valueOf(Smelting.vanillaXPBoostRank3Multiplier);
        }
        else if (skillValue >= Smelting.vanillaXPBoostRank2Level) {
            vanillaXPModifier = String.valueOf(Smelting.vanillaXPBoostRank2Multiplier);
        }
        else {
            vanillaXPModifier = String.valueOf(Smelting.vanillaXPBoostRank1Multiplier);
        }
    }

    @Override
    protected void permissionsCheck() {
        canFuelEfficiency = Permissions.fuelEfficiency(player);
        canSecondSmelt = Permissions.secondSmelt(player);
        canFluxMine = Permissions.fluxMining(player);
        canVanillaXPBoost = Permissions.smeltingVanillaXPBoost(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canFluxMine || canFuelEfficiency || canSecondSmelt || canVanillaXPBoost;
    }

    @Override
    protected void effectsDisplay() {
        luckyEffectsDisplay();

        if (canFuelEfficiency) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Smelting.Effect.0"), LocaleLoader.getString("Smelting.Effect.1") }));
        }

        if (canSecondSmelt) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Smelting.Effect.2"), LocaleLoader.getString("Smelting.Effect.3") }));
        }

        if (canVanillaXPBoost) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Smelting.Effect.4"), LocaleLoader.getString("Smelting.Effect.5") }));
        }

        if (canFluxMine) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Smelting.Effect.6"), LocaleLoader.getString("Smelting.Effect.7") }));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canFluxMine || canFuelEfficiency || canSecondSmelt || canVanillaXPBoost;
    }

    @Override
    protected void statsDisplay() {
        if (canFuelEfficiency) {
            player.sendMessage(LocaleLoader.getString("Smelting.Ability.FuelEfficiency", new Object[] { burnTimeModifier }));
        }

        if (canSecondSmelt) {
            if (isLucky) {
                player.sendMessage(LocaleLoader.getString("Smelting.Ability.SecondSmelt", new Object[] { secondSmeltChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { secondSmeltChanceLucky }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Smelting.Ability.SecondSmelt", new Object[] { secondSmeltChance }));
            }
        }

        if (canVanillaXPBoost) {
            if (skillValue < Smelting.vanillaXPBoostRank1Level) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Smelting.Ability.Locked.0", new Object[] { Smelting.vanillaXPBoostRank1Level } ) }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Smelting.Ability.VanillaXPBoost", new Object[] { vanillaXPModifier }));
            }
        }

        if (canFluxMine) {
            if (skillValue < Smelting.fluxMiningUnlockLevel) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Smelting.Ability.Locked.1", new Object[] { Smelting.fluxMiningUnlockLevel } ) }));
            }
            else if (isLucky) {
                player.sendMessage(LocaleLoader.getString("Smelting.Ability.FluxMining", new Object[] { fluxMiningChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { fluxMiningChanceLucky }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Smelting.Ability.FluxMining", new Object[] { fluxMiningChance }));
            }
        }
    }
}
