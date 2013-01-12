package com.gmail.nossr50.commands.skills;

import org.bukkit.ChatColor;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;

public class UnarmedCommand extends SkillCommand {
    AdvancedConfig advancedConfig = AdvancedConfig.getInstance();
    private String berserkLength;
    private String berserkLengthEndurance;
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
    private boolean lucky;
    private boolean endurance;

    public UnarmedCommand() {
        super(SkillType.UNARMED);
    }

    @Override
    protected void dataCalculations() {
        float disarmChanceF;
        float deflectChanceF;
        //Berserk
        int length = 2 + (int) ((double) skillValue / (double) abilityLengthIncreaseLevel);
        berserkLength = String.valueOf(length);

        if (Permissions.activationTwelve(player)) {
            length = length + 12;
        }
        else if (Permissions.activationEight(player)) {
            length = length + 8;
        }
        else if (Permissions.activationFour(player)) {
            length = length + 4;
        }
        int maxLength = SkillType.UNARMED.getAbility().getMaxTicks();
        if (maxLength != 0 && length > maxLength) {
            length = maxLength;
        }
        berserkLengthEndurance = String.valueOf(length);

        //Disarm
        if (skillValue >= disarmMaxLevel) disarmChanceF = disarmChanceMax;
        else disarmChanceF = (float) (((double) disarmChanceMax / (double) disarmMaxLevel) * skillValue);
        disarmChance = percent.format(disarmChanceF / 100D);
        if (disarmChanceF * 1.3333D >= 100D) disarmChanceLucky = percent.format(1D);
        else disarmChanceLucky = percent.format(disarmChanceF * 1.3333D / 100D);

        //Deflect
        if (skillValue >= deflectMaxLevel) deflectChanceF = deflectChanceMax;
        else deflectChanceF = (float) (((double) deflectChanceMax / (double) deflectMaxLevel) * skillValue);
        deflectChance = percent.format(deflectChanceF / 100D);
        if (deflectChanceF * 1.3333D >= 100D) deflectChanceLucky = percent.format(1D);
        else deflectChanceLucky = percent.format(deflectChanceF * 1.3333D / 100D);

        //Iron Arm
        if (skillValue >= 250) ironArmBonus = String.valueOf(ironArmMaxBonus);
        else ironArmBonus = String.valueOf(3 + ((double) skillValue / (double) ironArmIncreaseLevel));
    }

    @Override
    protected void permissionsCheck() {
        canBerserk = Permissions.berserk(player);
        canBonusDamage = Permissions.unarmedBonus(player);
        canDeflect = Permissions.deflect(player);
        canDisarm = Permissions.disarm(player);
        lucky = Permissions.luckyUnarmed(player);
        endurance = Permissions.activationTwelve(player) || Permissions.activationEight(player) || Permissions.activationFour(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canBerserk || canBonusDamage || canDeflect || canDisarm;
    }

    @Override
    protected void effectsDisplay() {
        if (lucky) {
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
            if (lucky)
                player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Chance.ArrowDeflect", new Object[] { deflectChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { deflectChanceLucky }));
            else
                player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Chance.ArrowDeflect", new Object[] { deflectChance }));
        }

        if (canDisarm) {
            if (lucky)
                player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Chance.Disarm", new Object[] { disarmChance }) + LocaleLoader.getString("Perks.lucky.bonus", new Object[] { disarmChanceLucky }));
            else
                player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Chance.Disarm", new Object[] { disarmChance }));
        }

        if (canBerserk) {
            if (endurance)
                player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Berserk.Length", new Object[] { berserkLength }) + LocaleLoader.getString("Perks.activationtime.bonus", new Object[] { berserkLengthEndurance }));
            else
                player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Berserk.Length", new Object[] { berserkLength }));
        }
    }
}
