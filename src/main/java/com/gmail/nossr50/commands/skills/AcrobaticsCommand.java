package com.gmail.nossr50.commands.skills;

import java.util.ArrayList;
import java.util.List;

import com.gmail.nossr50.datatypes.skills.SubSkill;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.PrimarySkill;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;

public class AcrobaticsCommand extends SkillCommand {
    private String dodgeChance;
    private String dodgeChanceLucky;
    private String rollChance;
    private String rollChanceLucky;
    private String gracefulRollChance;
    private String gracefulRollChanceLucky;

    private boolean canDodge;
    private boolean canRoll;
    private boolean canGracefulRoll;

    public AcrobaticsCommand() {
        super(PrimarySkill.ACROBATICS);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue, boolean isLucky) {
        // ACROBATICS_DODGE
        if (canDodge) {
            String[] dodgeStrings = calculateAbilityDisplayValues(skillValue, SubSkill.ACROBATICS_DODGE, isLucky);
            dodgeChance = dodgeStrings[0];
            dodgeChanceLucky = dodgeStrings[1];
        }

        // ACROBATICS_ROLL
        if (canRoll) {
            String[] rollStrings = calculateAbilityDisplayValues(skillValue, SubSkill.ACROBATICS_ROLL, isLucky);
            rollChance = rollStrings[0];
            rollChanceLucky = rollStrings[1];
        }

        // GRACEFUL ACROBATICS_ROLL
        if (canGracefulRoll) {
            String[] gracefulRollStrings = calculateAbilityDisplayValues(skillValue, SubSkill.ACROBATICS_GRACEFUL_ROLL, isLucky);
            gracefulRollChance = gracefulRollStrings[0];
            gracefulRollChanceLucky = gracefulRollStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canDodge = Permissions.isSubSkillEnabled(player, SubSkill.ACROBATICS_DODGE);
        canRoll = Permissions.isSubSkillEnabled(player, SubSkill.ACROBATICS_ROLL);
        canGracefulRoll = Permissions.isSubSkillEnabled(player, SubSkill.ACROBATICS_GRACEFUL_ROLL);
    }

    @Override
    protected List<String> effectsDisplay() {
        List<String> messages = new ArrayList<String>();

        if (canRoll) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Acrobatics.Effect.0"), LocaleLoader.getString("Acrobatics.Effect.1")));
        }

        if (canGracefulRoll) {
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
            messages.add(LocaleLoader.getString("Acrobatics.Roll.Chance", rollChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", rollChanceLucky) : ""));
        }

        if (canGracefulRoll) {
            messages.add(LocaleLoader.getString("Acrobatics.Roll.GraceChance", gracefulRollChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", gracefulRollChanceLucky) : ""));
        }

        if (canDodge) {
            messages.add(LocaleLoader.getString("Acrobatics.DodgeChance", dodgeChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", dodgeChanceLucky) : ""));
        }

        return messages;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        List<TextComponent> textComponents = new ArrayList<>();
        return textComponents;
    }
}
