package com.gmail.nossr50.commands.skills;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;

public class AcrobaticsCommand extends SkillCommand {
    AdvancedConfig advancedConfig = AdvancedConfig.getInstance();

    private String dodgeChance;
    private String dodgeChanceLucky;
    private String rollChance;
    private String rollChanceLucky;
    private String gracefulRollChance;
    private String gracefulRollChanceLucky;

    private float dodgeChanceMax = advancedConfig.getDodgeChanceMax();
    private float dodgeMaxBonusLevel = advancedConfig.getDodgeMaxBonusLevel();
    private float rollChanceMax = advancedConfig.getRollChanceMax();
    private float rollMaxBonusLevel = advancedConfig.getRollMaxBonusLevel();
    private float gracefulRollChanceMax = advancedConfig.getGracefulRollChanceMax();
    private float gracefulRollMaxBonusLevel = advancedConfig.getGracefulRollMaxBonusLevel();

    private boolean canDodge;
    private boolean canRoll;
    private boolean canGracefulRoll;
    private boolean lucky;

    public AcrobaticsCommand() {
        super(SkillType.ACROBATICS);
    }

    @Override
    protected void dataCalculations() {
        DecimalFormat df = new DecimalFormat("0.0");
        float dodgeChanceF;
        float rollChanceF;
        float gracefulRollChanceF;

        // DODGE
        if(skillValue >= dodgeMaxBonusLevel) dodgeChanceF = dodgeChanceMax;
        else dodgeChanceF = (float) (((double) dodgeChanceMax / (double) dodgeMaxBonusLevel) * skillValue);
        dodgeChance = df.format(dodgeChanceF);
        if(dodgeChanceF + dodgeChanceF * 0.3333D >= 100D) dodgeChanceLucky = df.format(100D);
        else dodgeChanceLucky = df.format(dodgeChanceF + dodgeChanceF * 0.3333D);

        // ROLL
        if(skillValue >= rollMaxBonusLevel) rollChanceF = rollChanceMax;
        else rollChanceF = (float) (((double) rollChanceMax / (double) rollMaxBonusLevel) * skillValue);
        rollChance = df.format(rollChanceF);
        if(rollChanceF + rollChanceF * 0.3333D >= 100D) rollChanceLucky = df.format(100D);
        else rollChanceLucky = df.format(rollChanceF + rollChanceF * 0.3333D);

        // GRACEFULROLL
        if(skillValue >= gracefulRollMaxBonusLevel) gracefulRollChanceF = gracefulRollChanceMax;
        else gracefulRollChanceF = (float) (((double) gracefulRollChanceMax / (double) gracefulRollMaxBonusLevel) * skillValue);
        gracefulRollChance = df.format(gracefulRollChanceF);
        if(gracefulRollChanceF + gracefulRollChanceF * 0.3333D >= 100D) gracefulRollChanceLucky = df.format(100D);
        else gracefulRollChanceLucky = df.format(gracefulRollChanceF + gracefulRollChanceF * 0.3333D);
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
        	if (player.hasPermission("mcmmo.perks.lucky.acrobatics"))
                player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.Chance", new Object[] { rollChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { rollChanceLucky }));
        	else
                player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.Chance", new Object[] { rollChance }));
        }

        if (canGracefulRoll) {
        	if (player.hasPermission("mcmmo.perks.lucky.acrobatics"))
                player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.GraceChance", new Object[] { gracefulRollChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { gracefulRollChanceLucky }));
        	else
                player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.GraceChance", new Object[] { gracefulRollChance }));
        }

        if (canDodge) {
        	if (player.hasPermission("mcmmo.perks.lucky.acrobatics"))
                player.sendMessage(LocaleLoader.getString("Acrobatics.DodgeChance", new Object[] { dodgeChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { dodgeChanceLucky }));
        	else
                player.sendMessage(LocaleLoader.getString("Acrobatics.DodgeChance", new Object[] { dodgeChance }));
        }
    }
}