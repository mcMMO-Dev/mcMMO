package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.listeners.InteractionManager;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.random.RandomChanceSkill;
import com.gmail.nossr50.util.random.RandomChanceUtil;
import com.gmail.nossr50.util.skills.SkillActivationType;
import com.gmail.nossr50.util.text.TextComponentFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AcrobaticsCommand extends SkillCommand {
    private String dodgeChance;
    private String dodgeChanceLucky;

    private boolean canDodge;
    private boolean canRoll;

    public AcrobaticsCommand() {
        super(PrimarySkillType.ACROBATICS);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue) {
        // ACROBATICS_DODGE
        if (canDodge) {
            String[] dodgeStrings = getAbilityDisplayValues(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, player, SubSkillType.ACROBATICS_DODGE);
            dodgeChance = dodgeStrings[0];
            dodgeChanceLucky = dodgeStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canDodge = canUseSubskill(player, SubSkillType.ACROBATICS_DODGE);
        canRoll = canUseSubskill(player, SubSkillType.ACROBATICS_ROLL);
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<>();

        if (canDodge) {
            messages.add(getStatMessage(SubSkillType.ACROBATICS_DODGE, dodgeChance)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", dodgeChanceLucky) : ""));
        }
        
        if (canRoll) {

            AbstractSubSkill abstractSubSkill = InteractionManager.getAbstractByName("Roll");

            if(abstractSubSkill != null)
            {
                double rollChance, graceChance;

                //Chance to roll at half
                RandomChanceSkill roll_rcs  = new RandomChanceSkill(player, SubSkillType.ACROBATICS_ROLL);

                //Chance to graceful roll
                RandomChanceSkill grace_rcs = new RandomChanceSkill(player, SubSkillType.ACROBATICS_ROLL);
                grace_rcs.setSkillLevel(grace_rcs.getSkillLevel() * 2); //Double Odds

                //Chance Stat Calculations
                rollChance       = RandomChanceUtil.getRandomChanceExecutionChance(roll_rcs);
                graceChance      = RandomChanceUtil.getRandomChanceExecutionChance(grace_rcs);
                //damageThreshold  = mcMMO.p.getAdvancedConfig().getRollDamageThreshold();

                String[] rollStrings = getAbilityDisplayValues(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, player, SubSkillType.ACROBATICS_ROLL);

                //Format
                double rollChanceLucky  = rollChance * 1.333D;
                double graceChanceLucky = graceChance * 1.333D;

                messages.add(getStatMessage(SubSkillType.ACROBATICS_ROLL, rollStrings[0])
                        + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", rollStrings[1]) : ""));

                /*messages.add(getStatMessage(true, false, SubSkillType.ACROBATICS_ROLL, String.valueOf(graceChance))
                        + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", String.valueOf(graceChanceLucky)) : ""));*/
            }
        }

        return messages;
    }

    @Override
    protected List<Component> getTextComponents(Player player) {
        List<Component> textComponents = new ArrayList<>();

        TextComponentFactory.getSubSkillTextComponents(player, textComponents, PrimarySkillType.ACROBATICS);

        return textComponents;
    }
}
