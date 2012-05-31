package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;

public class MiningCommand extends SkillCommand {
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

    public MiningCommand() {
        super(SkillType.MINING);
    }

    @Override
    protected void dataCalculations() {
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
            if (skillValue < 125) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Mining.Ability.Locked.0") }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Blast.Rank", new Object[] { blastMiningRank, LocaleLoader.getString("Mining.Blast.Effect." + (Misc.getInt(blastMiningRank) - 1)) }));
            }
        }

        if (canBiggerBombs) {
            if (skillValue < 250) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Mining.Ability.Locked.1") }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Blast.Radius.Increase", new Object[] { blastRadiusIncrease }));
            }
        }

        if (canDemoExpert) {
            if (skillValue < 500) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Mining.Ability.Locked.2") }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Effect.Decrease", new Object[] { blastDamageDecrease }));
            }
        }
    }
}
