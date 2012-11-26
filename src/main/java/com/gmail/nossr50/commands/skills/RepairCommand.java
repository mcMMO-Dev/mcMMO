package com.gmail.nossr50.commands.skills;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.repair.Repairable;

public class RepairCommand extends SkillCommand {
    AdvancedConfig advancedConfig = AdvancedConfig.getInstance();
    private int arcaneForgingRank;
    private String repairMasteryBonus;
    private String superRepairChance;

    private float repairMasteryChanceMax = advancedConfig.getRepairMasteryChanceMax();
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
        DecimalFormat df = new DecimalFormat("#.0");
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

        if(skillValue >= repairMasteryMaxBonusLevel) repairMasteryBonus = df.format(repairMasteryChanceMax);
            else repairMasteryBonus = df.format(((double) repairMasteryChanceMax / (double) repairMasteryMaxBonusLevel) * (double) skillValue);

        if(skillValue >= superRepairMaxBonusLevel) superRepairChance = df.format(superRepairChanceMax);
            else superRepairChance = df.format(((double) superRepairChanceMax / (double) superRepairMaxBonusLevel) * (double) skillValue);

        arcaneForgingRank = Repair.getArcaneForgingRank(profile);
    }

    @Override
    protected void permissionsCheck() {
        canSuperRepair = permInstance.repairBonus(player);
        canMasterRepair = permInstance.repairMastery(player);
        canArcaneForge = permInstance.arcaneForging(player);
        canSalvage = permInstance.salvage(player);
        canRepairDiamond = permInstance.diamondRepair(player);
        canRepairGold = permInstance.goldRepair(player);
        canRepairIron = permInstance.ironRepair(player);
        canRepairStone = permInstance.stoneRepair(player);
        canRepairString = permInstance.stringRepair(player);
        canRepairLeather = permInstance.leatherRepair(player);
        canRepairWood = permInstance.woodRepair(player);
	arcaneBypass = permInstance.arcaneBypass(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canArcaneForge || canSalvage || canRepairDiamond || canRepairGold || canRepairIron || canMasterRepair || canRepairStone || canSuperRepair || canRepairString || canRepairWood || canRepairLeather;
    }

    @Override
    protected void effectsDisplay() {
        if (player.hasPermission("mcmmo.perks.lucky.repair")) {
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
            player.sendMessage(LocaleLoader.getString("Repair.Skills.Super.Chance", new Object[] { superRepairChance }));
        }

        if (canArcaneForge) {
            player.sendMessage(LocaleLoader.getString("Repair.Arcane.Rank", new Object[] { arcaneForgingRank }));

            if (Config.getInstance().getArcaneForgingEnchantLossEnabled()) {
                player.sendMessage(LocaleLoader.getString("Repair.Arcane.Chance.Success", new Object[] { (arcaneBypass ? 100 : Repair.getEnchantChance(arcaneForgingRank)) }));
            }

            if (Config.getInstance().getArcaneForgingDowngradeEnabled()) {
                player.sendMessage(LocaleLoader.getString("Repair.Arcane.Chance.Downgrade", new Object[] { (arcaneBypass ? 0 : Repair.getDowngradeChance(arcaneForgingRank)) }));
            }
        }
    }
}
