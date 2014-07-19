package com.gmail.nossr50.commands.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.mining.BlastMining;
import com.gmail.nossr50.skills.mining.BlastMining.Tier;
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

    public MiningCommand() {
        super(SkillType.MINING);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue, boolean isLucky) {
        // SUPER BREAKER
        if (canSuperBreaker) {
            String[] superBreakerStrings = calculateLengthDisplayValues(player, skillValue);
            superBreakerLength = superBreakerStrings[0];
            superBreakerLengthEndurance = superBreakerStrings[1];
        }

        // DOUBLE DROPS
        if (canDoubleDrop) {
            String[] doubleDropStrings = calculateAbilityDisplayValues(skillValue, SecondaryAbility.MINING_DOUBLE_DROPS, isLucky);
            doubleDropChance = doubleDropStrings[0];
            doubleDropChanceLucky = doubleDropStrings[1];
        }

        // BLAST MINING
        if (canBlast || canDemoExpert || canBiggerBombs) {
            MiningManager miningManager = UserManager.getPlayer(player).getMiningManager();

            blastMiningRank = miningManager.getBlastMiningTier();
            bonusTNTDrops = miningManager.getDropMultiplier();
            oreBonus = percent.format(miningManager.getOreBonus() / 30.0D); // Base received in TNT is 30%
            debrisReduction = percent.format(miningManager.getDebrisReduction() / 30.0D); // Base received in TNT is 30%
            blastDamageDecrease = percent.format(miningManager.getBlastDamageModifier() / 100.0D);
            blastRadiusIncrease = miningManager.getBlastRadiusModifier();
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canBiggerBombs = Permissions.biggerBombs(player);
        canBlast = Permissions.remoteDetonation(player);
        canDemoExpert = Permissions.demolitionsExpertise(player);
        canDoubleDrop = Permissions.secondaryAbilityEnabled(player, SecondaryAbility.MINING_DOUBLE_DROPS) && !skill.getDoubleDropsDisabled();
        canSuperBreaker = Permissions.superBreaker(player);
    }

    @Override
    protected List<String> effectsDisplay() {
        List<String> messages = new ArrayList<String>();

        if (canSuperBreaker) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Mining.Effect.0"), LocaleLoader.getString("Mining.Effect.1")));
        }

        if (canDoubleDrop) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Mining.Effect.2"), LocaleLoader.getString("Mining.Effect.3")));
        }

        if (canBlast) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Mining.Effect.4"), LocaleLoader.getString("Mining.Effect.5")));
        }

        if (canBiggerBombs) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Mining.Effect.6"), LocaleLoader.getString("Mining.Effect.7")));
        }

        if (canDemoExpert) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Mining.Effect.8"), LocaleLoader.getString("Mining.Effect.9")));
        }

        return messages;
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<String>();

        if (canDoubleDrop) {
            messages.add(LocaleLoader.getString("Mining.Effect.DropChance", doubleDropChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", doubleDropChanceLucky) : ""));
        }

        if (canSuperBreaker) {
            messages.add(LocaleLoader.getString("Mining.Ability.Length", superBreakerLength) + (hasEndurance ? LocaleLoader.getString("Perks.ActivationTime.Bonus", superBreakerLengthEndurance) : ""));
        }

        if (canBlast) {
            int unlockLevel = AdvancedConfig.getInstance().getBlastMiningRankLevel(Tier.ONE);

            if (skillValue < unlockLevel) {
                messages.add(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Mining.Ability.Locked.0", unlockLevel)));
            }
            else {
                messages.add(LocaleLoader.getString("Mining.Blast.Rank", blastMiningRank, Tier.values().length, LocaleLoader.getString("Mining.Blast.Effect", oreBonus, debrisReduction, bonusTNTDrops)));
            }
        }

        if (canBiggerBombs) {
            int unlockLevel = BlastMining.getBiggerBombsUnlockLevel();

            if (skillValue < unlockLevel) {
                messages.add(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Mining.Ability.Locked.1", unlockLevel)));
            }
            else {
                messages.add(LocaleLoader.getString("Mining.Blast.Radius.Increase", blastRadiusIncrease));
            }
        }

        if (canDemoExpert) {
            int unlockLevel = BlastMining.getDemolitionExpertUnlockLevel();

            if (skillValue < unlockLevel) {
                messages.add(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Mining.Ability.Locked.2", unlockLevel)));
            }
            else {
                messages.add(LocaleLoader.getString("Mining.Effect.Decrease", blastDamageDecrease));
            }
        }

        return messages;
    }
}
