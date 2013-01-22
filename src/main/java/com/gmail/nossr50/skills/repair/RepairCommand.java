package com.gmail.nossr50.skills.repair;

import org.bukkit.Material;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillCommand;
import com.gmail.nossr50.skills.SkillType;
import com.gmail.nossr50.util.Permissions;

public class RepairCommand extends SkillCommand {
    private int arcaneForgingRank;
    private String repairMasteryBonus;
    private String superRepairChance;
    private String superRepairChanceLucky;

    private boolean canSuperRepair;
    private boolean canMasterRepair;
    private boolean canArcaneForge;
    private boolean canSalvage;
    private boolean canRepairStone;
    private boolean canRepairIron;
    private boolean canRepairGold;
    private boolean canRepairDiamond;
    private boolean canRepairString;
    private boolean canRepairLeather;
    private boolean canRepairWood;
    private boolean arcaneBypass;

    private int diamondLevel;
    private int goldLevel;
    private int ironLevel;
    private int stoneLevel;

    public RepairCommand() {
        super(SkillType.REPAIR);
    }

    @Override
    protected void dataCalculations() {
        // We're using pickaxes here, not the best but it works
        Repairable diamondRepairable = mcMMO.repairManager.getRepairable(Material.DIAMOND_PICKAXE.getId());
        Repairable goldRepairable = mcMMO.repairManager.getRepairable(Material.GOLD_PICKAXE.getId());
        Repairable ironRepairable = mcMMO.repairManager.getRepairable(Material.IRON_PICKAXE.getId());
        Repairable stoneRepairable = mcMMO.repairManager.getRepairable(Material.STONE_PICKAXE.getId());

        //TODO: This isn't really accurate - if they don't have pickaxes loaded it doesn't always mean the repair level is 0
        diamondLevel = (diamondRepairable == null) ? 0 : diamondRepairable.getMinimumLevel();
        goldLevel = (goldRepairable == null) ? 0 : goldRepairable.getMinimumLevel();
        ironLevel = (ironRepairable == null) ? 0 : ironRepairable.getMinimumLevel();
        stoneLevel = (stoneRepairable == null) ? 0 : stoneRepairable.getMinimumLevel();

        //REPAIR MASTERY
        if (skillValue >= Repair.REPAIR_MASTERY_MAX_BONUS_LEVEL) {
            repairMasteryBonus = percent.format(Repair.REPAIR_MASTERY_CHANCE_MAX / 100D);
        }
        else {
            repairMasteryBonus = percent.format((( Repair.REPAIR_MASTERY_CHANCE_MAX / Repair.REPAIR_MASTERY_MAX_BONUS_LEVEL) * skillValue) / 100D);
        }

        //SUPER REPAIR
        String[] superRepairStrings = calculateAbilityDisplayValues(Repair.SUPER_REPAIR_MAX_BONUS_LEVEL, Repair.SUPER_REPAIR_CHANCE_MAX);
        superRepairChance = superRepairStrings[0];
        superRepairChanceLucky = superRepairStrings[1];

        //ARCANE FORGING
        arcaneForgingRank = Repair.getArcaneForgingRank(profile);
    }

    @Override
    protected void permissionsCheck() {
        canSuperRepair = Permissions.repairBonus(player);
        canMasterRepair = Permissions.repairMastery(player);
        canArcaneForge = Permissions.arcaneForging(player);
        canSalvage = Permissions.salvage(player);
        canRepairDiamond = Permissions.diamondRepair(player);
        canRepairGold = Permissions.goldRepair(player);
        canRepairIron = Permissions.ironRepair(player);
        canRepairStone = Permissions.stoneRepair(player);
        canRepairString = Permissions.stringRepair(player);
        canRepairLeather = Permissions.leatherRepair(player);
        canRepairWood = Permissions.woodRepair(player);
        arcaneBypass = Permissions.arcaneBypass(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canArcaneForge || canSalvage || canRepairDiamond || canRepairGold || canRepairIron || canMasterRepair || canRepairStone || canSuperRepair || canRepairString || canRepairWood || canRepairLeather;
    }

    @Override
    protected void effectsDisplay() {
        luckyEffectsDisplay();

        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Repair.Effect.0"), LocaleLoader.getString("Repair.Effect.1") }));

        if (canMasterRepair) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Repair.Effect.2"), LocaleLoader.getString("Repair.Effect.3") }));
        }

        if (canSuperRepair) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Repair.Effect.4"), LocaleLoader.getString("Repair.Effect.5") }));
        }

        /* Repair Level Requirements */

        if (canRepairStone && stoneLevel > 0) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Repair.Effect.14", new Object[] { stoneLevel }), LocaleLoader.getString("Repair.Effect.15") }));
        }

        if (canRepairIron && ironLevel > 0) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Repair.Effect.12", new Object[] { ironLevel }), LocaleLoader.getString("Repair.Effect.13") }));
        }

        if (canRepairGold && goldLevel > 0) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Repair.Effect.10", new Object[] { goldLevel }), LocaleLoader.getString("Repair.Effect.11") }));
        }

        if (canRepairDiamond && diamondLevel > 0) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Repair.Effect.6", new Object[] { diamondLevel }), LocaleLoader.getString("Repair.Effect.7") }));
        }

        if (canSalvage && Salvage.salvageUnlockLevel > 0) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Repair.Effect.16", new Object[] { Salvage.salvageUnlockLevel }), LocaleLoader.getString("Repair.Effect.17") }));
        }

        if (canArcaneForge) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Repair.Effect.8"), LocaleLoader.getString("Repair.Effect.9") }));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canArcaneForge || canMasterRepair || canSuperRepair;
    }

    @Override
    protected void statsDisplay() {
        if (canMasterRepair) {
            player.sendMessage(LocaleLoader.getString("Repair.Skills.Mastery", new Object[] { repairMasteryBonus }));
        }

        if (canSuperRepair) {
            if (isLucky) {
                player.sendMessage(LocaleLoader.getString("Repair.Skills.Super.Chance", new Object[] { superRepairChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { superRepairChanceLucky }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Repair.Skills.Super.Chance", new Object[] { superRepairChance }));
            }
        }

        if (canArcaneForge) {
            player.sendMessage(LocaleLoader.getString("Repair.Arcane.Rank", new Object[] { arcaneForgingRank }));

            if (Repair.arcaneForgingEnchantLoss) {
                player.sendMessage(LocaleLoader.getString("Repair.Arcane.Chance.Success", new Object[] { (arcaneBypass ? 100 : Repair.getEnchantChance(arcaneForgingRank)) }));
            }

            if (Repair.arcaneForgingDowngrades) {
                player.sendMessage(LocaleLoader.getString("Repair.Arcane.Chance.Downgrade", new Object[] { (arcaneBypass ? 0 : Repair.getDowngradeChance(arcaneForgingRank)) }));
            }
        }
    }
}
