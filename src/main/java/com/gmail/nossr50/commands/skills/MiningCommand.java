package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.mining.BlastMining;
import com.gmail.nossr50.skills.mining.Mining;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;

public class MiningCommand extends SkillCommand {
    private String doubleDropChance;
    private String doubleDropChanceLucky;
    private String superBreakerLength;
    private String superBreakerLengthEndurance;
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
        //SUPER BREAKER
        String[] superBreakerStrings = calculateLengthDisplayValues();
        superBreakerLength = superBreakerStrings[0];
        superBreakerLengthEndurance = superBreakerStrings[1];

        //DOUBLE DROPS
        String[] doubleDropStrings = calculateAbilityDisplayValues(Mining.doubleDropsMaxLevel, Mining.doubleDropsMaxChance);
        doubleDropChance = doubleDropStrings[0];
        doubleDropChanceLucky = doubleDropStrings[1];

        //BLAST MINING
        if (skillValue >= BlastMining.rank8) {
            blastMiningRank = "8";
            blastDamageDecrease = "100.00%";
            blastRadiusIncrease = "4";
        }
        else if (skillValue >= BlastMining.rank7) {
            blastMiningRank = "7";
            blastDamageDecrease = "50.00%";
            blastRadiusIncrease = "3";
        }
        else if (skillValue >= BlastMining.rank6) {
            blastMiningRank = "6";
            blastDamageDecrease = "50.00%";
            blastRadiusIncrease = "3";
        }
        else if (skillValue >= BlastMining.rank5) {
            blastMiningRank = "5";
            blastDamageDecrease = "25.00%";
            blastRadiusIncrease = "2";
        }
        else if (skillValue >= BlastMining.rank4) {
            blastMiningRank = "4";
            blastDamageDecrease = "25.00%";
            blastRadiusIncrease = "2";
        }
        else if (skillValue >= BlastMining.rank3) {
            blastMiningRank = "3";
            blastDamageDecrease = "0.00%";
            blastRadiusIncrease = "1";
        }
        else if (skillValue >= BlastMining.rank2) {
            blastMiningRank = "2";
            blastDamageDecrease = "0.00%";
            blastRadiusIncrease = "1";
        }
        else if (skillValue >= BlastMining.rank1) {
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
        canBiggerBombs = Permissions.biggerBombs(player);
        canBlast = Permissions.blastMining(player);
        canDemoExpert = Permissions.demolitionsExpertise(player);
        canDoubleDrop = Permissions.miningDoubleDrops(player);
        canSuperBreaker = Permissions.superBreaker(player);
        doubleDropsDisabled = Mining.doubleDropsDisabled;
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canBiggerBombs || canBlast || canDemoExpert || (canDoubleDrop && !doubleDropsDisabled) || canSuperBreaker;
    }

    @Override
    protected void effectsDisplay() {
        luckyEffectsDisplay();

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
            if (isLucky) {
                player.sendMessage(LocaleLoader.getString("Mining.Effect.DropChance", new Object[] { doubleDropChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { doubleDropChanceLucky }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Effect.DropChance", new Object[] { doubleDropChance }));
            }
        }

        if (canSuperBreaker) {
            if (hasEndurance) {
                player.sendMessage(LocaleLoader.getString("Mining.Ability.Length", new Object[] { superBreakerLength }) + LocaleLoader.getString("Perks.activationtime.bonus", new Object[] { superBreakerLengthEndurance }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Ability.Length", new Object[] { superBreakerLength }));
            }
        }

        if (canBlast) {
            if (skillValue < BlastMining.rank1) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Mining.Ability.Locked.0", new Object[] { BlastMining.rank1 })  }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Blast.Rank", new Object[] { blastMiningRank, LocaleLoader.getString("Mining.Blast.Effect." + (Misc.getInt(blastMiningRank) - 1)) }));
            }
        }

        if (canBiggerBombs) {
            if (skillValue < BlastMining.rank2) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Mining.Ability.Locked.1", new Object[] { BlastMining.rank2 }) }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Blast.Radius.Increase", new Object[] { blastRadiusIncrease }));
            }
        }

        if (canDemoExpert) {
            if (skillValue < BlastMining.rank4) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Mining.Ability.Locked.2", new Object[] { BlastMining.rank4 }) }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Effect.Decrease", new Object[] { blastDamageDecrease }));
            }
        }
    }
}
