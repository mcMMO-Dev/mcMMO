package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.excavation.ExcavationManager;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ExcavationCommand extends SkillCommand {
    private String gigaDrillBreakerLength;
    private String gigaDrillBreakerLengthEndurance;

    private boolean canGigaDrill;
    private boolean canTreasureHunt;

    public ExcavationCommand(mcMMO pluginRef) {
        super(PrimarySkillType.EXCAVATION, pluginRef);
    }

    @Override
    protected void dataCalculations(Player player, double skillValue) {
        // GIGA DRILL BREAKER
        if (canGigaDrill) {
            String[] gigaDrillStrings = formatLengthDisplayValues(player, skillValue);
            gigaDrillBreakerLength = gigaDrillStrings[0];
            gigaDrillBreakerLengthEndurance = gigaDrillStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canGigaDrill = pluginRef.getPermissionTools().gigaDrillBreaker(player) && pluginRef.getRankTools().hasUnlockedSubskill(player, SubSkillType.EXCAVATION_GIGA_DRILL_BREAKER);
        canTreasureHunt = canUseSubskill(player, SubSkillType.EXCAVATION_ARCHAEOLOGY);
    }

    @Override
    protected List<String> statsDisplay(Player player, double skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<>();

        ExcavationManager excavationManager = pluginRef.getUserManager().getPlayer(player).getExcavationManager();

        if (canGigaDrill) {
            messages.add(getStatMessage(SubSkillType.EXCAVATION_GIGA_DRILL_BREAKER, gigaDrillBreakerLength)
                    + (hasEndurance ? pluginRef.getLocaleManager().getString("Perks.ActivationTime.Bonus", gigaDrillBreakerLengthEndurance) : ""));

            //messages.add(pluginRef.getLocaleManager().getString("Excavation.Effect.Length", gigaDrillBreakerLength) + (hasEndurance ? pluginRef.getLocaleManager().getString("Perks.ActivationTime.Bonus", gigaDrillBreakerLengthEndurance) : ""));
        }

        if(canUseSubskill(player, SubSkillType.EXCAVATION_ARCHAEOLOGY)) {
            messages.add(getStatMessage(false, false, SubSkillType.EXCAVATION_ARCHAEOLOGY,
                    percent.format(excavationManager.getArchaelogyExperienceOrbChance() / 100.0D)));
            messages.add(getStatMessage(true, false, SubSkillType.EXCAVATION_ARCHAEOLOGY,
                    String.valueOf(excavationManager.getExperienceOrbsReward())));

        }

        return messages;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        List<TextComponent> textComponents = new ArrayList<>();

        pluginRef.getTextComponentFactory().getSubSkillTextComponents(player, textComponents, PrimarySkillType.EXCAVATION);

        return textComponents;
    }
}
