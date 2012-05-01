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
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Page;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class MiningCommand implements CommandExecutor {
    private float skillValue;
    private String doubleDropChance;
    private String superBreakerLength;
    private String blastMiningRank;
    private String blastRadiusIncrease;
    private String blastDamageDecrease;

    private boolean canSuperBreaker;
    private boolean canDoubleDrop;
    private boolean canBlast;
    private boolean canBiggerBombs;
    private boolean canDemoExpert;
    private boolean doubleDropsDisabled;

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
        permissionsCheck(player);

        player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Mining.SkillName") }));
        player.sendMessage(LocaleLoader.getString("Commands.XPGain", new Object[] { LocaleLoader.getString("Commands.XPGain.Mining") }));
        player.sendMessage(LocaleLoader.getString("Effects.Level", new Object[] { PP.getSkillLevel(SkillType.MINING), PP.getSkillXpLevel(SkillType.MINING), PP.getXpToLevel(SkillType.MINING) }));

        if (canBiggerBombs || canBlast || canDemoExpert || (canDoubleDrop && !doubleDropsDisabled ) || canSuperBreaker) {
            player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Effects.Effects") }));
        }

        if (canSuperBreaker) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Mining.Effect.0"), LocaleLoader.getString("Mining.Effect.1") }));
        }

        if (canDoubleDrop && !doubleDropsDisabled) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Mining.Effect.2"), LocaleLoader.getString("Mining.Effect.3") }));
        }

        if (canBlast) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Mining.Effect.4"), LocaleLoader.getString("Mining.Effect.5") }));
        }

        if (canBiggerBombs) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Mining.Effect.6"), LocaleLoader.getString("Mining.Effect.7") }));
        }

        if (canDemoExpert) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Mining.Effect.8"), LocaleLoader.getString("Mining.Effect.9") }));
        }

        if (canBiggerBombs || canBlast || canDemoExpert || (canDoubleDrop && !doubleDropsDisabled ) || canSuperBreaker) {
            player.sendMessage(LocaleLoader.getString("Skills.Header", new Object[] { LocaleLoader.getString("Commands.Stats.Self") }));
        }

        if (canDoubleDrop && !doubleDropsDisabled) {
            player.sendMessage(LocaleLoader.getString("Mining.Effect.DropChance", new Object[] { doubleDropChance }));
        }

        if (canSuperBreaker) {
            player.sendMessage(LocaleLoader.getString("Mining.Ability.Length", new Object[] { superBreakerLength }));
        }

        if (canBlast) {
            if (PP.getSkillLevel(SkillType.MINING) < 125) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Mining.Ability.Locked.0") }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Blast.Rank", new Object[] { blastMiningRank, LocaleLoader.getString("Mining.Blast.Effect." + (Misc.getInt(blastMiningRank) - 1)) }));
            }
        }

        if (canBiggerBombs) {
            if (PP.getSkillLevel(SkillType.MINING) < 250) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Mining.Ability.Locked.1") }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Blast.Radius.Increase", new Object[] { blastRadiusIncrease }));
            }
        }

        if (canDemoExpert) {
            if (PP.getSkillLevel(SkillType.MINING) < 500) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Mining.Ability.Locked.2") }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Effect.Decrease", new Object[] { blastDamageDecrease }));
            }
        }

        Page.grabGuidePageForSkill(SkillType.MINING, player, args);

        return true;
    }

    private void dataCalculations(float skillValue) {
        DecimalFormat percent = new DecimalFormat("##0.00%");

        superBreakerLength = String.valueOf(2 + ((int) skillValue / 50));

        if (skillValue >= 1000) {
            blastMiningRank = "8";
            blastDamageDecrease = "100.00%";
            blastRadiusIncrease = "4";
            doubleDropChance = "100.00%";
        }
        else if (skillValue >= 875) {
            blastMiningRank = "7";
            blastDamageDecrease = "50.00%";
            blastRadiusIncrease = "3";
            doubleDropChance = percent.format(skillValue / 1000);
        }
        else if (skillValue >= 750) {
            blastMiningRank = "6";
            blastDamageDecrease = "50.00%";
            blastRadiusIncrease = "3";
            doubleDropChance = percent.format(skillValue / 1000);
        }
        else if (skillValue >= 625) {
            blastMiningRank = "5";
            blastDamageDecrease = "25.00%";
            blastRadiusIncrease = "2";
            doubleDropChance = percent.format(skillValue / 1000);
        }
        else if (skillValue >= 500) {
            blastMiningRank = "4";
            blastDamageDecrease = "25.00%";
            blastRadiusIncrease = "2";
            doubleDropChance = percent.format(skillValue / 1000);
        }
        else if (skillValue >= 375) {
            blastMiningRank = "3";
            blastDamageDecrease = "0.00%";
            blastRadiusIncrease = "1";
            doubleDropChance = percent.format(skillValue / 1000);
        }
        else if (skillValue >= 250) {
            blastMiningRank = "2";
            blastDamageDecrease = "0.00%";
            blastRadiusIncrease = "1";
            doubleDropChance = percent.format(skillValue / 1000);
        }
        else if (skillValue >= 125) {
            blastMiningRank = "1";
            blastDamageDecrease = "0.00%";
            blastRadiusIncrease = "0";
            doubleDropChance = percent.format(skillValue / 1000);
        }
        else {
            blastMiningRank = "0";
            blastDamageDecrease = "0.00%";
            blastRadiusIncrease = "0";
            doubleDropChance = percent.format(skillValue / 1000);
        }
    }

    private void permissionsCheck(Player player) {
        Permissions permInstance = Permissions.getInstance();
        Config configInstance = Config.getInstance();

        canBiggerBombs = permInstance.biggerBombs(player);
        canBlast = permInstance.blastMining(player);
        canDemoExpert = permInstance.demolitionsExpertise(player);
        canDoubleDrop = permInstance.miningDoubleDrops(player);
        canSuperBreaker = permInstance.superBreaker(player);
        doubleDropsDisabled = configInstance.miningDoubleDropsDisabled();
    }
}
