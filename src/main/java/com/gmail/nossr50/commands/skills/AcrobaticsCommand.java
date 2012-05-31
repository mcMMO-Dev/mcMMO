package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;

public class AcrobaticsCommand extends SkillCommand {
    private String dodgeChance;
    private String rollChance;
    private String gracefulRollChance;

    private boolean canDodge;
    private boolean canRoll;
    private boolean canGracefulRoll;

    public AcrobaticsCommand() {
        super(SkillType.ACROBATICS);
    }

    @Override
    protected void dataCalculations() {
        if (skillValue >= 1000) {
            dodgeChance = "20.00%";
            rollChance = "100.00%";
            gracefulRollChance = "100.00%";
        }
        else if (skillValue >= 800) {
            dodgeChance = "20.00%";
            rollChance = percent.format(skillValue / 1000);
            gracefulRollChance = "100.00%";
        }
        else if (skillValue >= 500) {
            dodgeChance = percent.format(skillValue / 4000);
            rollChance = percent.format(skillValue / 1000);
            gracefulRollChance = "100.00%";
        }
        else {
            dodgeChance = percent.format(skillValue / 4000);
            rollChance = percent.format(skillValue / 1000);
            gracefulRollChance = percent.format(skillValue / 500);
        }
    }

    @Override
    protected void permissionsCheck() {
        canDodge = permInstance.dodge(player);
        canRoll = permInstance.roll(player);
        canGracefulRoll = permInstance.gracefulRoll(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canDodge || canGracefulRoll || canRoll;
    }

    @Override
    protected void effectsDisplay() {
        if (canRoll) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Acrobatics.Effect.0"), LocaleLoader.getString("Acrobatics.Effect.1") }));
        }

        if (canGracefulRoll) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Acrobatics.Effect.2"), LocaleLoader.getString("Acrobatics.Effect.3") }));
        }

        if (canDodge) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Acrobatics.Effect.4"), LocaleLoader.getString("Acrobatics.Effect.5") }));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canDodge || canGracefulRoll || canRoll;
    }

    @Override
    protected void statsDisplay() {
        if (canRoll) {
            player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.Chance", new Object[] { rollChance }));
        }

        if (canGracefulRoll) {
            player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.GraceChance", new Object[] { gracefulRollChance }));
        }

        if (canDodge) {
            player.sendMessage(LocaleLoader.getString("Acrobatics.DodgeChance", new Object[] { dodgeChance }));
        }
    }
}