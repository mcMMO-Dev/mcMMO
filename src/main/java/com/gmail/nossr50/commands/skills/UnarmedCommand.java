package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.unarmed.Unarmed;
import com.gmail.nossr50.util.Permissions;

public class UnarmedCommand extends SkillCommand {
    private String berserkLength;
    private String berserkLengthEndurance;
    private String deflectChance;
    private String deflectChanceLucky;
    private String disarmChance;
    private String disarmChanceLucky;
    private String ironGripChance;
    private String ironGripChanceLucky;
    private String ironArmBonus;

    private boolean canBerserk;
    private boolean canDisarm;
    private boolean canBonusDamage;
    private boolean canDeflect;
    private boolean canIronGrip;

    public UnarmedCommand() {
        super(SkillType.UNARMED);
    }

    @Override
    protected void dataCalculations() {
        // BERSERK
        String[] berserkStrings = calculateLengthDisplayValues();
        berserkLength = berserkStrings[0];
        berserkLengthEndurance = berserkStrings[1];

        // DISARM
        String[] disarmStrings = calculateAbilityDisplayValues(Unarmed.disarmMaxBonusLevel, Unarmed.disarmMaxChance);
        disarmChance = disarmStrings[0];
        disarmChanceLucky = disarmStrings[1];

        // DEFLECT
        String[] deflectStrings = calculateAbilityDisplayValues(Unarmed.deflectMaxBonusLevel, Unarmed.deflectMaxChance);
        deflectChance = deflectStrings[0];
        deflectChanceLucky = deflectStrings[1];

        // IRON ARM
        if (skillValue >= ((Unarmed.ironArmMaxBonusDamage - 3) * Unarmed.ironArmIncreaseLevel)) {
            ironArmBonus = String.valueOf(Unarmed.ironArmMaxBonusDamage);
        }
        else {
            ironArmBonus = String.valueOf(3 + (skillValue / Unarmed.ironArmIncreaseLevel));
        }

        // IRON GRIP
        String[] ironGripStrings = calculateAbilityDisplayValues(Unarmed.ironGripMaxBonusLevel, Unarmed.ironGripMaxChance);
        ironGripChance = ironGripStrings[0];
        ironGripChanceLucky = ironGripStrings[1];
    }

    @Override
    protected void permissionsCheck() {
        canBerserk = Permissions.berserk(player);
        canBonusDamage = Permissions.bonusDamage(player, skill);
        canDeflect = Permissions.arrowDeflect(player);
        canDisarm = Permissions.disarm(player);
        canIronGrip = Permissions.ironGrip(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canBerserk || canBonusDamage || canDeflect || canDisarm || canIronGrip;
    }

    @Override
    protected void effectsDisplay() {
        luckyEffectsDisplay();

        if (canBerserk) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Unarmed.Effect.0"), LocaleLoader.getString("Unarmed.Effect.1")));
        }

        if (canDisarm) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Unarmed.Effect.2"), LocaleLoader.getString("Unarmed.Effect.3")));
        }

        if (canBonusDamage) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Unarmed.Effect.4"), LocaleLoader.getString("Unarmed.Effect.5")));
        }

        if (canDeflect) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Unarmed.Effect.6"), LocaleLoader.getString("Unarmed.Effect.7")));
        }

        if (canIronGrip) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Unarmed.Effect.8"), LocaleLoader.getString("Unarmed.Effect.9")));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canBerserk || canBonusDamage || canDeflect || canDisarm || canIronGrip;
    }

    @Override
    protected void statsDisplay() {
        if (canBonusDamage) {
            player.sendMessage(LocaleLoader.getString("Ability.Generic.Template", LocaleLoader.getString("Unarmed.Ability.Bonus.0"), LocaleLoader.getString("Unarmed.Ability.Bonus.1", ironArmBonus)));
        }

        if (canDeflect) {
            if (isLucky) {
                player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Chance.ArrowDeflect", deflectChance) + LocaleLoader.getString("Perks.lucky.bonus", deflectChanceLucky));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Chance.ArrowDeflect", deflectChance));
            }
        }

        if (canDisarm) {
            if (isLucky) {
                player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Chance.Disarm", disarmChance) + LocaleLoader.getString("Perks.lucky.bonus", disarmChanceLucky));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Chance.Disarm", disarmChance));
            }
        }

        if (canIronGrip) {
            if (isLucky) {
                player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Chance.IronGrip", ironGripChance) + LocaleLoader.getString("Perks.lucky.bonus", ironGripChanceLucky));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Chance.IronGrip", ironGripChance));
            }
        }

        if (canBerserk) {
            if (hasEndurance) {
                player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Berserk.Length", berserkLength) + LocaleLoader.getString("Perks.activationtime.bonus", berserkLengthEndurance));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Unarmed.Ability.Berserk.Length", berserkLength));
            }
        }
    }
}
