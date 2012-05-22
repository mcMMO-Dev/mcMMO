package com.gmail.nossr50.commands.skills;

import java.text.DecimalFormat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.util.Page;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class RepairCommand implements CommandExecutor {
    private float skillValue;
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.skills.repair")) {
            return true;
        }

        Player player = (Player) sender;
        PlayerProfile PP = Users.getProfile(player);

        skillValue = (float) PP.getSkillLevel(SkillType.REPAIR);
        dataCalculations(skillValue);
        permissionsCheck(player);

        int arcaneForgingRank = Repair.getArcaneForgingRank(PP);

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Repair.SkillName") }));
        player.sendMessage(LocaleLoader.getString("Commands.XPGain", new Object[] { LocaleLoader.getString("Commands.XPGain.Repair") }));
        player.sendMessage(LocaleLoader.getString("Effects.Level", new Object[] { PP.getSkillLevel(SkillType.REPAIR), PP.getSkillXpLevel(SkillType.REPAIR), PP.getXpToLevel(SkillType.REPAIR) }));

        if (canArcaneForge || canRepairDiamond || canRepairGold || canRepairIron || canMasterRepair || canRepairStone || canSuperRepair || canRepairString || canRepairWood || canRepairLeather) {
            player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Effects.Effects") }));
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

        if (canArcaneForge) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Repair.Effect.8"), LocaleLoader.getString("Repair.Effect.9") }));
        }

        if (canArcaneForge || canMasterRepair || canSuperRepair) {
            player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Commands.Stats.Self") }));
        }

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

        Page.grabGuidePageForSkill(SkillType.REPAIR, player, args);

        return true;
    }

    private void dataCalculations(float skillValue) {
        DecimalFormat percent = new DecimalFormat("##0.00%");
        Config configInstance = Config.getInstance();

        diamondLevel = configInstance.getRepairDiamondLevelRequirement();
        goldLevel = configInstance.getRepairGoldLevelRequirement();
        ironLevel = configInstance.getRepairIronLevelRequirement();
        stoneLevel = configInstance.getRepairStoneLevelRequirement();

        repairMasteryBonus = percent.format(skillValue / 500);

        if (skillValue >= 1000) {
            superRepairChance = "100.00%";
        }
        else {
            superRepairChance = percent.format(skillValue / 1000);
        }
    }

    private void permissionsCheck(Player player) {
        Permissions permInstance = Permissions.getInstance();

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
}
