package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.text.TextComponentFactory;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

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
        super(PrimarySkillType.UNARMED);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue) {
        // UNARMED_ARROW_DEFLECT
        if (canDeflect) {
            String[] deflectStrings = ProbabilityUtil.getRNGDisplayValues(mmoPlayer,
                    SubSkillType.UNARMED_ARROW_DEFLECT);
            deflectChance = deflectStrings[0];
            deflectChanceLucky = deflectStrings[1];
        }

        // BERSERK
        if (canBerserk) {
            String[] berserkStrings = calculateLengthDisplayValues(player, skillValue);
            berserkLength = berserkStrings[0];
            berserkLengthEndurance = berserkStrings[1];
        }

        // UNARMED_DISARM
        if (canDisarm) {
            String[] disarmStrings = ProbabilityUtil.getRNGDisplayValues(mmoPlayer,
                    SubSkillType.UNARMED_DISARM);
            disarmChance = disarmStrings[0];
            disarmChanceLucky = disarmStrings[1];
        }

        // IRON ARM
        if (canIronArm) {
            ironArmBonus = mmoPlayer.getUnarmedManager().getSteelArmStyleDamage();
        }

        // IRON GRIP
        if (canIronGrip) {
            String[] ironGripStrings = ProbabilityUtil.getRNGDisplayValues(mmoPlayer,
                    SubSkillType.UNARMED_IRON_GRIP);
            ironGripChance = ironGripStrings[0];
            ironGripChanceLucky = ironGripStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canBerserk = RankUtils.hasUnlockedSubskill(player, SubSkillType.UNARMED_BERSERK)
                && Permissions.berserk(player);
        canIronArm = Permissions.canUseSubSkill(player, SubSkillType.UNARMED_STEEL_ARM_STYLE);
        canDeflect = Permissions.canUseSubSkill(player, SubSkillType.UNARMED_ARROW_DEFLECT);
        canDisarm = Permissions.canUseSubSkill(player, SubSkillType.UNARMED_DISARM);
        canIronGrip = Permissions.canUseSubSkill(player, SubSkillType.UNARMED_IRON_GRIP);
        // TODO: Apparently we forgot about block cracker?
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance,
            boolean isLucky) {
        List<String> messages = new ArrayList<>();

        if (canDeflect) {
            messages.add(getStatMessage(SubSkillType.UNARMED_ARROW_DEFLECT, deflectChance)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", deflectChanceLucky)
                    : ""));
            //messages.add(LocaleLoader.getString("Unarmed.Ability.Chance.ArrowDeflect", deflectChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", deflectChanceLucky) : ""));
        }

        if (canBerserk) {
            messages.add(getStatMessage(SubSkillType.UNARMED_BERSERK, berserkLength)
                    + (hasEndurance ? LocaleLoader.getString("Perks.ActivationTime.Bonus",
                    berserkLengthEndurance) : ""));
            //messages.add(LocaleLoader.getString("Unarmed.Ability.Berserk.Length", berserkLength) + (hasEndurance ? LocaleLoader.getString("Perks.ActivationTime.Bonus", berserkLengthEndurance) : ""));
        }

        if (canDisarm) {
            messages.add(getStatMessage(SubSkillType.UNARMED_DISARM, disarmChance)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", disarmChanceLucky)
                    : ""));
            //messages.add(LocaleLoader.getString("Unarmed.Ability.Chance.Disarm", disarmChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", disarmChanceLucky) : ""));
        }

        if (canIronArm) {
            messages.add(LocaleLoader.getString("Ability.Generic.Template",
                    LocaleLoader.getString("Unarmed.Ability.Bonus.0"),
                    LocaleLoader.getString("Unarmed.Ability.Bonus.1", ironArmBonus)));
        }

        if (canIronGrip) {
            messages.add(getStatMessage(SubSkillType.UNARMED_IRON_GRIP, ironGripChance)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", ironGripChanceLucky)
                    : ""));
            //messages.add(LocaleLoader.getString("Unarmed.Ability.Chance.IronGrip", ironGripChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", ironGripChanceLucky) : ""));
        }

        if (Permissions.canUseSubSkill(player, SubSkillType.UNARMED_UNARMED_LIMIT_BREAK)) {
            messages.add(getStatMessage(SubSkillType.UNARMED_UNARMED_LIMIT_BREAK,
                    String.valueOf(CombatUtils.getLimitBreakDamageAgainstQuality(player,
                            SubSkillType.UNARMED_UNARMED_LIMIT_BREAK, 1000))));
        }

        return messages;
    }

    @Override
    protected List<Component> getTextComponents(Player player) {
        List<Component> textComponents = new ArrayList<>();

        TextComponentFactory.appendSubSkillTextComponents(player, textComponents,
                PrimarySkillType.UNARMED);

        return textComponents;
    }
}
