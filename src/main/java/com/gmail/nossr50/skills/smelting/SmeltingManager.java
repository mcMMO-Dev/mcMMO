package com.gmail.nossr50.skills.smelting;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;

import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;

public class SmeltingManager extends SkillManager {

    public SmeltingManager(Player player) {
        super(player, SkillType.SMELTING);
    }

    /**
     * Increases burn time for furnace fuel.
     *
     * @param event The {@link FurnaceBurnEvent} to modify.
     */
    public void fuelEfficiency(FurnaceBurnEvent event) {
        if (Misc.isNPCPlayer(player) || !Permissions.fuelEfficiency(player)) {
            return;
        }

        FuelEfficiencyEventHandler eventHandler = new FuelEfficiencyEventHandler(this, event);
        eventHandler.calculateBurnModifier();
        eventHandler.modifyBurnTime();
    }

    public void smeltProcessing(FurnaceSmeltEvent event) {
        if (Misc.isNPCPlayer(player)) {
            return;
        }

        SmeltResourceEventHandler eventHandler = new SmeltResourceEventHandler(this, event);

        if (Permissions.smelting(player)) {
            eventHandler.handleXPGain();
        }

        if (!Permissions.secondSmelt(player)) {
            return;
        }

        eventHandler.calculateSkillModifier();

        double chance = (Smelting.secondSmeltMaxChance / Smelting.secondSmeltMaxLevel) * eventHandler.skillModifier;
        if (chance > Misc.getRandom().nextInt(activationChance)) {
            eventHandler.handleBonusSmelts();
        }
    }

    public void fluxMining(BlockBreakEvent event) {
        if (skillLevel < Smelting.fluxMiningUnlockLevel) {
            return;
        }

        if (Smelting.fluxMiningChance > Misc.getRandom().nextInt(activationChance)) {
            FluxMiningEventHandler eventHandler = new FluxMiningEventHandler(this, event);
            eventHandler.processDrops();
            eventHandler.eventCancellationAndProcessing();
            eventHandler.sendAbilityMessage();
        }
    }

    public void vanillaXPBoost(FurnaceExtractEvent event) {
        if (skillLevel < Smelting.vanillaXPBoostRank1Level || !Permissions.smeltingVanillaXPBoost(player)) {
            return;
        }

        SmeltingVanillaXPEventHandler eventHandler = new SmeltingVanillaXPEventHandler(this, event);
        eventHandler.calculateModifier();
        eventHandler.modifyVanillaXP();
    }
}
