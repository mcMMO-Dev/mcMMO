package com.gmail.nossr50.commands.skills;

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
    protected void dataCalculations() {
        // SERRATED STRIKES
        if (canSerratedStrike) {
            String[] serratedStrikesStrings = calculateLengthDisplayValues();
            serratedStrikesLength = serratedStrikesStrings[0];
            serratedStrikesLengthEndurance = serratedStrikesStrings[1];
        }

        // BLEED
        if (canBleed) {
            bleedLength = (skillValue >= Swords.bleedMaxBonusLevel) ? Swords.bleedMaxTicks : Swords.bleedBaseTicks;

            String[] bleedStrings = calculateAbilityDisplayValues(Swords.bleedMaxBonusLevel, Swords.bleedMaxChance);
            bleedChance = bleedStrings[0];
            bleedChanceLucky = bleedStrings[1];
        }

        // COUNTER ATTACK
        if (canCounter) {
            String[] counterAttackStrings = calculateAbilityDisplayValues(Swords.counterAttackMaxBonusLevel, Swords.counterAttackMaxChance);
            counterAttackChance = counterAttackStrings[0];
            counterAttackChanceLucky = counterAttackStrings[1];
        }
    }

    @Override
    protected void permissionsCheck() {
        canBleed = Permissions.bleed(player);
        canCounter = Permissions.counterAttack(player);
        canSerratedStrike = Permissions.serratedStrikes(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canBleed || canCounter || canSerratedStrike;
    }

    @Override
    protected void effectsDisplay() {
        luckyEffectsDisplay();

        if (canCounter) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Swords.Effect.0"), LocaleLoader.getString("Swords.Effect.1", percent.format(1.0D / Swords.counterAttackModifier))));
        }

        if (canSerratedStrike) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Swords.Effect.2"), LocaleLoader.getString("Swords.Effect.3", percent.format(1.0D / Swords.serratedStrikesModifier))));
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Swords.Effect.4"), LocaleLoader.getString("Swords.Effect.5", Swords.serratedStrikesBleedTicks)));
        }

        if (canBleed) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Swords.Effect.6"), LocaleLoader.getString("Swords.Effect.7")));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canBleed || canCounter || canSerratedStrike;
    }

    @Override
    protected void statsDisplay() {
        if (canCounter) {
            player.sendMessage(LocaleLoader.getString("Swords.Combat.Counter.Chance", counterAttackChance) + (isLucky ? LocaleLoader.getString("Perks.lucky.bonus", counterAttackChanceLucky) : ""));
        }

        if (canBleed) {
            player.sendMessage(LocaleLoader.getString("Swords.Combat.Bleed.Length", bleedLength));
            player.sendMessage(LocaleLoader.getString("Swords.Combat.Bleed.Note"));
            player.sendMessage(LocaleLoader.getString("Swords.Combat.Bleed.Chance", bleedChance) + (isLucky ? LocaleLoader.getString("Perks.lucky.bonus", bleedChanceLucky) : ""));
        }

        if (canSerratedStrike) {
            player.sendMessage(LocaleLoader.getString("Swords.SS.Length", serratedStrikesLength) + (hasEndurance ? LocaleLoader.getString("Perks.activationtime.bonus", serratedStrikesLengthEndurance) : ""));
        }
    }
}
