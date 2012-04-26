package com.gmail.nossr50.commands.skills;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.skills.Repair;
import com.gmail.nossr50.util.Page;

public class RepairCommand implements CommandExecutor {
    private float skillValue;
    private String repairMasteryBonus;
    private String superRepairChance;

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

        int arcaneForgingRank = Repair.getArcaneForgingRank(PP);

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Repair.SkillName") }));
        player.sendMessage(mcLocale.getString("Commands.XPGain", new Object[] { mcLocale.getString("Commands.XPGain.Repair") }));
        player.sendMessage(mcLocale.getString("Effects.Level", new Object[] { PP.getSkillLevel(SkillType.REPAIR), PP.getSkillXpLevel(SkillType.REPAIR), PP.getXpToLevel(SkillType.REPAIR) }));

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Effects.Effects") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Repair.Effect.0"), mcLocale.getString("Repair.Effect.1") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Repair.Effect.2"), mcLocale.getString("Repair.Effect.3") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Repair.Effect.4"), mcLocale.getString("Repair.Effect.5") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Repair.Effect.6", new Object[] { Config.getRepairDiamondLevelRequirement() }), mcLocale.getString("Repair.Effect.7") }));
        player.sendMessage(mcLocale.getString("Effects.Template", new Object[] { mcLocale.getString("Repair.Effect.8"), mcLocale.getString("Repair.Effect.9") }));

        player.sendMessage(mcLocale.getString("Skills.Header", new Object[] { mcLocale.getString("Commands.Stats.Self") }));
        player.sendMessage(mcLocale.getString("Repair.Skills.Mastery", new Object[] { repairMasteryBonus }));
        player.sendMessage(mcLocale.getString("Repair.Skills.Super.Chance", new Object[] { superRepairChance }));
        player.sendMessage(mcLocale.getString("Repair.Arcane.Rank", new Object[] { arcaneForgingRank }));
        player.sendMessage(mcLocale.getString("Repair.Arcane.Chance.Success", new Object[] { Repair.getEnchantChance(arcaneForgingRank) }));
        player.sendMessage(mcLocale.getString("Repair.Arcane.Chance.Downgrade", new Object[] { Repair.getDowngradeChance(arcaneForgingRank) }));

        Page.grabGuidePageForSkill(SkillType.REPAIR, player, args);

        return true;
    }

    private void dataCalculations(float skillValue) {
        repairMasteryBonus = String.valueOf(skillValue / 5);

        if (skillValue >= 1000) {
            superRepairChance = "100";
        }
        else {
            superRepairChance = String.valueOf(skillValue / 10);
        }
    }
}
