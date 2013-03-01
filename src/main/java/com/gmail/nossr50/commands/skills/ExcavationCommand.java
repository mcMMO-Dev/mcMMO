package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;

public class ExcavationCommand extends SkillCommand {
    private String gigaDrillBreakerLength;
    private String gigaDrillBreakerLengthEndurance;

    private boolean canGigaDrill;
    private boolean canTreasureHunt;

    public ExcavationCommand() {
        super(SkillType.EXCAVATION);
    }

    @Override
    protected void dataCalculations() {
        // GIGA DRILL BREAKER
        String gigaDrillStrings[] = calculateLengthDisplayValues();
        gigaDrillBreakerLength = gigaDrillStrings[0];
        gigaDrillBreakerLengthEndurance = gigaDrillStrings[1];
    }

    @Override
    protected void permissionsCheck() {
        canGigaDrill = Permissions.gigaDrillBreaker(player);
        canTreasureHunt = Permissions.excavationTreasureHunter(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canGigaDrill || canTreasureHunt;
    }

    @Override
    protected void effectsDisplay() {
        luckyEffectsDisplay();

        if (canGigaDrill) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Excavation.Effect.0"), LocaleLoader.getString("Excavation.Effect.1")));
        }

        if (canTreasureHunt) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Excavation.Effect.2"), LocaleLoader.getString("Excavation.Effect.3")));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canGigaDrill;
    }

    @Override
    protected void statsDisplay() {
        if (canGigaDrill) {
            if (hasEndurance) {
                player.sendMessage(LocaleLoader.getString("Excavation.Effect.Length", gigaDrillBreakerLength) + LocaleLoader.getString("Perks.activationtime.bonus", gigaDrillBreakerLengthEndurance));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Excavation.Effect.Length", gigaDrillBreakerLength));
            }
        }
    }
}
