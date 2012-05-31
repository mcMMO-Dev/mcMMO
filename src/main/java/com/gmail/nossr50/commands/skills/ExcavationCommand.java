package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;

public class ExcavationCommand extends SkillCommand {
    private String gigaDrillBreakerLength;

    private boolean canGigaDrill;
    private boolean canTreasureHunt;

    public ExcavationCommand() {
        super(SkillType.EXCAVATION);
    }

    @Override
    protected void dataCalculations() {
        gigaDrillBreakerLength = String.valueOf(2 + ((int) skillValue / 50));
    }

    @Override
    protected void permissionsCheck() {
        canGigaDrill = permInstance.gigaDrillBreaker(player);
        canTreasureHunt = permInstance.excavationTreasures(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canGigaDrill || canTreasureHunt;
    }

    @Override
    protected void effectsDisplay() {
        if (canGigaDrill) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Excavation.Effect.0"), LocaleLoader.getString("Excavation.Effect.1") }));
        }

        if (canTreasureHunt) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Excavation.Effect.2"), LocaleLoader.getString("Excavation.Effect.3") }));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canGigaDrill;
    }

    @Override
    protected void statsDisplay() {
        if (canGigaDrill) {
            player.sendMessage(LocaleLoader.getString("Excavation.Effect.Length", new Object[] { gigaDrillBreakerLength }));
        }
    }
}
