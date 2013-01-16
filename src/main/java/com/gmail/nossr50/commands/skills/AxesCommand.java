package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.axes.Axes;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Skills;

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
    private boolean lucky;
    private boolean endurance;

    public AxesCommand() {
        super(SkillType.AXES);
    }

    @Override
    protected void dataCalculations() {
        float critChanceF;
        int skillCheck = Misc.skillCheck((int) skillValue, Axes.criticalHitMaxBonusLevel);

        //Armor Impact
        impactDamage = String.valueOf(1 + ((double) skillValue / (double) Axes.impactIncreaseLevel));
        //Skull Splitter
        int length = 2 + (int) ((double) skillValue / (double) Misc.abilityLengthIncreaseLevel);
        skullSplitterLength = String.valueOf(length);

        if (Permissions.activationTwelve(player)) {
            length = length + 12;
        }
        else if (Permissions.activationEight(player)) {
            length = length + 8;
        }
        else if (Permissions.activationFour(player)) {
            length = length + 4;
        }
        int maxLength = SkillType.AXES.getAbility().getMaxTicks();
        if (maxLength != 0 && length > maxLength) {
            length = maxLength;
        }
        skullSplitterLengthEndurance = String.valueOf(length);

        //Greater Impact
        greaterImpactDamage = String.valueOf(Axes.greaterImpactBonusDamage);
        //Critical Strikes
        if (skillValue >= Axes.criticalHitMaxBonusLevel) critChanceF = (float) Axes.criticalHitMaxChance;
        else critChanceF = (float) ((Axes.criticalHitMaxChance / Axes.criticalHitMaxBonusLevel) * skillCheck);
        critChance = percent.format(critChanceF / 100D);
        if (critChanceF * 1.3333D >= 100D) critChanceLucky = percent.format(1D);
        else critChanceLucky = percent.format(critChanceF * 1.3333D / 100D);
        //Axe Mastery
        if (skillValue >= Axes.bonusDamageMaxBonusLevel) bonusDamage = String.valueOf(Axes.bonusDamageMaxBonus);
        else bonusDamage = String.valueOf(skillValue / ((double) Axes.bonusDamageMaxBonusLevel / (double) Axes.bonusDamageMaxBonus));
    }

    @Override
    protected void permissionsCheck() {
        canSkullSplitter = Permissions.skullSplitter(player);
        canCritical = Permissions.criticalHit(player);
        canBonusDamage = Permissions.axeBonus(player);
        canImpact = Permissions.impact(player);
        canGreaterImpact = Permissions.greaterImpact(player);
        lucky = Permissions.luckyAxes(player);
        endurance = Permissions.activationTwelve(player) || Permissions.activationEight(player) || Permissions.activationFour(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canSkullSplitter || canCritical || canBonusDamage || canImpact || canGreaterImpact;
    }

    @Override
    protected void effectsDisplay() {
        if (lucky) {
            String perkPrefix = LocaleLoader.getString("MOTD.PerksPrefix");
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.lucky.name"), LocaleLoader.getString("Perks.lucky.desc", new Object[] { Skills.localizeSkillName(SkillType.AXES) }) }));
        }

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
            if (lucky)
                player.sendMessage(LocaleLoader.getString("Axes.Combat.CritChance", new Object[] { critChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { critChanceLucky }));
            else
                player.sendMessage(LocaleLoader.getString("Axes.Combat.CritChance", new Object[] { critChance }));
        }

        if (canSkullSplitter) {
            if (endurance)
                player.sendMessage(LocaleLoader.getString("Axes.Combat.SS.Length", new Object[] { skullSplitterLength }) + LocaleLoader.getString("Perks.activationtime.bonus", new Object[] { skullSplitterLengthEndurance }));
            else
                player.sendMessage(LocaleLoader.getString("Axes.Combat.SS.Length", new Object[] { skullSplitterLength }));
        }
    }
}
