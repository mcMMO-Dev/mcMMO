package com.gmail.nossr50.commands.skills;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.commands.CommandHelper;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Page;
import com.gmail.nossr50.util.Users;

public class MiningCommand implements CommandExecutor {
    private float skillValue;
    private String doubleDropChance;
    private String superBreakerLength;
    private int blastMiningRank;
    private int blastRadiusIncrease;
    private int blastDamageDecrease;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandHelper.noConsoleUsage(sender)) {
            return true;
        }

        if (CommandHelper.noCommandPermissions(sender, "mcmmo.skills.mining")) {
            return true;
        }

        Player player = (Player) sender;
        PlayerProfile PP = Users.getProfile(player);

        skillValue = (float) PP.getSkillLevel(SkillType.MINING);
        dataCalculations(skillValue);

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Mining.SkillName") }));
        player.sendMessage(LocaleLoader.getString("Commands.XPGain", new Object[] { LocaleLoader.getString("Commands.XPGain.Mining") }));
        player.sendMessage(LocaleLoader.getString("Effects.Level", new Object[] { PP.getSkillLevel(SkillType.MINING), PP.getSkillXpLevel(SkillType.MINING), PP.getXpToLevel(SkillType.MINING) }));

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Effects.Effects") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Mining.Effect.0"), LocaleLoader.getString("Mining.Effect.1") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Mining.Effect.2"), LocaleLoader.getString("Mining.Effect.3") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Mining.Effect.4"), LocaleLoader.getString("Mining.Effect.5") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Mining.Effect.6"), LocaleLoader.getString("Mining.Effect.7") }));
        player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Mining.Effect.8"), LocaleLoader.getString("Mining.Effect.9") }));

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Commands.Stats.Self") }));
        player.sendMessage(LocaleLoader.getString("Mining.Effect.DropChance", new Object[] { doubleDropChance }));
        player.sendMessage(LocaleLoader.getString("Mining.Ability.Length", new Object[] { superBreakerLength }));

        if (PP.getSkillLevel(SkillType.MINING) < 125) {
            player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Mining.Ability.Locked.0") }));
        }
        else {
            player.sendMessage(LocaleLoader.getString("Mining.Blast.Rank", new Object[] { blastMiningRank, LocaleLoader.getString("Mining.Blast.Effect." + (blastMiningRank - 1)) }));
        }

        if (PP.getSkillLevel(SkillType.MINING) < 250) {
            player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Mining.Ability.Locked.1") }));
        }
        else {
            player.sendMessage(LocaleLoader.getString("m.MiningBiggerBombs", new Object[] { blastRadiusIncrease }));
        }

        if (PP.getSkillLevel(SkillType.MINING) < 500) {
            player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Mining.Ability.Locked.2") }));
        }
        else {
            player.sendMessage(LocaleLoader.getString("m.MiningDemolitionsExpertDamageDecrease", new Object[] { blastDamageDecrease }));
        }

        Page.grabGuidePageForSkill(SkillType.MINING, player, args);

        return true;
    }

    private void dataCalculations(float skillValue) {
        superBreakerLength = String.valueOf(2 + ((int) skillValue / 50));

        if (skillValue >= 1000) {
            blastMiningRank = 8;
            blastDamageDecrease = 100;
            blastRadiusIncrease = 4;
            doubleDropChance = "100";
        }
        else if (skillValue >= 875) {
            blastMiningRank = 7;
            blastDamageDecrease = 50;
            blastRadiusIncrease = 3;
            doubleDropChance = String.valueOf(skillValue / 10);
        }
        else if (skillValue >= 750) {
            blastMiningRank = 6;
            blastDamageDecrease = 50;
            blastRadiusIncrease = 3;
            doubleDropChance = String.valueOf(skillValue / 10);
        }
        else if (skillValue >= 625) {
            blastMiningRank = 5;
            blastDamageDecrease = 25;
            blastRadiusIncrease = 2;
            doubleDropChance = String.valueOf(skillValue / 10);
        }
        else if (skillValue >= 500) {
            blastMiningRank = 4;
            blastDamageDecrease = 25;
            blastRadiusIncrease = 2;
            doubleDropChance = String.valueOf(skillValue / 10);
        }
        else if (skillValue >= 375) {
            blastMiningRank = 3;
            blastDamageDecrease = 0;
            blastRadiusIncrease = 1;
            doubleDropChance = String.valueOf(skillValue / 10);
        }
        else if (skillValue >= 250) {
            blastMiningRank = 2;
            blastDamageDecrease = 0;
            blastRadiusIncrease = 1;
            doubleDropChance = String.valueOf(skillValue / 10);
        }
        else if (skillValue >= 125) {
            blastMiningRank = 1;
            blastDamageDecrease = 0;
            blastRadiusIncrease = 0;
            doubleDropChance = String.valueOf(skillValue / 10);
        }
        else {
            blastMiningRank = 0;
            blastDamageDecrease = 0;
            blastRadiusIncrease = 0;
            doubleDropChance = String.valueOf(skillValue / 10);
        }
    }
}
