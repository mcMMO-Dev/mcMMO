package com.gmail.nossr50.commands.skills;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;

public class AcrobaticsCommand extends SkillCommand {
    AdvancedConfig advancedConfig = AdvancedConfig.getInstance();

    private String dodgeChance;
    private String rollChance;
    private String gracefulRollChance;

    private float dodgeChanceMax = advancedConfig.getDodgeChanceMax();
    private float dodgeMaxBonusLevel = advancedConfig.getDodgeMaxBonusLevel();
    private float rollChanceMax = advancedConfig.getRollChanceMax();
    private float rollMaxBonusLevel = advancedConfig.getRollMaxBonusLevel();
    private float gracefulRollChanceMax = advancedConfig.getGracefulRollChanceMax();
    private float gracefulRollMaxBonusLevel = advancedConfig.getGracefulRollMaxBonusLevel();

    private boolean canDodge;
    private boolean canRoll;
    private boolean canGracefulRoll;

    public AcrobaticsCommand() {
        super(SkillType.ACROBATICS);
    }

    @Override
    protected void dataCalculations() {
        DecimalFormat df = new DecimalFormat("#.0");
        // DODGE
        if(skillValue >= dodgeMaxBonusLevel) dodgeChance = df.format(dodgeChanceMax);
        else dodgeChance = df.format(((double) dodgeChanceMax / (double) dodgeMaxBonusLevel) * (double) skillValue);
        // ROLL
        if(skillValue >= rollMaxBonusLevel) rollChance = df.format(rollChanceMax);
        else rollChance = df.format(((double) rollChanceMax / (double) rollMaxBonusLevel) * (double) skillValue);            
        // GRACEFULROLL
        if(skillValue >= gracefulRollMaxBonusLevel) gracefulRollChance = df.format(gracefulRollChanceMax);
        else gracefulRollChance = df.format(((double) gracefulRollChanceMax / (double) gracefulRollMaxBonusLevel) * (double) skillValue);
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
                if (player.hasPermission("mcmmo.perks.lucky.acrobatics")) {
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