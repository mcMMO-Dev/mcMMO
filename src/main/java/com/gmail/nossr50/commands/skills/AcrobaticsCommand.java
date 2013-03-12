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
        if (canDodge) {
            String[] dodgeStrings = calculateAbilityDisplayValues(Acrobatics.dodgeMaxBonusLevel, Acrobatics.dodgeMaxChance);
            dodgeChance = dodgeStrings[0];
            dodgeChanceLucky = dodgeStrings[1];
        }

        // ROLL
        if (canRoll) {
            String[] rollStrings = calculateAbilityDisplayValues(Acrobatics.rollMaxBonusLevel, Acrobatics.rollMaxChance);
            rollChance = rollStrings[0];
            rollChanceLucky = rollStrings[1];
        }

        // GRACEFUL ROLL
        if (canGracefulRoll) {
            String[] gracefulRollStrings = calculateAbilityDisplayValues(Acrobatics.gracefulRollMaxBonusLevel, Acrobatics.gracefulRollMaxChance);
            gracefulRollChance = gracefulRollStrings[0];
            gracefulRollChanceLucky = gracefulRollStrings[1];
        }
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
            player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.Chance", rollChance) + (isLucky ? LocaleLoader.getString("Perks.lucky.bonus", rollChanceLucky) : ""));
        }

        if (canGracefulRoll) {
            player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.GraceChance", gracefulRollChance) + (isLucky ? LocaleLoader.getString("Perks.lucky.bonus", gracefulRollChanceLucky) : ""));
        }

        if (canDodge) {
            player.sendMessage(LocaleLoader.getString("Acrobatics.DodgeChance", dodgeChance) + (isLucky ? LocaleLoader.getString("Perks.lucky.bonus", dodgeChanceLucky) : ""));
        }
    }
}
