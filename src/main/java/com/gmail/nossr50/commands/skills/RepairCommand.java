package com.gmail.nossr50.commands.skills;

import org.bukkit.ChatColor;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.repair.Repairable;
import com.gmail.nossr50.util.Permissions;

public class RepairCommand extends SkillCommand {
    AdvancedConfig advancedConfig = AdvancedConfig.getInstance();
    private int arcaneForgingRank;
    private String repairMasteryBonus;
    private String superRepairChance;
    private String superRepairChanceLucky;

    private float repairMasteryMaxBonus = advancedConfig.getRepairMasteryMaxBonus();
    private float repairMasteryMaxBonusLevel = advancedConfig.getRepairMasteryMaxLevel();
    private float superRepairChanceMax = advancedConfig.getSuperRepairChanceMax();
    private float superRepairMaxBonusLevel = advancedConfig.getSuperRepairMaxLevel();

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
    private boolean lucky;

    private int salvageLevel;
    private int diamondLevel;
    private int goldLevel;
    private int ironLevel;
    private int stoneLevel;

    public RepairCommand() {
        super(SkillType.REPAIR);
    }

    @Override
    protected void dataCalculations() {
        float superRepairChanceF;
        // We're using pickaxes here, not the best but it works
        Repairable diamondRepairable = mcMMO.repairManager.getRepairable(278);
        Repairable goldRepairable = mcMMO.repairManager.getRepairable(285);
        Repairable ironRepairable = mcMMO.repairManager.getRepairable(257);
        Repairable stoneRepairable = mcMMO.repairManager.getRepairable(274);

        diamondLevel = (diamondRepairable == null) ? 0 : diamondRepairable.getMinimumLevel();
        goldLevel = (goldRepairable == null) ? 0 : goldRepairable.getMinimumLevel();
        ironLevel = (ironRepairable == null) ? 0 : ironRepairable.getMinimumLevel();
        stoneLevel = (stoneRepairable == null) ? 0 : stoneRepairable.getMinimumLevel();

        salvageLevel = Config.getInstance().getSalvageUnlockLevel();

        if (skillValue >= repairMasteryMaxBonusLevel) repairMasteryBonus = percent.format(repairMasteryMaxBonus / 100D);
        else repairMasteryBonus = percent.format((((double) repairMasteryMaxBonus / (double) repairMasteryMaxBonusLevel) * skillValue) / 100D);

        if (skillValue >= superRepairMaxBonusLevel) superRepairChanceF = superRepairChanceMax;
        else superRepairChanceF = (float) (((double) superRepairChanceMax / (double) superRepairMaxBonusLevel) * skillValue);
        superRepairChance = percent.format(superRepairChanceF / 100D);
        if (superRepairChanceF * 1.3333D >= 100D) superRepairChanceLucky = percent.format(1D);
        else superRepairChanceLucky = percent.format((superRepairChanceF * 1.3333D) / 100D);

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
        lucky = Permissions.luckyRepair(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canArcaneForge || canSalvage || canRepairDiamond || canRepairGold || canRepairIron || canMasterRepair || canRepairStone || canSuperRepair || canRepairString || canRepairWood || canRepairLeather;
    }

    @Override
    protected void effectsDisplay() {
        if (lucky) {
            String perkPrefix = ChatColor.RED + "[mcMMO Perks] ";
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.lucky.name"), LocaleLoader.getString("Perks.lucky.desc", new Object[] { "Repair" }) }));
        }

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

        if (canSalvage && salvageLevel > 0) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Repair.Effect.16", new Object[] { salvageLevel }), LocaleLoader.getString("Repair.Effect.17") }));
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
            if (lucky)
                player.sendMessage(LocaleLoader.getString("Repair.Skills.Super.Chance", new Object[] { superRepairChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { superRepairChanceLucky }));
            else
                player.sendMessage(LocaleLoader.getString("Repair.Skills.Super.Chance", new Object[] { superRepairChance }));
        }

        if (canArcaneForge) {
            player.sendMessage(LocaleLoader.getString("Repair.Arcane.Rank", new Object[] { arcaneForgingRank }));

            if (advancedConfig.getArcaneForgingEnchantLossEnabled()) {
                player.sendMessage(LocaleLoader.getString("Repair.Arcane.Chance.Success", new Object[] { (arcaneBypass ? 100 : Repair.getEnchantChance(arcaneForgingRank)) }));
            }

            if (advancedConfig.getArcaneForgingDowngradeEnabled()) {
                player.sendMessage(LocaleLoader.getString("Repair.Arcane.Chance.Downgrade", new Object[] { (arcaneBypass ? 0 : Repair.getDowngradeChance(arcaneForgingRank)) }));
            }
        }
    }
}
