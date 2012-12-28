package com.gmail.nossr50.commands.skills;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;

public class UnarmedCommand extends SkillCommand {
    AdvancedConfig advancedConfig = AdvancedConfig.getInstance();
    private String berserkLength;
    private String deflectChance;
    private String disarmChance;
    private String ironArmBonus;

    private float disarmChanceMax = advancedConfig.getDisarmChanceMax();
    private float disarmMaxLevel = advancedConfig.getDisarmMaxBonusLevel();
    private float deflectChanceMax = advancedConfig.getDeflectChanceMax();
    private float deflectMaxLevel = advancedConfig.getDeflectMaxBonusLevel();
    private float ironArmMaxBonus = advancedConfig.getIronArmBonus();
    private int ironArmIncreaseLevel = advancedConfig.getIronArmIncreaseLevel();
    private int abilityLengthIncreaseLevel = advancedConfig.getAbilityLength();

    private boolean canBerserk;
    private boolean canDisarm;
    private boolean canBonusDamage;
    private boolean canDeflect;

    public UnarmedCommand() {
        super(SkillType.UNARMED);
    }

    @Override
    protected void dataCalculations() {
        DecimalFormat df = new DecimalFormat("0.0");
        berserkLength = String.valueOf(2 + (int) ((double) skillValue / (double) abilityLengthIncreaseLevel));

        if(skillValue >= disarmMaxLevel) disarmChance = df.format(disarmChanceMax);
        else disarmChance = df.format(((double) disarmChanceMax / (double) disarmMaxLevel) * skillValue);

        if(skillValue >= deflectMaxLevel) deflectChance = df.format(deflectChanceMax);
        else deflectChance = df.format(((double) deflectChanceMax / (double) deflectMaxLevel) * skillValue);

        if (skillValue >= 250) ironArmBonus = String.valueOf(ironArmMaxBonus);
        else ironArmBonus = String.valueOf(3 + ((double) skillValue / (double) ironArmIncreaseLevel));
    }

    @Override
    protected void permissionsCheck() {
        canBerserk = permInstance.berserk(player);
        canBonusDamage = permInstance.unarmedBonus(player);
        canDeflect = permInstance.deflect(player);
        canDisarm = permInstance.disarm(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canBerserk || canBonusDamage || canDeflect || canDisarm;
    }

    @Override
    protected void effectsDisplay() {
        if (player.hasPermission("mcmmo.perks.lucky.unarmed")) {
            String perkPrefix = ChatColor.RED + "[mcMMO Perks] ";
            player.sendMessage(perkPrefix + LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Perks.lucky.name"), LocaleLoader.getString("Perks.lucky.desc", new Object[] { "Unarmed" }) }));
        }

        if (canBerserk) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Unarmed.Effect.0"), LocaleLoader.getString("Unarmed.Effect.1") }));
        }

        if (canDisarm) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Unarmed.Effect.2"), LocaleLoader.getString("Unarmed.Effect.3") }));
        }

        if (canBonusDamage) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Unarmed.Effect.4"), LocaleLoader.getString("Unarmed.Effect.5") }));
        }

        if (canDeflect) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Unarmed.Effect.6"), LocaleLoader.getString("Unarmed.Effect.7") }));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canBerserk || canBonusDamage || canDeflect || canDisarm;
    }

    @Override
    protected void statsDisplay() {
        if (canBonusDamage) {
            player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", new Object[] { LocaleLoader.getString("Unarmed.Ability.Bonus.0"), LocaleLoader.getString("Unarmed.Ability.Bonus.1", new Object[] {ironArmBonus}) }));
        }

        if (canDeflect) {
            player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Chance.ArrowDeflect", new Object[] { deflectChance }));
        }

        if (canDisarm) {
            player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Chance.Disarm", new Object[] { disarmChance }));
        }

        if (canBerserk) {
            player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Berserk.Length", new Object[] { berserkLength }));
        }
    }
}
