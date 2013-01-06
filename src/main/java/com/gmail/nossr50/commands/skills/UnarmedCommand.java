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
    private String deflectChanceLucky;
    private String disarmChance;
    private String disarmChanceLucky;
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
        float disarmChanceF;
        float deflectChanceF;
        berserkLength = String.valueOf(2 + (int) ((double) skillValue / (double) abilityLengthIncreaseLevel));

        if(skillValue >= disarmMaxLevel) disarmChanceF = disarmChanceMax;
        else disarmChanceF = (float) (((double) disarmChanceMax / (double) disarmMaxLevel) * skillValue);
        disarmChance = df.format(disarmChanceF);
        if(disarmChanceF + disarmChanceF * 0.3333D >= 100D) disarmChanceLucky = df.format(100D);
        else disarmChanceLucky = df.format(disarmChanceF + disarmChanceF * 0.3333D);

        if(skillValue >= deflectMaxLevel) deflectChanceF = deflectChanceMax;
        else deflectChanceF = (float) (((double) deflectChanceMax / (double) deflectMaxLevel) * skillValue);
        deflectChance = df.format(deflectChanceF);
        if(deflectChanceF + deflectChanceF * 0.3333D >= 100D) deflectChanceLucky = df.format(100D);
        else deflectChanceLucky = df.format(deflectChanceF + deflectChanceF * 0.3333D);

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
            if (player.hasPermission("mcmmo.perks.lucky.unarmed"))
                player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Chance.ArrowDeflect", new Object[] { deflectChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { deflectChanceLucky }));
            else
                player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Chance.ArrowDeflect", new Object[] { deflectChance }));
        }

        if (canDisarm) {
            if (player.hasPermission("mcmmo.perks.lucky.unarmed"))
                player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Chance.Disarm", new Object[] { disarmChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { disarmChanceLucky }));
            else
                player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Chance.Disarm", new Object[] { disarmChance }));
        }

        if (canBerserk) {
            player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Berserk.Length", new Object[] { berserkLength }));
        }
    }
}
