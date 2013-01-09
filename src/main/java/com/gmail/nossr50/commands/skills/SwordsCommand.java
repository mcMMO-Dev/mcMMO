package com.gmail.nossr50.commands.skills;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;

public class SwordsCommand extends SkillCommand {
    AdvancedConfig advancedConfig = AdvancedConfig.getInstance();

    private String counterAttackChance;
    private String counterAttackChanceLucky;
    private String bleedLength;
    private String bleedChance;
    private String bleedChanceLucky;
    private String serratedStrikesLength;

    private int abilityLengthIncreaseLevel = advancedConfig.getAbilityLength();
    private float bleedChanceMax = advancedConfig.getBleedChanceMax();
    private float bleedMaxLevel = advancedConfig.getBleedMaxBonusLevel();
    private int bleedMaxTicks = advancedConfig.getBleedMaxTicks();
    private int bleedBaseTicks = advancedConfig.getBleedBaseTicks();
    private float counterChanceMax = advancedConfig.getCounterChanceMax();
    private float counterMaxLevel = advancedConfig.getCounterMaxBonusLevel();
    private int serratedBleedTicks = advancedConfig.getSerratedStrikesTicks();

    private boolean canCounter;
    private boolean canSerratedStrike;
    private boolean canBleed;
    private boolean lucky;

    public SwordsCommand() {
        super(SkillType.SWORDS);
    }

    @Override
    protected void dataCalculations() {
        DecimalFormat df = new DecimalFormat("0.0");
        float bleedChanceF;
        float counterAttackChanceF;
        serratedStrikesLength = String.valueOf(2 + (int) ((double) skillValue / (double) abilityLengthIncreaseLevel));

        if (skillValue >= bleedMaxLevel) bleedLength = String.valueOf(bleedMaxTicks);
        else bleedLength = String.valueOf(bleedBaseTicks);

        if(skillValue >= bleedMaxLevel) bleedChanceF = bleedChanceMax;
        else bleedChanceF = (float) (((double) bleedChanceMax / (double) bleedMaxLevel) * skillValue);
        bleedChance = df.format(bleedChanceF);
        if(bleedChanceF + bleedChanceF * 0.3333D >= 100D) bleedChanceLucky = df.format(100D);
        else bleedChanceLucky = df.format(bleedChanceF + bleedChanceF * 0.3333D);

        if(skillValue >= counterMaxLevel) counterAttackChanceF = counterChanceMax;
        else counterAttackChanceF = (float) (((double) counterChanceMax / (double) counterMaxLevel) * skillValue);
        counterAttackChance = df.format(counterAttackChanceF);
        if(counterAttackChanceF + counterAttackChanceF * 0.3333D >= 100D) counterAttackChanceLucky = df.format(100D);
        else counterAttackChanceLucky = df.format(counterAttackChanceF + counterAttackChanceF * 0.3333D);
    }

    @Override
    protected void permissionsCheck() {
        canBleed = Permissions.swordsBleed(player);
        canCounter = Permissions.counterAttack(player);
        canSerratedStrike = Permissions.serratedStrikes(player);
        lucky = Permissions.luckySwords(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canBleed || canCounter || canSerratedStrike;
    }

    @Override
    protected void effectsDisplay() {
        if (lucky) {
            String perkPrefix = ChatColor.RED + "[mcMMO Perks] ";
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.lucky.name"), LocaleLoader.getString("Perks.lucky.desc", new Object[] { "Swords" }) }));
        }

        if (canCounter) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Swords.Effect.0"), LocaleLoader.getString("Swords.Effect.1") }));
        }

        if (canSerratedStrike) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Swords.Effect.2"), LocaleLoader.getString("Swords.Effect.3") }));
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Swords.Effect.4"), LocaleLoader.getString("Swords.Effect.5") }));
        }

        if (canBleed) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Swords.Effect.6"), LocaleLoader.getString("Swords.Effect.7") }));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canBleed || canCounter || canSerratedStrike;
    }

    @Override
    protected void statsDisplay() {
        if (canCounter) {
            if (player.hasPermission("mcmmo.perks.lucky.swords"))
                player.sendMessage(LocaleLoader.getString("Swords.Combat.Counter.Chance", new Object[] { counterAttackChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { counterAttackChanceLucky }));
            else
                player.sendMessage(LocaleLoader.getString("Swords.Combat.Counter.Chance", new Object[] { counterAttackChance }));
        }

        if (canBleed) {
            player.sendMessage(LocaleLoader.getString("Swords.Combat.Bleed.Length", new Object[] { bleedLength }));
            player.sendMessage(LocaleLoader.getString("Swords.Combat.Bleed.Note"));
            if (player.hasPermission("mcmmo.perks.lucky.swords"))
                player.sendMessage(LocaleLoader.getString("Swords.Combat.Bleed.Chance", new Object[] { bleedChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { bleedChanceLucky }));
            else
                player.sendMessage(LocaleLoader.getString("Swords.Combat.Bleed.Chance", new Object[] { bleedChance }));
        }

        if (canSerratedStrike) {
            player.sendMessage(LocaleLoader.getString("Swords.SS.Length", new Object[] { serratedStrikesLength }));
        }
    }
}
