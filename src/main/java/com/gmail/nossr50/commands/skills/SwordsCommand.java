package com.gmail.nossr50.commands.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.swords.Swords;
import com.gmail.nossr50.util.Permissions;

public class SwordsCommand extends SkillCommand {
    private String counterChance;
    private String counterChanceLucky;
    private int bleedLength;
    private String bleedChance;
    private String bleedChanceLucky;
    private String serratedStrikesLength;
    private String serratedStrikesLengthEndurance;

    private boolean canCounter;
    private boolean canSerratedStrike;
    private boolean canBleed;

    public SwordsCommand() {
        super(SkillType.swords);
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
            bleedLength = (skillValue >= AdvancedConfig.getInstance().getMaxBonusLevel(SecondaryAbility.bleed)) ? Swords.bleedMaxTicks : Swords.bleedBaseTicks;

            String[] bleedStrings = calculateAbilityDisplayValues(skillValue, SecondaryAbility.bleed, isLucky);
            bleedChance = bleedStrings[0];
            bleedChanceLucky = bleedStrings[1];
        }

        // COUNTER
        if (canCounter) {
            String[] counterStrings = calculateAbilityDisplayValues(skillValue, SecondaryAbility.counter, isLucky);
            counterChance = counterStrings[0];
            counterChanceLucky = counterStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canBleed = Permissions.secondaryAbilityEnabled(player, SecondaryAbility.bleed);
        canCounter = Permissions.secondaryAbilityEnabled(player, SecondaryAbility.counter);
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
            messages.add(LocaleLoader.getString("Swords.Combat.Counter.Chance", counterChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", counterChanceLucky) : ""));
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
