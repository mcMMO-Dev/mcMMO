package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.mining.Mining;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;

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
    private boolean doubleDropsDisabled;

    public MiningCommand() {
        super(SkillType.MINING);
    }

    @Override
    protected void dataCalculations() {
        // SUPER BREAKER
        String[] superBreakerStrings = calculateLengthDisplayValues();
        superBreakerLength = superBreakerStrings[0];
        superBreakerLengthEndurance = superBreakerStrings[1];

        // DOUBLE DROPS
        String[] doubleDropStrings = calculateAbilityDisplayValues(Mining.doubleDropsMaxLevel, Mining.doubleDropsMaxChance);
        doubleDropChance = doubleDropStrings[0];
        doubleDropChanceLucky = doubleDropStrings[1];

        // BLAST MINING
        MiningManager miningManager = UserManager.getPlayer(player).getMiningManager();
        blastMiningRank = miningManager.getBlastMiningTier();
        bonusTNTDrops = miningManager.getDropMultiplier();
        oreBonus = percent.format(miningManager.getOreBonus() / 30.0D); // Base received in TNT is 30%
        debrisReduction = percent.format(miningManager.getDebrisReduction() / 30.0D); // Base received in TNT is 30%
        blastDamageDecrease = percent.format(miningManager.getBlastDamageModifier() / 100.0D);
        blastRadiusIncrease = miningManager.getBlastRadiusModifier();
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
            if (skillValue < AdvancedConfig.getInstance().getBlastMiningRank1()) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Mining.Ability.Locked.0", AdvancedConfig.getInstance().getBlastMiningRank1())));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Blast.Rank", blastMiningRank, LocaleLoader.getString("Mining.Blast.Effect", oreBonus, debrisReduction, bonusTNTDrops)));
            }
        }

        if (canBiggerBombs) {
            if (skillValue < AdvancedConfig.getInstance().getBlastMiningRank2()) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Mining.Ability.Locked.1", AdvancedConfig.getInstance().getBlastMiningRank2())));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Blast.Radius.Increase", blastRadiusIncrease));
            }
        }

        if (canDemoExpert) {
            if (skillValue < AdvancedConfig.getInstance().getBlastMiningRank4()) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Mining.Ability.Locked.2", AdvancedConfig.getInstance().getBlastMiningRank4())));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Mining.Effect.Decrease", blastDamageDecrease));
            }
        }
    }
}
