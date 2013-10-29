package com.gmail.nossr50.commands.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.swords.Swords;
import com.gmail.nossr50.util.Permissions;

public class SwordsCommand extends SkillCommand {
    private String counterAttackChance;
    private String counterAttackChanceLucky;
    private int bleedLength;
    private String bleedChance;
    private String bleedChanceLucky;
    private String serratedStrikesLength;
    private String serratedStrikesLengthEndurance;

    private boolean canCounter;
    private boolean canSerratedStrike;
    private boolean canBleed;

    public SwordsCommand() {
        super(SkillType.SWORDS);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue, boolean isLucky) {
        // SERRATED STRIKES
        if (canSerratedStrike) {
            String[] serratedStrikesStrings = calculateLengthDisplayValues(player, skillValue);
            serratedStrikesLength = serratedStrikesStrings[0];
            serratedStrikesLengthEndurance = serratedStrikesStrings[1];
        }

        // BLEED
        if (canBleed) {
            bleedLength = (skillValue >= Swords.bleedMaxBonusLevel) ? Swords.bleedMaxTicks : Swords.bleedBaseTicks;

            String[] bleedStrings = calculateAbilityDisplayValues(skillValue, Swords.bleedMaxBonusLevel, Swords.bleedMaxChance, isLucky);
            bleedChance = bleedStrings[0];
            bleedChanceLucky = bleedStrings[1];
        }

        // COUNTER ATTACK
        if (canCounter) {
            String[] counterAttackStrings = calculateAbilityDisplayValues(skillValue, Swords.counterAttackMaxBonusLevel, Swords.counterAttackMaxChance, isLucky);
            counterAttackChance = counterAttackStrings[0];
            counterAttackChanceLucky = counterAttackStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canBleed = Permissions.bleed(player);
        canCounter = Permissions.counterAttack(player);
        canSerratedStrike = Permissions.serratedStrikes(player);
    }

    @Override
    protected List<String> effectsDisplay() {
        List<String> messages = new ArrayList<String>();

        if (canCounter) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Swords.Effect.0"), LocaleLoader.getString("Swords.Effect.1", percent.format(1.0D / Swords.counterAttackModifier))));
        }

        if (canSerratedStrike) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Swords.Effect.2"), LocaleLoader.getString("Swords.Effect.3", percent.format(1.0D / Swords.serratedStrikesModifier))));
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Swords.Effect.4"), LocaleLoader.getString("Swords.Effect.5", Swords.serratedStrikesBleedTicks)));
        }

        if (canBleed) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Swords.Effect.6"), LocaleLoader.getString("Swords.Effect.7")));
        }

        return messages;
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<String>();

        if (canCounter) {
            messages.add(LocaleLoader.getString("Swords.Combat.Counter.Chance", counterAttackChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", counterAttackChanceLucky) : ""));
        }

        if (canBleed) {
            messages.add(LocaleLoader.getString("Swords.Combat.Bleed.Length", bleedLength));
            messages.add(LocaleLoader.getString("Swords.Combat.Bleed.Note"));
            messages.add(LocaleLoader.getString("Swords.Combat.Bleed.Chance", bleedChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", bleedChanceLucky) : ""));
        }

        if (canSerratedStrike) {
            messages.add(LocaleLoader.getString("Swords.SS.Length", serratedStrikesLength) + (hasEndurance ? LocaleLoader.getString("Perks.ActivationTime.Bonus", serratedStrikesLengthEndurance) : ""));
        }

        return messages;
    }
}
