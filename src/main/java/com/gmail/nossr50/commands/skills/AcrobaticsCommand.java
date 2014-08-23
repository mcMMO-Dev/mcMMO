package com.gmail.nossr50.commands.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
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
        super(SkillType.acrobatics);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue, boolean isLucky) {
        // DODGE
        if (canDodge) {
            String[] dodgeStrings = calculateAbilityDisplayValues(skillValue, SecondaryAbility.DODGE, isLucky);
            dodgeChance = dodgeStrings[0];
            dodgeChanceLucky = dodgeStrings[1];
        }

        // ROLL
        if (canRoll) {
            String[] rollStrings = calculateAbilityDisplayValues(skillValue, SecondaryAbility.ROLL, isLucky);
            rollChance = rollStrings[0];
            rollChanceLucky = rollStrings[1];
        }

        // GRACEFUL ROLL
        if (canGracefulRoll) {
            String[] gracefulRollStrings = calculateAbilityDisplayValues(skillValue, SecondaryAbility.GRACEFUL_ROLL, isLucky);
            gracefulRollChance = gracefulRollStrings[0];
            gracefulRollChanceLucky = gracefulRollStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canDodge = Permissions.secondaryAbilityEnabled(player, SecondaryAbility.DODGE);
        canRoll = Permissions.secondaryAbilityEnabled(player, SecondaryAbility.ROLL);
        canGracefulRoll = Permissions.secondaryAbilityEnabled(player, SecondaryAbility.GRACEFUL_ROLL);
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
}
