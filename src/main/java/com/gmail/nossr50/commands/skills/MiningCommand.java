package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.mining.Mining;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.util.Permissions;

public class MiningCommand extends SkillCommand {
    private String doubleDropChance;
    private String doubleDropChanceLucky;
    private String superBreakerLength;
    private String superBreakerLengthEndurance;

    private int blastMiningRank;
    private int bonusTNTDrops;
    private double blastRadiusIncrease;
    private String oreBonus;
    private String debrisReduction;
    private String blastDamageDecrease;

    private boolean canSuperBreaker;
    private boolean canDoubleDrop;
    private boolean canBlast;
    private boolean canBiggerBombs;
    private boolean canDemoExpert;

    public MiningCommand() {
        super(SkillType.MINING);
    }

    @Override
    protected void dataCalculations() {
        // SUPER BREAKER
        if (canSuperBreaker) {
            String[] superBreakerStrings = calculateLengthDisplayValues();
            superBreakerLength = superBreakerStrings[0];
            superBreakerLengthEndurance = superBreakerStrings[1];
        }

        // DOUBLE DROPS
        if (canDoubleDrop) {
            String[] doubleDropStrings = calculateAbilityDisplayValues(Mining.doubleDropsMaxLevel, Mining.doubleDropsMaxChance);
            doubleDropChance = doubleDropStrings[0];
            doubleDropChanceLucky = doubleDropStrings[1];
        }

        // BLAST MINING
        if (canBlast || canDemoExpert || canBiggerBombs) {
            MiningManager miningManager = mcMMOPlayer.getMiningManager();

            blastMiningRank = miningManager.getBlastMiningTier();
            bonusTNTDrops = miningManager.getDropMultiplier();
            oreBonus = percent.format(miningManager.getOreBonus() / 30.0D); // Base received in TNT is 30%
            debrisReduction = percent.format(miningManager.getDebrisReduction() / 30.0D); // Base received in TNT is 30%
            blastDamageDecrease = percent.format(miningManager.getBlastDamageModifier() / 100.0D);
            blastRadiusIncrease = miningManager.getBlastRadiusModifier();
        }
    }

    @Override
    protected void permissionsCheck() {
        canBiggerBombs = Permissions.biggerBombs(player);
        canBlast = Permissions.remoteDetonation(player);
        canDemoExpert = Permissions.demolitionsExpertise(player);
        canDoubleDrop = Permissions.doubleDrops(player, skill) && !skill.getDoubleDropsDisabled();
        canSuperBreaker = Permissions.superBreaker(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canBiggerBombs || canBlast || canDemoExpert || canDoubleDrop || canSuperBreaker;
    }

    @Override
    protected void effectsDisplay() {
        luckyEffectsDisplay();

        if (canSuperBreaker) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Mining.Effect.0"), LocaleLoader.getString("Mining.Effect.1")));
        }

        if (canDoubleDrop) {
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
        return canBiggerBombs || canBlast || canDemoExpert || canDoubleDrop || canSuperBreaker;
    }

    @Override
    protected void statsDisplay() {
        if (canDoubleDrop) {
            player.sendMessage(LocaleLoader.getString("Mining.Effect.DropChance", doubleDropChance) + (isLucky ? LocaleLoader.getString("Perks.lucky.bonus", doubleDropChanceLucky) : ""));
        }

        if (canSuperBreaker) {
            player.sendMessage(LocaleLoader.getString("Mining.Ability.Length", superBreakerLength) + (hasEndurance ? LocaleLoader.getString("Perks.activationtime.bonus", superBreakerLengthEndurance) : ""));
        }

        if (canBlast) {
            int unlockLevel = AdvancedConfig.getInstance().getBlastMiningRank1();

            if (skillValue < unlockLevel) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Mining.Ability.Locked.0", unlockLevel)));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Blast.Rank", blastMiningRank, LocaleLoader.getString("Mining.Blast.Effect", oreBonus, debrisReduction, bonusTNTDrops)));
            }
        }

        if (canBiggerBombs) {
            int unlockLevel = AdvancedConfig.getInstance().getBlastMiningRank2();

            if (skillValue < unlockLevel) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Mining.Ability.Locked.1", unlockLevel)));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Blast.Radius.Increase", blastRadiusIncrease));
            }
        }

        if (canDemoExpert) {
            int unlockLevel = AdvancedConfig.getInstance().getBlastMiningRank4();

            if (skillValue < unlockLevel) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Mining.Ability.Locked.2", unlockLevel)));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Effect.Decrease", blastDamageDecrease));
            }
        }
    }
}
