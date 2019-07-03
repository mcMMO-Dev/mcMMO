package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.axes.Axes;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AxesCommand extends SkillCommand {
    private String critChance;
    private String critChanceLucky;
    private double axeMasteryDamage;
    private double impactDamage;
    private String skullSplitterLength;
    private String skullSplitterLengthEndurance;

    private boolean canSkullSplitter;
    private boolean canCritical;
    private boolean canAxeMastery;
    private boolean canImpact;
    private boolean canGreaterImpact;

    public AxesCommand(mcMMO pluginRef) {
        super(PrimarySkillType.AXES, pluginRef);
    }

    @Override
    protected void dataCalculations(Player player, double skillValue) {
        // ARMOR IMPACT
        if (canImpact) {
            impactDamage = pluginRef.getUserManager().getPlayer(player).getAxesManager().getImpactDurabilityDamage();
        }

        // AXE MASTERY
        if (canAxeMastery) {
            axeMasteryDamage = Axes.getAxeMasteryBonusDamage(player);
        }

        // CRITICAL HIT
        if (canCritical) {
            String[] criticalHitStrings = getAbilityDisplayValues(player, SubSkillType.AXES_CRITICAL_STRIKES);
            critChance = criticalHitStrings[0];
            critChanceLucky = criticalHitStrings[1];
        }

        // SKULL SPLITTER
        if (canSkullSplitter) {
            String[] skullSplitterStrings = formatLengthDisplayValues(player, skillValue);
            skullSplitterLength = skullSplitterStrings[0];
            skullSplitterLengthEndurance = skullSplitterStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canSkullSplitter = Permissions.skullSplitter(player) && RankUtils.hasUnlockedSubskill(player, SubSkillType.AXES_SKULL_SPLITTER);
        canCritical = canUseSubskill(player, SubSkillType.AXES_CRITICAL_STRIKES);
        canAxeMastery = canUseSubskill(player, SubSkillType.AXES_AXE_MASTERY);
        canImpact = canUseSubskill(player, SubSkillType.AXES_ARMOR_IMPACT);
        canGreaterImpact = canUseSubskill(player, SubSkillType.AXES_GREATER_IMPACT);
    }

    @Override
    protected List<String> statsDisplay(Player player, double skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<>();

        if (canImpact) {
            messages.add(pluginRef.getLocaleManager().getString("Ability.Generic.Template", pluginRef.getLocaleManager().getString("Axes.Ability.Bonus.2"), pluginRef.getLocaleManager().getString("Axes.Ability.Bonus.3", impactDamage)));
        }

        if (canAxeMastery) {
            messages.add(pluginRef.getLocaleManager().getString("Ability.Generic.Template", pluginRef.getLocaleManager().getString("Axes.Ability.Bonus.0"), pluginRef.getLocaleManager().getString("Axes.Ability.Bonus.1", axeMasteryDamage)));
        }

        if (canCritical) {
            messages.add(getStatMessage(SubSkillType.AXES_CRITICAL_STRIKES, critChance)
                    + (isLucky ? pluginRef.getLocaleManager().getString("Perks.Lucky.Bonus", critChanceLucky) : ""));
        }

        if (canGreaterImpact) {
            messages.add(pluginRef.getLocaleManager().getString("Ability.Generic.Template", pluginRef.getLocaleManager().getString("Axes.Ability.Bonus.4"), pluginRef.getLocaleManager().getString("Axes.Ability.Bonus.5", pluginRef.getConfigManager().getConfigAxes().getGreaterImpactBonusDamage())));
        }

        if (canSkullSplitter) {
            messages.add(getStatMessage(SubSkillType.AXES_SKULL_SPLITTER, skullSplitterLength)
                    + (hasEndurance ? pluginRef.getLocaleManager().getString("Perks.ActivationTime.Bonus", skullSplitterLengthEndurance) : ""));
        }

        if (canUseSubskill(player, SubSkillType.AXES_AXES_LIMIT_BREAK)) {
            messages.add(getStatMessage(SubSkillType.AXES_AXES_LIMIT_BREAK,
                    String.valueOf(CombatUtils.getLimitBreakDamage(player, SubSkillType.AXES_AXES_LIMIT_BREAK))));
        }

        return messages;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        List<TextComponent> textComponents = new ArrayList<>();

        pluginRef.getTextComponentFactory().getSubSkillTextComponents(player, textComponents, PrimarySkillType.AXES);

        return textComponents;
    }
}
