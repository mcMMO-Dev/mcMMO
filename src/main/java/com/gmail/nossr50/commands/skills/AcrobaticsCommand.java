package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.listeners.InteractionManager;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.TextComponentFactory;
import com.gmail.nossr50.util.skills.SkillUtils;
import net.md_5.bungee.api.chat.TextComponent;
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
    protected void dataCalculations(Player player, float skillValue, boolean isLucky) {
        // ACROBATICS_DODGE
        if (canDodge) {
            String[] dodgeStrings = SkillUtils.calculateAbilityDisplayValues(skillValue, SubSkillType.ACROBATICS_DODGE, isLucky);
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
    protected List<String> effectsDisplay() {
        List<String> messages = new ArrayList<String>();

        if (canRoll) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Acrobatics.Effect.0"), LocaleLoader.getString("Acrobatics.Effect.1")));
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Acrobatics.Effect.2"), LocaleLoader.getString("Acrobatics.Effect.3")));
        }

        if (canDodge) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Acrobatics.Effect.4"), LocaleLoader.getString("Acrobatics.Effect.5")));
        }

        return messages;
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<String>();

        if (canRoll) {

            AbstractSubSkill abstractSubSkill = InteractionManager.getAbstractByName("Roll");

            if(abstractSubSkill != null)
            {
                double maxBonusLevel = Config.getInstance().getIsRetroMode() ? AdvancedConfig.getInstance().getMaxBonusLevel(abstractSubSkill) * 10 : AdvancedConfig.getInstance().getMaxBonusLevel(abstractSubSkill);
                double maxChance = AdvancedConfig.getInstance().getMaxChance(abstractSubSkill);
                double rollChance   = SkillUtils.getChanceOfSuccess(skillValue, maxBonusLevel, maxChance);
                double graceChance  = SkillUtils.getChanceOfSuccess(skillValue, maxBonusLevel, maxChance / 2);

                String rollChanceLucky = isLucky ? percent.format(Math.min(rollChance * 1.3333D, 100.0D) / 100.0D) : null;
                String graceChanceLucky = isLucky ? percent.format(Math.min(rollChance * 1.3333D, 100.0D) / 100.0D) : null;


                messages.add(getStatMessage(SubSkillType.ACROBATICS_ROLL, percent.format(rollChance))
                        + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", rollChanceLucky) : ""));
                messages.add(getStatMessage(true, false, SubSkillType.ACROBATICS_ROLL, percent.format(graceChance))
                        + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", graceChanceLucky) : ""));
            }
        }

        if (canDodge) {
            messages.add(getStatMessage(SubSkillType.ACROBATICS_DODGE, dodgeChance)
                    + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", dodgeChanceLucky) : ""));
        }

        return messages;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        List<TextComponent> textComponents = new ArrayList<>();

        TextComponentFactory.getSubSkillTextComponents(player, textComponents, PrimarySkillType.ACROBATICS);

        return textComponents;
    }
}
