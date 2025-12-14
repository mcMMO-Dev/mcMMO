package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.listeners.InteractionManager;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.text.TextComponentFactory;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

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
            final String[] dodgeStrings = ProbabilityUtil.getRNGDisplayValues(mmoPlayer,
                    SubSkillType.ACROBATICS_DODGE);
            dodgeChance = dodgeStrings[0];
            dodgeChanceLucky = dodgeStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canDodge = Permissions.canUseSubSkill(player, SubSkillType.ACROBATICS_DODGE);
        canRoll = Permissions.canUseSubSkill(player, SubSkillType.ACROBATICS_ROLL);
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance,
            boolean isLucky) {
        List<String> messages = new ArrayList<>();

        if (canDodge) {
            messages.add(getStatMessage(SubSkillType.ACROBATICS_DODGE, dodgeChance)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", dodgeChanceLucky)
                    : ""));
        }

        if (canRoll) {

            AbstractSubSkill abstractSubSkill = InteractionManager.getAbstractByName("Roll");

            if (abstractSubSkill != null) {
                String[] rollStrings = ProbabilityUtil.getRNGDisplayValues(mmoPlayer,
                        SubSkillType.ACROBATICS_ROLL);

                messages.add(getStatMessage(SubSkillType.ACROBATICS_ROLL, rollStrings[0])
                        + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", rollStrings[1])
                        : ""));
            }
        }

        return messages;
    }

    @Override
    protected List<Component> getTextComponents(Player player) {
        List<Component> textComponents = new ArrayList<>();

        TextComponentFactory.appendSubSkillTextComponents(player, textComponents,
                PrimarySkillType.ACROBATICS);

        return textComponents;
    }
}
