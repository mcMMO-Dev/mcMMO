package com.gmail.nossr50.commands.skills;

import org.bukkit.Material;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.repair.ArcaneForging;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.repair.RepairManager;
import com.gmail.nossr50.skills.repair.Repairable;
import com.gmail.nossr50.util.Permissions;

public class RepairCommand extends SkillCommand {
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
        Repairable diamondRepairable = mcMMO.getRepairableManager().getRepairable(Material.DIAMOND_PICKAXE.getId());
        Repairable goldRepairable = mcMMO.getRepairableManager().getRepairable(Material.GOLD_PICKAXE.getId());
        Repairable ironRepairable = mcMMO.getRepairableManager().getRepairable(Material.IRON_PICKAXE.getId());
        Repairable stoneRepairable = mcMMO.getRepairableManager().getRepairable(Material.STONE_PICKAXE.getId());

        // TODO: This isn't really accurate - if they don't have pickaxes loaded it doesn't always mean the repair level is 0
        diamondLevel = (diamondRepairable == null) ? 0 : diamondRepairable.getMinimumLevel();
        goldLevel = (goldRepairable == null) ? 0 : goldRepairable.getMinimumLevel();
        ironLevel = (ironRepairable == null) ? 0 : ironRepairable.getMinimumLevel();
        stoneLevel = (stoneRepairable == null) ? 0 : stoneRepairable.getMinimumLevel();

        // REPAIR MASTERY
        if (canMasterRepair) {
            repairMasteryBonus = percent.format(Math.min(((Repair.repairMasteryMaxBonus / Repair.repairMasteryMaxBonusLevel) * skillValue), Repair.repairMasteryMaxBonus) / 100D);
        }

        // SUPER REPAIR
        if (canSuperRepair) {
            String[] superRepairStrings = calculateAbilityDisplayValues(Repair.superRepairMaxBonusLevel, Repair.superRepairMaxChance);
            superRepairChance = superRepairStrings[0];
            superRepairChanceLucky = superRepairStrings[1];
        }
    }

    @Override
    protected void permissionsCheck() {
        canSuperRepair = Permissions.superRepair(player);
        canMasterRepair = Permissions.repairMastery(player);
        canArcaneForge = Permissions.arcaneForging(player);
        canSalvage = Permissions.salvage(player);
        canRepairDiamond = Permissions.repairDiamond(player);
        canRepairGold = Permissions.repairGold(player);
        canRepairIron = Permissions.repairIron(player);
        canRepairStone = Permissions.repairStone(player);
        canRepairString = Permissions.repairString(player);
        canRepairLeather = Permissions.repairLeather(player);
        canRepairWood = Permissions.repairWood(player);
        arcaneBypass = Permissions.arcaneBypass(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canArcaneForge || canSalvage || canRepairDiamond || canRepairGold || canRepairIron || canMasterRepair || canRepairStone || canSuperRepair || canRepairString || canRepairWood || canRepairLeather;
    }

    @Override
    protected void effectsDisplay() {
        luckyEffectsDisplay();

        player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Repair.Effect.0"), LocaleLoader.getString("Repair.Effect.1")));

        if (canMasterRepair) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Repair.Effect.2"), LocaleLoader.getString("Repair.Effect.3")));
        }

        if (canSuperRepair) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Repair.Effect.4"), LocaleLoader.getString("Repair.Effect.5")));
        }

        /* Repair Level Requirements */

        if (canRepairStone && stoneLevel > 0) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Repair.Effect.14", stoneLevel), LocaleLoader.getString("Repair.Effect.15")));
        }

        if (canRepairIron && ironLevel > 0) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Repair.Effect.12", ironLevel), LocaleLoader.getString("Repair.Effect.13")));
        }

        if (canRepairGold && goldLevel > 0) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Repair.Effect.10", goldLevel), LocaleLoader.getString("Repair.Effect.11")));
        }

        if (canRepairDiamond && diamondLevel > 0) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Repair.Effect.6", diamondLevel), LocaleLoader.getString("Repair.Effect.7")));
        }

        if (canSalvage && Repair.salvageUnlockLevel > 0) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Repair.Effect.16", Repair.salvageUnlockLevel), LocaleLoader.getString("Repair.Effect.17")));
        }

        if (canArcaneForge) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Repair.Effect.8"), LocaleLoader.getString("Repair.Effect.9")));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canArcaneForge || canMasterRepair || canSuperRepair;
    }

    @Override
    protected void statsDisplay() {
        if (canMasterRepair) {
            player.sendMessage(LocaleLoader.getString("Repair.Skills.Mastery", repairMasteryBonus));
        }

        if (canSuperRepair) {
            player.sendMessage(LocaleLoader.getString("Repair.Skills.Super.Chance", superRepairChance) + (isLucky ? LocaleLoader.getString("Perks.lucky.bonus", superRepairChanceLucky) : ""));
        }

        if (canArcaneForge) {
            RepairManager repairManager = mcMMOPlayer.getRepairManager();

            player.sendMessage(LocaleLoader.getString("Repair.Arcane.Rank", repairManager.getArcaneForgingRank()));

            if (ArcaneForging.arcaneForgingEnchantLoss) {
                player.sendMessage(LocaleLoader.getString("Repair.Arcane.Chance.Success", (arcaneBypass ? 100 : repairManager.getKeepEnchantChance())));
            }

            if (ArcaneForging.arcaneForgingDowngrades) {
                player.sendMessage(LocaleLoader.getString("Repair.Arcane.Chance.Downgrade", (arcaneBypass ? 0 : repairManager.getDowngradeEnchantChance())));
            }
        }
    }
}
