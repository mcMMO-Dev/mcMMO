package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.archery.Archery;
import com.gmail.nossr50.util.Permissions;

public class ArcheryCommand extends SkillCommand {
    private String skillShotBonus;
    private String dazeChance;
    private String dazeChanceLucky;
    private String retrieveChance;
    private String retrieveChanceLucky;

    private boolean canSkillShot;
    private boolean canDaze;
    private boolean canRetrieve;

    public ArcheryCommand() {
        super(SkillType.ARCHERY);
    }

    @Override
    protected void dataCalculations() {
        // SKILL SHOT
        if (canSkillShot) {
            double bonus = (skillValue / Archery.skillShotIncreaseLevel) * Archery.skillShotIncreasePercentage;
            skillShotBonus = percent.format(Math.min(bonus, Archery.skillShotMaxBonusPercentage));
        }

        // DAZE
        if (canDaze) {
            String[] dazeStrings = calculateAbilityDisplayValues(Archery.dazeMaxBonusLevel, Archery.dazeMaxBonus);
            dazeChance = dazeStrings[0];
            dazeChanceLucky = dazeStrings[1];
        }

        // RETRIEVE
        if (canRetrieve) {
            String[] retrieveStrings = calculateAbilityDisplayValues(Archery.retrieveMaxBonusLevel, Archery.retrieveMaxChance);
            retrieveChance = retrieveStrings[0];
            retrieveChanceLucky = retrieveStrings[1];
        }
    }

    @Override
    protected void permissionsCheck() {
        canSkillShot = Permissions.bonusDamage(player, skill);
        canDaze = Permissions.daze(player);
        canRetrieve = Permissions.arrowRetrieval(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canSkillShot || canDaze || canRetrieve;
    }

    @Override
    protected void effectsDisplay() {
        luckyEffectsDisplay();

        if (canSkillShot) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Archery.Effect.0"), LocaleLoader.getString("Archery.Effect.1")));
        }

        if (canDaze) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Archery.Effect.2"), LocaleLoader.getString("Archery.Effect.3", Archery.dazeModifier)));
        }

        if (canRetrieve) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Archery.Effect.4"), LocaleLoader.getString("Archery.Effect.5")));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canSkillShot || canDaze || canRetrieve;
    }

    @Override
    protected void statsDisplay() {
        if (canSkillShot) {
            player.sendMessage(LocaleLoader.getString("Archery.Combat.SkillshotBonus", skillShotBonus));
        }

        if (canDaze) {
            player.sendMessage(LocaleLoader.getString("Archery.Combat.DazeChance", dazeChance) + (isLucky ? LocaleLoader.getString("Perks.lucky.bonus", dazeChanceLucky) : ""));
        }

        if (canRetrieve) {
            player.sendMessage(LocaleLoader.getString("Archery.Combat.RetrieveChance", retrieveChance) + (isLucky ? LocaleLoader.getString("Perks.lucky.bonus", retrieveChanceLucky) : ""));
        }
    }
}
