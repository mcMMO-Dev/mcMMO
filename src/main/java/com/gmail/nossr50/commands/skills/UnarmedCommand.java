package com.gmail.nossr50.commands.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
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
    private double ironArmBonus;

    private boolean canBerserk;
    private boolean canDisarm;
    private boolean canIronArm;
    private boolean canDeflect;
    private boolean canIronGrip;

    public UnarmedCommand() {
        super(SkillType.UNARMED);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue, boolean isLucky) {
        // BERSERK
        if (canBerserk) {
            String[] berserkStrings = calculateLengthDisplayValues(player, skillValue);
            berserkLength = berserkStrings[0];
            berserkLengthEndurance = berserkStrings[1];
        }

        // DISARM
        if (canDisarm) {
            String[] disarmStrings = calculateAbilityDisplayValues(skillValue, SecondaryAbility.DISARM, isLucky);
            disarmChance = disarmStrings[0];
            disarmChanceLucky = disarmStrings[1];
        }

        // DEFLECT
        if (canDeflect) {
            String[] deflectStrings = calculateAbilityDisplayValues(skillValue, SecondaryAbility.DEFLECT, isLucky);
            deflectChance = deflectStrings[0];
            deflectChanceLucky = deflectStrings[1];
        }

        // IRON ARM
        if (canIronArm) {
            ironArmBonus = Math.min(Unarmed.ironArmMinBonusDamage + ((int) skillValue / Unarmed.ironArmIncreaseLevel), Unarmed.ironArmMaxBonusDamage);
        }

        // IRON GRIP
        if (canIronGrip) {
            String[] ironGripStrings = calculateAbilityDisplayValues(skillValue, SecondaryAbility.IRON_GRIP, isLucky);
            ironGripChance = ironGripStrings[0];
            ironGripChanceLucky = ironGripStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canBerserk = Permissions.berserk(player);
        canIronArm = Permissions.secondaryAbilityEnabled(player, SecondaryAbility.IRON_ARM);
        canDeflect = Permissions.secondaryAbilityEnabled(player, SecondaryAbility.DEFLECT);
        canDisarm = Permissions.secondaryAbilityEnabled(player, SecondaryAbility.DISARM);
        canIronGrip = Permissions.secondaryAbilityEnabled(player, SecondaryAbility.IRON_GRIP);
        // TODO: Apparently we forgot about block cracker?
    }

    @Override
    protected List<String> effectsDisplay() {
        List<String> messages = new ArrayList<String>();

        if (canBerserk) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Unarmed.Effect.0"), LocaleLoader.getString("Unarmed.Effect.1")));
        }
        // TODO: Apparently we forgot about block cracker?

        if (canDisarm) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Unarmed.Effect.2"), LocaleLoader.getString("Unarmed.Effect.3")));
        }

        if (canIronArm) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Unarmed.Effect.4"), LocaleLoader.getString("Unarmed.Effect.5")));
        }

        if (canDeflect) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Unarmed.Effect.6"), LocaleLoader.getString("Unarmed.Effect.7")));
        }

        if (canIronGrip) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Unarmed.Effect.8"), LocaleLoader.getString("Unarmed.Effect.9")));
        }

        return messages;
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<String>();

        if (canIronArm) {
            messages.add(LocaleLoader.getString("Ability.Generic.Template", LocaleLoader.getString("Unarmed.Ability.Bonus.0"), LocaleLoader.getString("Unarmed.Ability.Bonus.1", ironArmBonus)));
        }

        if (canDeflect) {
            messages.add(LocaleLoader.getString("Unarmed.Ability.Chance.ArrowDeflect", deflectChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", deflectChanceLucky) : ""));
        }

        if (canDisarm) {
            messages.add(LocaleLoader.getString("Unarmed.Ability.Chance.Disarm", disarmChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", disarmChanceLucky) : ""));
        }

        if (canIronGrip) {
            messages.add(LocaleLoader.getString("Unarmed.Ability.Chance.IronGrip", ironGripChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", ironGripChanceLucky) : ""));
        }

        if (canBerserk) {
            messages.add(LocaleLoader.getString("Unarmed.Ability.Berserk.Length", berserkLength) + (hasEndurance ? LocaleLoader.getString("Perks.ActivationTime.Bonus", berserkLengthEndurance) : ""));
        }

        return messages;
    }
}
