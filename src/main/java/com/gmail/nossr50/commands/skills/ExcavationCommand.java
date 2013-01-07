package com.gmail.nossr50.commands.skills;

import org.bukkit.ChatColor;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;

public class ExcavationCommand extends SkillCommand {
    AdvancedConfig advancedConfig = AdvancedConfig.getInstance();
    private String gigaDrillBreakerLength;

    private int abilityLengthIncreaseLevel = advancedConfig.getAbilityLength();

    private boolean canGigaDrill;
    private boolean canTreasureHunt;
    private boolean lucky;

    public ExcavationCommand() {
        super(SkillType.EXCAVATION);
    }

    @Override
    protected void dataCalculations() {
        gigaDrillBreakerLength = String.valueOf(2 + (int) ((double) skillValue / (double) abilityLengthIncreaseLevel));
    }

    @Override
    protected void permissionsCheck() {
        canGigaDrill = Permissions.gigaDrillBreaker(player);
        canTreasureHunt = Permissions.excavationTreasures(player);
        lucky = Permissions.luckyExcavation(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canGigaDrill || canTreasureHunt;
    }

    @Override
    protected void effectsDisplay() {
        if (lucky) {
            String perkPrefix = ChatColor.RED + "[mcMMO Perks] ";
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.lucky.name"), LocaleLoader.getString("Perks.lucky.desc", new Object[] { "Excavation" }) }));
        }

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
