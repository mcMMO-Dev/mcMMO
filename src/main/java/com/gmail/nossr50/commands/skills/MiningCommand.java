package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.TextComponentFactory;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillActivationType;
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

    public MiningCommand() {
        super(PrimarySkillType.MINING);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue) {
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
        
        // DOUBLE DROPS
        if (canDoubleDrop) {
            String[] doubleDropStrings = getAbilityDisplayValues(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, player, SubSkillType.MINING_DOUBLE_DROPS);
            doubleDropChance = doubleDropStrings[0];
            doubleDropChanceLucky = doubleDropStrings[1];
        }
        
        // SUPER BREAKER
        if (canSuperBreaker) {
            String[] superBreakerStrings = calculateLengthDisplayValues(player, skillValue);
            superBreakerLength = superBreakerStrings[0];
            superBreakerLengthEndurance = superBreakerStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canBiggerBombs = RankUtils.hasUnlockedSubskill(player, SubSkillType.MINING_BIGGER_BOMBS) && Permissions.biggerBombs(player);
        canBlast = RankUtils.hasUnlockedSubskill(player, SubSkillType.MINING_BLAST_MINING) && Permissions.remoteDetonation(player);
        canDemoExpert = RankUtils.hasUnlockedSubskill(player, SubSkillType.MINING_DEMOLITIONS_EXPERTISE) && Permissions.demolitionsExpertise(player);
        canDoubleDrop = canUseSubskill(player, SubSkillType.MINING_DOUBLE_DROPS);
        canSuperBreaker = RankUtils.hasUnlockedSubskill(player, SubSkillType.MINING_SUPER_BREAKER) && Permissions.superBreaker(player);
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<String>();

        if (canBiggerBombs) {
            messages.add(getStatMessage(true, true, SubSkillType.MINING_BLAST_MINING, String.valueOf(blastRadiusIncrease)));
            //messages.add(LocaleLoader.getString("Mining.Blast.Radius.Increase", blastRadiusIncrease));
        }
        
        if (canBlast) {
            messages.add(getStatMessage(false, true, SubSkillType.MINING_BLAST_MINING, String.valueOf(blastMiningRank), String.valueOf(RankUtils.getHighestRank(SubSkillType.MINING_BLAST_MINING)), LocaleLoader.getString("Mining.Blast.Effect", oreBonus, debrisReduction, bonusTNTDrops)));
            //messages.add(LocaleLoader.getString("Mining.Blast.Rank", blastMiningRank, RankUtils.getHighestRank(SubSkillType.MINING_BLAST_MINING), LocaleLoader.getString("Mining.Blast.Effect", oreBonus, debrisReduction, bonusTNTDrops)));
        }
        
         if (canDemoExpert) {
            messages.add(getStatMessage(SubSkillType.MINING_DEMOLITIONS_EXPERTISE, blastDamageDecrease));
            //messages.add(LocaleLoader.getString("Mining.Effect.Decrease", blastDamageDecrease));
        }
        
        if (canDoubleDrop) {
            messages.add(getStatMessage(SubSkillType.MINING_DOUBLE_DROPS, doubleDropChance)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", doubleDropChanceLucky) : ""));
            //messages.add(LocaleLoader.getString("Mining.Effect.DropChance", doubleDropChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", doubleDropChanceLucky) : ""));
        }

        if (canSuperBreaker) {
            messages.add(getStatMessage(SubSkillType.MINING_SUPER_BREAKER, superBreakerLength)
                    + (hasEndurance ? LocaleLoader.getString("Perks.ActivationTime.Bonus", superBreakerLengthEndurance) : ""));
            //messages.add(LocaleLoader.getString("Mining.Ability.Length", superBreakerLength) + (hasEndurance ? LocaleLoader.getString("Perks.ActivationTime.Bonus", superBreakerLengthEndurance) : ""));
        }

        return messages;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        List<TextComponent> textComponents = new ArrayList<>();

        TextComponentFactory.getSubSkillTextComponents(player, textComponents, PrimarySkillType.MINING);

        return textComponents;
    }
}
