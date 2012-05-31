package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.repair.Repairable;

public class RepairCommand extends SkillCommand {
    private int arcaneForgingRank;
    private String repairMasteryBonus;
    private String superRepairChance;

    private boolean canSuperRepair;
    private boolean canMasterRepair;
    private boolean canArcaneForge;
    private boolean canRepairStone;
    private boolean canRepairIron;
    private boolean canRepairGold;
    private boolean canRepairDiamond;
    private boolean canRepairString;
    private boolean canRepairLeather;
    private boolean canRepairWood;

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
        Repairable diamondRepairable = mcMMO.repairManager.getRepairable(278);
        Repairable goldRepairable = mcMMO.repairManager.getRepairable(285);
        Repairable ironRepairable = mcMMO.repairManager.getRepairable(257);
        Repairable stoneRepairable = mcMMO.repairManager.getRepairable(274);

        diamondLevel = (diamondRepairable == null) ? 0 : diamondRepairable.getMinimumLevel();
        goldLevel = (goldRepairable == null) ? 0 : goldRepairable.getMinimumLevel();
        ironLevel = (ironRepairable == null) ? 0 : ironRepairable.getMinimumLevel();
        stoneLevel = (stoneRepairable == null) ? 0 : stoneRepairable.getMinimumLevel();

        repairMasteryBonus = percent.format(skillValue / 500);

        if (skillValue >= 1000) {
            superRepairChance = "100.00%";
        }
        else {
            superRepairChance = percent.format(skillValue / 1000);
        }

        arcaneForgingRank = Repair.getArcaneForgingRank(profile);
    }

    @Override
    protected void permissionsCheck() {
        canSuperRepair = permInstance.repairBonus(player);
        canMasterRepair = permInstance.repairMastery(player);
        canArcaneForge = permInstance.arcaneForging(player);
        canRepairDiamond = permInstance.diamondRepair(player);
        canRepairGold = permInstance.goldRepair(player);
        canRepairIron = permInstance.ironRepair(player);
        canRepairStone = permInstance.stoneRepair(player);
        canRepairString = permInstance.stringRepair(player);
        canRepairLeather = permInstance.leatherRepair(player);
        canRepairWood = permInstance.woodRepair(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canArcaneForge || canRepairDiamond || canRepairGold || canRepairIron || canMasterRepair || canRepairStone || canSuperRepair || canRepairString || canRepairWood || canRepairLeather;
    }

    @Override
    protected void effectsDisplay() {
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
            player.sendMessage(LocaleLoader.getString("Repair.Skills.Super.Chance", new Object[] { superRepairChance }));
        }

        if (canArcaneForge) {
            player.sendMessage(LocaleLoader.getString("Repair.Arcane.Rank", new Object[] { arcaneForgingRank }));

            if (Config.getInstance().getArcaneForgingEnchantLossEnabled()) {
                player.sendMessage(LocaleLoader.getString("Repair.Arcane.Chance.Success", new Object[] { Repair.getEnchantChance(arcaneForgingRank) }));
            }

            if (Config.getInstance().getArcaneForgingDowngradeEnabled()) {
                player.sendMessage(LocaleLoader.getString("Repair.Arcane.Chance.Downgrade", new Object[] { Repair.getDowngradeChance(arcaneForgingRank) }));
            }
        }
    }
}
