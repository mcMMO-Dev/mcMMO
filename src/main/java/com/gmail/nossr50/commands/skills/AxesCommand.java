package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;

public class AxesCommand extends SkillCommand {
    private String critChance;
    private String bonusDamage;
    private String impactDamage;
    private String greaterImpactDamage;
    private String skullSplitterLength;

    private boolean canSkullSplitter;
    private boolean canCritical;
    private boolean canBonusDamage;
    private boolean canImpact;
    private boolean canGreaterImpact;

    public AxesCommand() {
        super(SkillType.AXES);
    }

    @Override
    protected void dataCalculations() {
        impactDamage = String.valueOf(5 + ((int) skillValue / 30));
        skullSplitterLength = String.valueOf(2 + ((int) skillValue / 50));
        greaterImpactDamage = "2";

        if (skillValue >= 750) {
            critChance = "37.50";
            bonusDamage = "4";
        }
        else if (skillValue >= 200) {
            critChance = percent.format(skillValue / 2000);
            bonusDamage = "4";
        }
        else {
            critChance = percent.format(skillValue / 2000);
            bonusDamage = String.valueOf((int) skillValue / 50);
        }
    }

    @Override
    protected void permissionsCheck() {
        canSkullSplitter = permInstance.skullSplitter(player);
        canCritical = permInstance.criticalHit(player);
        canBonusDamage = permInstance.axeBonus(player);
        canImpact = permInstance.impact(player);
        canGreaterImpact = permInstance.greaterImpact(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canSkullSplitter || canCritical || canBonusDamage || canImpact || canGreaterImpact;
    }

    @Override
    protected void effectsDisplay() {
        if (canSkullSplitter) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Axes.Effect.0"), LocaleLoader.getString("Axes.Effect.1") }));
        }

        if (canCritical) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Axes.Effect.2"), LocaleLoader.getString("Axes.Effect.3") }));
        }

        if (canBonusDamage) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Axes.Effect.4"), LocaleLoader.getString("Axes.Effect.5") }));
        }

        if (canImpact) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Axes.Effect.6"), LocaleLoader.getString("Axes.Effect.7") }));
        }

        if (canGreaterImpact) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Axes.Effect.8"), LocaleLoader.getString("Axes.Effect.9") }));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canSkullSplitter || canCritical || canBonusDamage || canImpact || canGreaterImpact;
    }

    @Override
    protected void statsDisplay() {
        if (canBonusDamage) {
            player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Axes.Ability.Bonus.0"), LocaleLoader.getString("Axes.Ability.Bonus.1", new Object[] {bonusDamage}) }));
        }

        if (canImpact) {
            player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Axes.Ability.Bonus.2"), LocaleLoader.getString("Axes.Ability.Bonus.3", new Object[] {impactDamage}) }));
        }

        if (canGreaterImpact) {
            player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Axes.Ability.Bonus.4"), LocaleLoader.getString("Axes.Ability.Bonus.5", new Object[] {greaterImpactDamage}) }));
        }

        if (canCritical) {
            player.sendMessage(LocaleLoader.getString("Axes.Combat.CritChance", new Object[] { critChance }));
        }

        if (canSkullSplitter) {
            player.sendMessage(LocaleLoader.getString("Axes.Combat.SS.Length", new Object[] { skullSplitterLength }));
        }
    }
}
