package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.excavation.ExcavationManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.text.TextComponentFactory;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class ExcavationCommand extends SkillCommand {
    private String gigaDrillBreakerLength;
    private String gigaDrillBreakerLengthEndurance;

    private boolean canGigaDrill;
    private boolean canTreasureHunt;

    public ExcavationCommand() {
        super(PrimarySkillType.EXCAVATION);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue) {
        // GIGA DRILL BREAKER
        if (canGigaDrill) {
            String[] gigaDrillStrings = calculateLengthDisplayValues(player, skillValue);
            gigaDrillBreakerLength = gigaDrillStrings[0];
            gigaDrillBreakerLengthEndurance = gigaDrillStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canGigaDrill = Permissions.gigaDrillBreaker(player) && RankUtils.hasUnlockedSubskill(player,
                SubSkillType.EXCAVATION_GIGA_DRILL_BREAKER);
        canTreasureHunt = Permissions.canUseSubSkill(player, SubSkillType.EXCAVATION_ARCHAEOLOGY);
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance,
            boolean isLucky) {
        List<String> messages = new ArrayList<>();

        ExcavationManager excavationManager = mmoPlayer.getExcavationManager();

        if (canGigaDrill) {
            messages.add(getStatMessage(SubSkillType.EXCAVATION_GIGA_DRILL_BREAKER,
                    gigaDrillBreakerLength)
                    + (hasEndurance ? LocaleLoader.getString("Perks.ActivationTime.Bonus",
                    gigaDrillBreakerLengthEndurance) : ""));

            //messages.add(LocaleLoader.getString("Excavation.Effect.Length", gigaDrillBreakerLength) + (hasEndurance ? LocaleLoader.getString("Perks.ActivationTime.Bonus", gigaDrillBreakerLengthEndurance) : ""));
        }

        if (Permissions.canUseSubSkill(player, SubSkillType.EXCAVATION_ARCHAEOLOGY)) {
            messages.add(getStatMessage(false, false, SubSkillType.EXCAVATION_ARCHAEOLOGY,
                    percent.format(excavationManager.getArchaelogyExperienceOrbChance() / 100.0D)));
            messages.add(getStatMessage(true, false, SubSkillType.EXCAVATION_ARCHAEOLOGY,
                    String.valueOf(excavationManager.getExperienceOrbsReward())));

        }

        return messages;
    }

    @Override
    protected List<Component> getTextComponents(Player player) {
        List<Component> textComponents = new ArrayList<>();

        TextComponentFactory.appendSubSkillTextComponents(player, textComponents,
                PrimarySkillType.EXCAVATION);

        return textComponents;
    }
}
