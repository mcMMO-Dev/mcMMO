package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;

public class ArcheryCommand extends SkillCommand {
    private String skillShotBonus;
    private String dazeChance;
    private String retrieveChance;

    private boolean canSkillShot;
    private boolean canDaze;
    private boolean canRetrieve;

    public ArcheryCommand() {
        super(SkillType.ARCHERY);
    }

    @Override
    protected void dataCalculations() {
        if (skillValue >= 1000) {
            skillShotBonus = "200.00%";
            dazeChance = "50.00%";
            retrieveChance = "100.00%";
        }
        else {
            skillShotBonus = percent.format(((int) skillValue / 50) * 0.1D); //TODO: Not sure if this is the best way to calculate this or not...
            dazeChance = percent.format(skillValue / 2000);
            retrieveChance = percent.format(skillValue / 1000);
        }
    }

    @Override
    protected void permissionsCheck() {
        canSkillShot = permInstance.archeryBonus(player);
        canDaze = permInstance.daze(player);
        canRetrieve = permInstance.trackArrows(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canSkillShot || canDaze || canRetrieve;
    }

    @Override
    protected void effectsDisplay() {
        if (canSkillShot) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Archery.Effect.0"), LocaleLoader.getString("Archery.Effect.1") }));
        }

        if (canDaze) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Archery.Effect.2"), LocaleLoader.getString("Archery.Effect.3") }));
        }

        if (canRetrieve) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Archery.Effect.4"), LocaleLoader.getString("Archery.Effect.5") }));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canSkillShot || canDaze || canRetrieve;
    }

    @Override
    protected void statsDisplay() {
        if (canSkillShot) {
            player.sendMessage(LocaleLoader.getString("Archery.Combat.SkillshotBonus", new Object[] { skillShotBonus }));
        }

        if (canDaze) {
            player.sendMessage(LocaleLoader.getString("Archery.Combat.DazeChance", new Object[] { dazeChance }));
        }

        if (canRetrieve) {
            player.sendMessage(LocaleLoader.getString("Archery.Combat.RetrieveChance", new Object[] { retrieveChance }));
        }
    }
}
