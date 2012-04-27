package com.gmail.nossr50.commands.skills;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.Repair;
import com.gmail.nossr50.util.Page;
import com.gmail.nossr50.util.Users;

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

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Repair.SkillName") }));
        player.sendMessage(LocaleLoader.getString("Commands.XPGain", new Object[] { LocaleLoader.getString("Commands.XPGain.Repair") }));
        player.sendMessage(LocaleLoader.getString("Effects.Level", new Object[] { PP.getSkillLevel(SkillType.REPAIR), PP.getSkillXpLevel(SkillType.REPAIR), PP.getXpToLevel(SkillType.REPAIR) }));

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Effects.Effects") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Repair.Effect.0"), LocaleLoader.getString("Repair.Effect.1") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Repair.Effect.2"), LocaleLoader.getString("Repair.Effect.3") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Repair.Effect.4"), LocaleLoader.getString("Repair.Effect.5") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Repair.Effect.6", new Object[] { Config.getInstance().getRepairDiamondLevelRequirement() }), LocaleLoader.getString("Repair.Effect.7") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Repair.Effect.8"), LocaleLoader.getString("Repair.Effect.9") }));

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Commands.Stats.Self") }));
        player.sendMessage(LocaleLoader.getString("Repair.Skills.Mastery", new Object[] { repairMasteryBonus }));
        player.sendMessage(LocaleLoader.getString("Repair.Skills.Super.Chance", new Object[] { superRepairChance }));
        player.sendMessage(LocaleLoader.getString("Repair.Arcane.Rank", new Object[] { arcaneForgingRank }));
        player.sendMessage(LocaleLoader.getString("Repair.Arcane.Chance.Success", new Object[] { Repair.getEnchantChance(arcaneForgingRank) }));
        player.sendMessage(LocaleLoader.getString("Repair.Arcane.Chance.Downgrade", new Object[] { Repair.getDowngradeChance(arcaneForgingRank) }));

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
