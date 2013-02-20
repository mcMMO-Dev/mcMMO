package com.gmail.nossr50.skills.mining;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillCommand;
import com.gmail.nossr50.skills.utilities.SkillType;
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
        canBlast = Permissions.remoteDetonation(player);
        canDemoExpert = Permissions.demolitionsExpertise(player);
        canDoubleDrop = Permissions.doubleDrops(player, skill);
        canSuperBreaker = Permissions.superBreaker(player);
        doubleDropsDisabled = skill.getDoubleDropsDisabled();
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canBiggerBombs || canBlast || canDemoExpert || (canDoubleDrop && !doubleDropsDisabled) || canSuperBreaker;
    }

    @Override
    protected void effectsDisplay() {
        luckyEffectsDisplay();

        if (canSuperBreaker) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Mining.Effect.0"), LocaleLoader.getString("Mining.Effect.1")));
        }

        if (canDoubleDrop && !doubleDropsDisabled) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Mining.Effect.2"), LocaleLoader.getString("Mining.Effect.3")));
        }

        if (canBlast) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Mining.Effect.4"), LocaleLoader.getString("Mining.Effect.5")));
        }

        if (canBiggerBombs) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Mining.Effect.6"), LocaleLoader.getString("Mining.Effect.7")));
        }

        if (canDemoExpert) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Mining.Effect.8"), LocaleLoader.getString("Mining.Effect.9")));
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
                player.sendMessage(LocaleLoader.getString("Mining.Effect.DropChance", doubleDropChance) + LocaleLoader.getString("Perks.lucky.bonus", doubleDropChanceLucky));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Effect.DropChance", doubleDropChance));
            }
        }

        if (canSuperBreaker) {
            if (hasEndurance) {
                player.sendMessage(LocaleLoader.getString("Mining.Ability.Length", superBreakerLength) + LocaleLoader.getString("Perks.activationtime.bonus", superBreakerLengthEndurance));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Ability.Length", superBreakerLength));
            }
        }

        if (canBlast) {
            if (skillValue < BlastMining.rank1) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Mining.Ability.Locked.0", BlastMining.rank1)));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Blast.Rank", blastMiningRank, LocaleLoader.getString("Mining.Blast.Effect." + (Integer.parseInt(blastMiningRank) - 1))));
            }
        }

        if (canBiggerBombs) {
            if (skillValue < BlastMining.rank2) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Mining.Ability.Locked.1", BlastMining.rank2)));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Blast.Radius.Increase", blastRadiusIncrease));
            }
        }

        if (canDemoExpert) {
            if (skillValue < BlastMining.rank4) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Mining.Ability.Locked.2", BlastMining.rank4)));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Effect.Decrease", blastDamageDecrease));
            }
        }
    }
}
