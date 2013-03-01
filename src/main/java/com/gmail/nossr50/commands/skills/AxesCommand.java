package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.axes.Axes;
import com.gmail.nossr50.util.Permissions;

public class AxesCommand extends SkillCommand {
    private String critChance;
    private String critChanceLucky;
    private String bonusDamage;
    private String impactDamage;
    private String greaterImpactDamage;
    private String skullSplitterLength;
    private String skullSplitterLengthEndurance;

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
        // IMPACT
        impactDamage = String.valueOf(1 + (skillValue / Axes.impactIncreaseLevel));
        greaterImpactDamage = String.valueOf(Axes.greaterImpactBonusDamage);

        // SKULL SPLITTER
        String[] skullSplitterStrings = calculateLengthDisplayValues();
        skullSplitterLength = skullSplitterStrings[0];
        skullSplitterLengthEndurance = skullSplitterStrings[1];

        // CRITICAL STRIKES
        String[] criticalStrikeStrings = calculateAbilityDisplayValues(Axes.criticalHitMaxBonusLevel, Axes.criticalHitMaxChance);
        critChance = criticalStrikeStrings[0];
        critChanceLucky = criticalStrikeStrings[1];

        // AXE MASTERY
        if (skillValue >= Axes.bonusDamageMaxBonusLevel) {
            bonusDamage = String.valueOf(Axes.bonusDamageMaxBonus);
        }
        else {
            bonusDamage = String.valueOf(skillValue / (Axes.bonusDamageMaxBonusLevel / Axes.bonusDamageMaxBonus));
        }
    }

    @Override
    protected void permissionsCheck() {
        canSkullSplitter = Permissions.skullSplitter(player);
        canCritical = Permissions.criticalStrikes(player);
        canBonusDamage = Permissions.bonusDamage(player, skill);
        canImpact = Permissions.armorImpact(player);
        canGreaterImpact = Permissions.greaterImpact(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canSkullSplitter || canCritical || canBonusDamage || canImpact || canGreaterImpact;
    }

    @Override
    protected void effectsDisplay() {
        luckyEffectsDisplay();

        if (canSkullSplitter) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Axes.Effect.0"), LocaleLoader.getString("Axes.Effect.1")));
        }

        if (canCritical) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Axes.Effect.2"), LocaleLoader.getString("Axes.Effect.3")));
        }

        if (canBonusDamage) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Axes.Effect.4"), LocaleLoader.getString("Axes.Effect.5")));
        }

        if (canImpact) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Axes.Effect.6"), LocaleLoader.getString("Axes.Effect.7")));
        }

        if (canGreaterImpact) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Axes.Effect.8"), LocaleLoader.getString("Axes.Effect.9")));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canSkullSplitter || canCritical || canBonusDamage || canImpact || canGreaterImpact;
    }

    @Override
    protected void statsDisplay() {
        if (canBonusDamage) {
            player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", LocaleLoader.getString("Axes.Ability.Bonus.0"), LocaleLoader.getString("Axes.Ability.Bonus.1", bonusDamage)));
        }

        if (canImpact) {
            player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", LocaleLoader.getString("Axes.Ability.Bonus.2"), LocaleLoader.getString("Axes.Ability.Bonus.3", impactDamage)));
        }

        if (canGreaterImpact) {
            player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", LocaleLoader.getString("Axes.Ability.Bonus.4"), LocaleLoader.getString("Axes.Ability.Bonus.5", greaterImpactDamage)));
        }

        if (canCritical) {
            if (isLucky) {
                player.sendMessage(LocaleLoader.getString("Axes.Combat.CritChance", critChance) + LocaleLoader.getString("Perks.lucky.bonus", critChanceLucky));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Axes.Combat.CritChance", critChance));
            }
        }

        if (canSkullSplitter) {
            if (hasEndurance) {
                player.sendMessage(LocaleLoader.getString("Axes.Combat.SS.Length", skullSplitterLength) + LocaleLoader.getString("Perks.activationtime.bonus", skullSplitterLengthEndurance));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Axes.Combat.SS.Length", skullSplitterLength));
            }
        }
    }
}
