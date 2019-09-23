package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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

    public UnarmedCommand(mcMMO pluginRef) {
        super(PrimarySkillType.UNARMED, pluginRef);
    }

    @Override
    protected void dataCalculations(Player player, double skillValue) {
        // UNARMED_ARROW_DEFLECT
        if (canDeflect) {
            String[] deflectStrings = getAbilityDisplayValues(player, SubSkillType.UNARMED_ARROW_DEFLECT);
            deflectChance = deflectStrings[0];
            deflectChanceLucky = deflectStrings[1];
        }

        // BERSERK
        if (canBerserk) {
            String[] berserkStrings = formatLengthDisplayValues(player, skillValue);
            berserkLength = berserkStrings[0];
            berserkLengthEndurance = berserkStrings[1];
        }

        // UNARMED_DISARM
        if (canDisarm) {
            String[] disarmStrings = getAbilityDisplayValues(player, SubSkillType.UNARMED_DISARM);
            disarmChance = disarmStrings[0];
            disarmChanceLucky = disarmStrings[1];
        }

        // IRON ARM
        if (canIronArm) {
            ironArmBonus = pluginRef.getUserManager().getPlayer(player).getUnarmedManager().getIronArmDamage();
        }

        // IRON GRIP
        if (canIronGrip) {
            String[] ironGripStrings = getAbilityDisplayValues(player, SubSkillType.UNARMED_IRON_GRIP);
            ironGripChance = ironGripStrings[0];
            ironGripChanceLucky = ironGripStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canBerserk = pluginRef.getRankTools().hasUnlockedSubskill(player, SubSkillType.UNARMED_BERSERK) && pluginRef.getPermissionTools().berserk(player);
        canIronArm = canUseSubSkill(player, SubSkillType.UNARMED_IRON_ARM_STYLE);
        canDeflect = canUseSubSkill(player, SubSkillType.UNARMED_ARROW_DEFLECT);
        canDisarm = canUseSubSkill(player, SubSkillType.UNARMED_DISARM);
        canIronGrip = canUseSubSkill(player, SubSkillType.UNARMED_IRON_GRIP);
        // TODO: Apparently we forgot about block cracker?
    }

    @Override
    protected List<String> statsDisplay(Player player, double skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<>();

        if (canDeflect) {
            messages.add(getStatMessage(SubSkillType.UNARMED_ARROW_DEFLECT, deflectChance)
                    + (isLucky ? pluginRef.getLocaleManager().getString("Perks.Lucky.Bonus", deflectChanceLucky) : ""));
            //messages.add(pluginRef.getLocaleManager().getString("Unarmed.Ability.Chance.ArrowDeflect", deflectChance) + (isLucky ? pluginRef.getLocaleManager().getString("Perks.Lucky.Bonus", deflectChanceLucky) : ""));
        }

        if (canBerserk) {
            messages.add(getStatMessage(SubSkillType.UNARMED_BERSERK, berserkLength)
                    + (hasEndurance ? pluginRef.getLocaleManager().getString("Perks.ActivationTime.Bonus", berserkLengthEndurance) : ""));
            //messages.add(pluginRef.getLocaleManager().getString("Unarmed.Ability.Berserk.Length", berserkLength) + (hasEndurance ? pluginRef.getLocaleManager().getString("Perks.ActivationTime.Bonus", berserkLengthEndurance) : ""));
        }

        if (canDisarm) {
            messages.add(getStatMessage(SubSkillType.UNARMED_DISARM, disarmChance)
                    + (isLucky ? pluginRef.getLocaleManager().getString("Perks.Lucky.Bonus", disarmChanceLucky) : ""));
            //messages.add(pluginRef.getLocaleManager().getString("Unarmed.Ability.Chance.Disarm", disarmChance) + (isLucky ? pluginRef.getLocaleManager().getString("Perks.Lucky.Bonus", disarmChanceLucky) : ""));
        }

        if (canIronArm) {
            messages.add(pluginRef.getLocaleManager().getString("Ability.Generic.Template", pluginRef.getLocaleManager().getString("Unarmed.Ability.Bonus.0"), pluginRef.getLocaleManager().getString("Unarmed.Ability.Bonus.1", ironArmBonus)));
        }

        if (canIronGrip) {
            messages.add(getStatMessage(SubSkillType.UNARMED_IRON_GRIP, ironGripChance)
                    + (isLucky ? pluginRef.getLocaleManager().getString("Perks.Lucky.Bonus", ironGripChanceLucky) : ""));
            //messages.add(pluginRef.getLocaleManager().getString("Unarmed.Ability.Chance.IronGrip", ironGripChance) + (isLucky ? pluginRef.getLocaleManager().getString("Perks.Lucky.Bonus", ironGripChanceLucky) : ""));
        }

        if (canUseSubSkill(player, SubSkillType.UNARMED_UNARMED_LIMIT_BREAK)) {
            messages.add(getStatMessage(SubSkillType.UNARMED_UNARMED_LIMIT_BREAK,
<<<<<<< HEAD
                    String.valueOf(pluginRef.getCombatTools().getLimitBreakDamage(player, SubSkillType.UNARMED_UNARMED_LIMIT_BREAK))));
=======
                    String.valueOf(CombatUtils.getLimitBreakDamageAgainstQuality(player, SubSkillType.UNARMED_UNARMED_LIMIT_BREAK, 1000))));
>>>>>>> 308e3a4b1f46e9e3de28d6d540dd055a540ed4d5
        }

        return messages;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        List<TextComponent> textComponents = new ArrayList<>();

        pluginRef.getTextComponentFactory().getSubSkillTextComponents(player, textComponents, PrimarySkillType.UNARMED);

        return textComponents;
    }
}
