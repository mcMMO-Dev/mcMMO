package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;

public class SwordsCommand extends SkillCommand {
    private String counterAttackChance;
    private String bleedLength;
    private String bleedChance;
    private String serratedStrikesLength;

    private boolean canCounter;
    private boolean canSerratedStrike;
    private boolean canBleed;

    public SwordsCommand() {
        super(SkillType.SWORDS);
    }

    @Override
    protected void dataCalculations() {
        serratedStrikesLength = String.valueOf(2 + ((int) skillValue / 50));

        if (skillValue >= 750) {
            bleedLength = "3";
            bleedChance = "75.00%";
            counterAttackChance = "30.00%";
        }
        else if (skillValue >= 600) {
            bleedLength = "2";
            bleedChance = percent.format(skillValue / 1000);
            counterAttackChance = "30.00%";
        }
        else {
            bleedLength = "2";
            bleedChance = percent.format(skillValue / 1000);
            counterAttackChance = percent.format(skillValue / 2000);
        }
    }

    @Override
    protected void permissionsCheck() {
        canBleed = permInstance.swordsBleed(player);
        canCounter = permInstance.counterAttack(player);
        canSerratedStrike = permInstance.serratedStrikes(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canBleed || canCounter || canSerratedStrike;
    }

    @Override
    protected void effectsDisplay() {
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
            player.sendMessage(LocaleLoader.getString("Swords.Combat.Counter.Chance", new Object[] { counterAttackChance }));
        }

        if (canBleed) {
            player.sendMessage(LocaleLoader.getString("Swords.Combat.Bleed.Length", new Object[] { bleedLength }));
            player.sendMessage(LocaleLoader.getString("Swords.Combat.Bleed.Note"));
            player.sendMessage(LocaleLoader.getString("Swords.Combat.Bleed.Chance", new Object[] { bleedChance }));
        }

        if (canSerratedStrike) {
            player.sendMessage(LocaleLoader.getString("Swords.SS.Length", new Object[] { serratedStrikesLength }));
        }
    }
}
