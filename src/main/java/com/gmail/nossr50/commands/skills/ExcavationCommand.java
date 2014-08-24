package com.gmail.nossr50.commands.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;

public class ExcavationCommand extends SkillCommand {
    private String gigaDrillBreakerLength;
    private String gigaDrillBreakerLengthEndurance;

    private boolean canGigaDrill;
    private boolean canTreasureHunt;

    public ExcavationCommand() {
        super(SkillType.excavation);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue, boolean isLucky) {
        // GIGA DRILL BREAKER
        if (canGigaDrill) {
            String gigaDrillStrings[] = calculateLengthDisplayValues(player, skillValue);
            gigaDrillBreakerLength = gigaDrillStrings[0];
            gigaDrillBreakerLengthEndurance = gigaDrillStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canGigaDrill = Permissions.gigaDrillBreaker(player);
        canTreasureHunt = Permissions.secondaryAbilityEnabled(player, SecondaryAbility.excavationTreasureHunter);
    }

    @Override
    protected List<String> effectsDisplay() {
        List<String> messages = new ArrayList<String>();

        if (canGigaDrill) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Excavation.Effect.0"), LocaleLoader.getString("Excavation.Effect.1")));
        }

        if (canTreasureHunt) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Excavation.Effect.2"), LocaleLoader.getString("Excavation.Effect.3")));
        }

        return messages;
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<String>();

        if (canGigaDrill) {
            messages.add(LocaleLoader.getString("Excavation.Effect.Length", gigaDrillBreakerLength) + (hasEndurance ? LocaleLoader.getString("Perks.ActivationTime.Bonus", gigaDrillBreakerLengthEndurance) : ""));
        }

        return messages;
    }
}
