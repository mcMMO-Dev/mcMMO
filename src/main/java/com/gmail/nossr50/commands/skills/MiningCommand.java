package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.mining.MiningManager;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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

    public MiningCommand(mcMMO pluginRef) {
        super(PrimarySkillType.MINING, pluginRef);
    }

    @Override
    protected void dataCalculations(Player player, double skillValue) {
        // BLAST MINING
        if (canBlast || canDemoExpert || canBiggerBombs) {
            MiningManager miningManager = pluginRef.getUserManager().getPlayer(player).getMiningManager();

            blastMiningRank = miningManager.getBlastMiningTier();
            bonusTNTDrops = miningManager.getDropMultiplier();
            oreBonus = percent.format(miningManager.getOreBonus() / 30.0D); // Base received in TNT is 30%
            debrisReduction = percent.format(miningManager.getDebrisReduction() / 30.0D); // Base received in TNT is 30%
            blastDamageDecrease = percent.format(miningManager.getBlastDamageModifier() / 100.0D);
            blastRadiusIncrease = miningManager.getBlastRadiusModifier();
        }

        // DOUBLE DROPS
        if (canDoubleDrop) {
            String[] doubleDropStrings = getAbilityDisplayValues(player, SubSkillType.MINING_DOUBLE_DROPS);
            doubleDropChance = doubleDropStrings[0];
            doubleDropChanceLucky = doubleDropStrings[1];
        }

        // SUPER BREAKER
        if (canSuperBreaker) {
            String[] superBreakerStrings = formatLengthDisplayValues(player, skillValue);
            superBreakerLength = superBreakerStrings[0];
            superBreakerLengthEndurance = superBreakerStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canBiggerBombs = pluginRef.getRankTools().hasUnlockedSubskill(player, SubSkillType.MINING_BIGGER_BOMBS) && pluginRef.getPermissionTools().biggerBombs(player);
        canBlast = pluginRef.getRankTools().hasUnlockedSubskill(player, SubSkillType.MINING_BLAST_MINING) && pluginRef.getPermissionTools().remoteDetonation(player);
        canDemoExpert = pluginRef.getRankTools().hasUnlockedSubskill(player, SubSkillType.MINING_DEMOLITIONS_EXPERTISE) && pluginRef.getPermissionTools().demolitionsExpertise(player);
        canDoubleDrop = canUseSubSkill(player, SubSkillType.MINING_DOUBLE_DROPS);
        canSuperBreaker = pluginRef.getRankTools().hasUnlockedSubskill(player, SubSkillType.MINING_SUPER_BREAKER) && pluginRef.getPermissionTools().superBreaker(player);
    }

    @Override
    protected List<String> statsDisplay(Player player, double skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<>();

        if (canBiggerBombs) {
            messages.add(getStatMessage(true, true, SubSkillType.MINING_BLAST_MINING, String.valueOf(blastRadiusIncrease)));
            //messages.add(pluginRef.getLocaleManager().getString("Mining.Blast.Radius.Increase", blastRadiusIncrease));
        }

        if (canBlast) {
            messages.add(getStatMessage(false, true, SubSkillType.MINING_BLAST_MINING, String.valueOf(blastMiningRank), String.valueOf(pluginRef.getRankTools().getHighestRank(SubSkillType.MINING_BLAST_MINING)), pluginRef.getLocaleManager().getString("Mining.Blast.Effect", oreBonus, debrisReduction, bonusTNTDrops)));
            //messages.add(pluginRef.getLocaleManager().getString("Mining.Blast.Rank", blastMiningRank, RankUtils.getHighestRank(SubSkillType.MINING_BLAST_MINING), pluginRef.getLocaleManager().getString("Mining.Blast.Effect", oreBonus, debrisReduction, bonusTNTDrops)));
        }

        if (canDemoExpert) {
            messages.add(getStatMessage(SubSkillType.MINING_DEMOLITIONS_EXPERTISE, blastDamageDecrease));
            //messages.add(pluginRef.getLocaleManager().getString("Mining.Effect.Decrease", blastDamageDecrease));
        }

        if (canDoubleDrop) {
            messages.add(getStatMessage(SubSkillType.MINING_DOUBLE_DROPS, doubleDropChance)
                    + (isLucky ? pluginRef.getLocaleManager().getString("Perks.Lucky.Bonus", doubleDropChanceLucky) : ""));
            //messages.add(pluginRef.getLocaleManager().getString("Mining.Effect.DropChance", doubleDropChance) + (isLucky ? pluginRef.getLocaleManager().getString("Perks.Lucky.Bonus", doubleDropChanceLucky) : ""));
        }

        if (canSuperBreaker) {
            messages.add(getStatMessage(SubSkillType.MINING_SUPER_BREAKER, superBreakerLength)
                    + (hasEndurance ? pluginRef.getLocaleManager().getString("Perks.ActivationTime.Bonus", superBreakerLengthEndurance) : ""));
            //messages.add(pluginRef.getLocaleManager().getString("Mining.Ability.Length", superBreakerLength) + (hasEndurance ? pluginRef.getLocaleManager().getString("Perks.ActivationTime.Bonus", superBreakerLengthEndurance) : ""));
        }

        return messages;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        List<TextComponent> textComponents = new ArrayList<>();

        pluginRef.getTextComponentFactory().getSubSkillTextComponents(player, textComponents, PrimarySkillType.MINING);

        return textComponents;
    }
}
