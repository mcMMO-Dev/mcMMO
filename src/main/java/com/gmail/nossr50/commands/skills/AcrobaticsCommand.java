package com.gmail.nossr50.commands.skills;

import org.bukkit.ChatColor;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.acrobatics.Acrobatics;
import com.gmail.nossr50.util.Permissions;

public class AcrobaticsCommand extends SkillCommand {
    AdvancedConfig advancedConfig = AdvancedConfig.getInstance();

    private String dodgeChance;
    private String dodgeChanceLucky;
    private String rollChance;
    private String rollChanceLucky;
    private String gracefulRollChance;
    private String gracefulRollChanceLucky;

    private boolean canDodge;
    private boolean canRoll;
    private boolean canGracefulRoll;
    private boolean lucky;

    public AcrobaticsCommand() {
        super(SkillType.ACROBATICS);
    }

    @Override
    protected void dataCalculations() {
        float dodgeChanceF;
        float rollChanceF;
        float gracefulRollChanceF;

        // DODGE
        if (skillValue >= Acrobatics.dodgeMaxBonusLevel) dodgeChanceF = (float) Acrobatics.dodgeMaxChance;
        else dodgeChanceF = (float) ((Acrobatics.dodgeMaxChance / Acrobatics.dodgeMaxBonusLevel) * skillValue);
        dodgeChance = percent.format(dodgeChanceF / 100D);
        if (dodgeChanceF * 1.3333D >= 100D) dodgeChanceLucky = percent.format(1D);
        else dodgeChanceLucky = percent.format(dodgeChanceF * 1.3333D / 100D);

        // ROLL
        if (skillValue >= Acrobatics.rollMaxBonusLevel) rollChanceF = (float) Acrobatics.rollMaxChance;
        else rollChanceF = (float) ((Acrobatics.rollMaxChance / Acrobatics.rollMaxBonusLevel) * skillValue);
        rollChance = percent.format(rollChanceF / 100D);
        if (rollChanceF * 1.3333D >= 100D) rollChanceLucky = percent.format(1D);
        else rollChanceLucky = percent.format(rollChanceF * 1.3333D / 100D);

        // GRACEFULROLL
        if (skillValue >= Acrobatics.gracefulRollMaxBonusLevel) gracefulRollChanceF = (float) Acrobatics.gracefulRollMaxChance;
        else gracefulRollChanceF = (float) ((Acrobatics.gracefulRollMaxChance / Acrobatics.gracefulRollMaxBonusLevel) * skillValue);
        gracefulRollChance = percent.format(gracefulRollChanceF / 100D);
        if (gracefulRollChanceF * 1.3333D >= 100D) gracefulRollChanceLucky = percent.format(1D);
        else gracefulRollChanceLucky = percent.format(gracefulRollChanceF * 1.3333D / 100D);
    }

    @Override
    protected void permissionsCheck() {
        canDodge = Permissions.dodge(player);
        canRoll = Permissions.roll(player);
        canGracefulRoll = Permissions.gracefulRoll(player);
        lucky = Permissions.luckyAcrobatics(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canDodge || canGracefulRoll || canRoll;
    }

    @Override
    protected void effectsDisplay() {
        if (lucky) {
            String perkPrefix = ChatColor.RED + "[mcMMO Perks] ";
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.lucky.name"), LocaleLoader.getString("Perks.lucky.desc", new Object[] { "Acrobatics" }) }));
        }

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
            if (lucky)
                player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.Chance", new Object[] { rollChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { rollChanceLucky }));
        	else
                player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.Chance", new Object[] { rollChance }));
        }

        if (canGracefulRoll) {
            if (lucky)
                player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.GraceChance", new Object[] { gracefulRollChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { gracefulRollChanceLucky }));
        	else
                player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.GraceChance", new Object[] { gracefulRollChance }));
        }

        if (canDodge) {
            if (lucky)
                player.sendMessage(LocaleLoader.getString("Acrobatics.DodgeChance", new Object[] { dodgeChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { dodgeChanceLucky }));
        	else
                player.sendMessage(LocaleLoader.getString("Acrobatics.DodgeChance", new Object[] { dodgeChance }));
        }
    }
}