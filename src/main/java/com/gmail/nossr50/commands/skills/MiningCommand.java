package com.gmail.nossr50.commands.skills;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;

public class MiningCommand extends SkillCommand {
    AdvancedConfig advancedConfig = AdvancedConfig.getInstance();
    private String doubleDropChance;
    private String superBreakerLength;
    private String blastMiningRank;
    private String blastRadiusIncrease;
    private String blastDamageDecrease;

    private int blastMiningRank1 = advancedConfig.getBlastMiningRank1();
    private int blastMiningRank2 = advancedConfig.getBlastMiningRank2();
    private int blastMiningRank3 = advancedConfig.getBlastMiningRank3();
    private int blastMiningRank4 = advancedConfig.getBlastMiningRank4();
    private int blastMiningRank5 = advancedConfig.getBlastMiningRank5();
    private int blastMiningRank6 = advancedConfig.getBlastMiningRank6();
    private int blastMiningRank7 = advancedConfig.getBlastMiningRank7();
    private int blastMiningRank8 = advancedConfig.getBlastMiningRank8();

    private double doubleDropsMaxBonus = advancedConfig.getMiningDoubleDropChance();
    private int doubleDropsMaxLevel = advancedConfig.getMiningDoubleDropMaxLevel();
    public int abilityLengthIncreaseLevel = advancedConfig.getAbilityLength();

    private boolean canSuperBreaker;
    private boolean canDoubleDrop;
    private boolean canBlast;
    private boolean canBiggerBombs;
    private boolean canDemoExpert;
    private boolean doubleDropsDisabled;

    public MiningCommand() {
        super(SkillType.MINING);
    }

    @Override
    protected void dataCalculations() {
        DecimalFormat df = new DecimalFormat("#.0");
        superBreakerLength = String.valueOf(2 + ((double) skillValue / (double) abilityLengthIncreaseLevel));
        if(skillValue >= doubleDropsMaxLevel) doubleDropChance = df.format(doubleDropsMaxBonus);
        else doubleDropChance = df.format(((double) doubleDropsMaxBonus / (double) doubleDropsMaxLevel) * (double) skillValue);
        
        if (skillValue >= blastMiningRank8) {
            blastMiningRank = "8";
            blastDamageDecrease = "100.00%";
            blastRadiusIncrease = "4";
        }
        else if (skillValue >= blastMiningRank7) {
            blastMiningRank = "7";
            blastDamageDecrease = "50.00%";
            blastRadiusIncrease = "3";
        }
        else if (skillValue >= blastMiningRank6) {
            blastMiningRank = "6";
            blastDamageDecrease = "50.00%";
            blastRadiusIncrease = "3";
        }
        else if (skillValue >= blastMiningRank5) {
            blastMiningRank = "5";
            blastDamageDecrease = "25.00%";
            blastRadiusIncrease = "2";
        }
        else if (skillValue >= blastMiningRank4) {
            blastMiningRank = "4";
            blastDamageDecrease = "25.00%";
            blastRadiusIncrease = "2";
        }
        else if (skillValue >= blastMiningRank3) {
            blastMiningRank = "3";
            blastDamageDecrease = "0.00%";
            blastRadiusIncrease = "1";
        }
        else if (skillValue >= blastMiningRank2) {
            blastMiningRank = "2";
            blastDamageDecrease = "0.00%";
            blastRadiusIncrease = "1";
        }
        else if (skillValue >= blastMiningRank1) {
            blastMiningRank = "1";
            blastDamageDecrease = "0.00%";
            blastRadiusIncrease = "0";
        }
        else {
            blastMiningRank = "0";
            blastDamageDecrease = "0.00%";
            blastRadiusIncrease = "0";
        }
    }

    @Override
    protected void permissionsCheck() {
        Config configInstance = Config.getInstance();

        canBiggerBombs = permInstance.biggerBombs(player);
        canBlast = permInstance.blastMining(player);
        canDemoExpert = permInstance.demolitionsExpertise(player);
        canDoubleDrop = permInstance.miningDoubleDrops(player);
        canSuperBreaker = permInstance.superBreaker(player);
        doubleDropsDisabled = configInstance.miningDoubleDropsDisabled();
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canBiggerBombs || canBlast || canDemoExpert || (canDoubleDrop && !doubleDropsDisabled) || canSuperBreaker;
    }

    @Override
    protected void effectsDisplay() {
        if (player.hasPermission("mcmmo.perks.lucky.mining")) {
            String perkPrefix = ChatColor.RED + "[mcMMO Perks] ";
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.lucky.name"), LocaleLoader.getString("Perks.lucky.desc", new Object[] { "Mining" }) }));
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
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canBiggerBombs || canBlast || canDemoExpert || (canDoubleDrop && !doubleDropsDisabled) || canSuperBreaker;
    }

    @Override
    protected void statsDisplay() {
        if (canDoubleDrop && !doubleDropsDisabled) {
            player.sendMessage(LocaleLoader.getString("Mining.Effect.DropChance", new Object[] { doubleDropChance }));
        }

        if (canSuperBreaker) {
            player.sendMessage(LocaleLoader.getString("Mining.Ability.Length", new Object[] { superBreakerLength }));
        }

        if (canBlast) {
            if (skillValue < blastMiningRank1) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Mining.Ability.Locked.0", new Object[] { blastMiningRank1 })  }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Blast.Rank", new Object[] { blastMiningRank, LocaleLoader.getString("Mining.Blast.Effect." + (Misc.getInt(blastMiningRank) - 1)) }));
            }
        }

        if (canBiggerBombs) {
            if (skillValue < blastMiningRank2) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Mining.Ability.Locked.1", new Object[] { blastMiningRank2 }) }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Blast.Radius.Increase", new Object[] { blastRadiusIncrease }));
            }
        }

        if (canDemoExpert) {
            if (skillValue < blastMiningRank4) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Mining.Ability.Locked.2", new Object[] { blastMiningRank4 }) }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Effect.Decrease", new Object[] { blastDamageDecrease }));
            }
        }
    }
}
