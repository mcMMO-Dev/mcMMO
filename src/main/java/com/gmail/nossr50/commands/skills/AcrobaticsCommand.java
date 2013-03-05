package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.acrobatics.Acrobatics;
import com.gmail.nossr50.util.Permissions;

public class AcrobaticsCommand extends SkillCommand {
    private String dodgeChance;
    private String dodgeChanceLucky;
    private String rollChance;
    private String rollChanceLucky;
    private String gracefulRollChance;
    private String gracefulRollChanceLucky;

    private boolean canDodge;
    private boolean canRoll;
    private boolean canGracefulRoll;

    public AcrobaticsCommand() {
        super(SkillType.ACROBATICS);
    }

    @Override
    protected void dataCalculations() {
        // DODGE
        String[] dodgeStrings = calculateAbilityDisplayValues(Acrobatics.dodgeMaxBonusLevel, Acrobatics.dodgeMaxChance);
        dodgeChance = dodgeStrings[0];
        dodgeChanceLucky = dodgeStrings[1];

        // ROLL
        String[] rollStrings = calculateAbilityDisplayValues(Acrobatics.rollMaxBonusLevel, Acrobatics.rollMaxChance);
        rollChance = rollStrings[0];
        rollChanceLucky = rollStrings[1];

        // GRACEFUL ROLL
        String[] gracefulRollStrings = calculateAbilityDisplayValues(Acrobatics.gracefulRollMaxBonusLevel, Acrobatics.gracefulRollMaxChance);
        gracefulRollChance = gracefulRollStrings[0];
        gracefulRollChanceLucky = gracefulRollStrings[1];
    }

    @Override
    protected void permissionsCheck() {
        canDodge = Permissions.dodge(player);
        canRoll = Permissions.roll(player);
        canGracefulRoll = Permissions.gracefulRoll(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canDodge || canGracefulRoll || canRoll;
    }

    @Override
    protected void effectsDisplay() {
        luckyEffectsDisplay();

        if (canRoll) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Acrobatics.Effect.0"), LocaleLoader.getString("Acrobatics.Effect.1")));
        }

        if (canGracefulRoll) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Acrobatics.Effect.2"), LocaleLoader.getString("Acrobatics.Effect.3")));
        }

        if (canDodge) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Acrobatics.Effect.4"), LocaleLoader.getString("Acrobatics.Effect.5")));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canDodge || canGracefulRoll || canRoll;
    }

    @Override
    protected void statsDisplay() {
        if (canRoll) {
            if (isLucky) {
                player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.Chance", new Object[] { rollChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { rollChanceLucky }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.Chance", new Object[] { rollChance }));
            }
        }

        if (canGracefulRoll) {
            if (isLucky) {
                player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.GraceChance", new Object[] { gracefulRollChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { gracefulRollChanceLucky }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.GraceChance", new Object[] { gracefulRollChance }));
            }
        }

        if (canDodge) {
            if (isLucky) {
                player.sendMessage(LocaleLoader.getString("Acrobatics.DodgeChance", new Object[] { dodgeChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { dodgeChanceLucky }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Acrobatics.DodgeChance", new Object[] { dodgeChance }));
            }
        }
    }
}
