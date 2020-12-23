package com.gmail.nossr50.commands.skills;

import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.excavation.ExcavationManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.text.TextComponentFactory;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ExcavationCommand extends SkillCommand {
    private String gigaDrillBreakerLength;
    private String gigaDrillBreakerLengthEndurance;

    private boolean canGigaDrill;
    private boolean canTreasureHunt;

    public ExcavationCommand() {
        super(PrimarySkillType.EXCAVATION);
    }

    @Override
    protected void dataCalculations(@NotNull OnlineMMOPlayer mmoPlayer, float skillValue) {
        // GIGA DRILL BREAKER
        if (canGigaDrill) {
            String[] gigaDrillStrings = calculateLengthDisplayValues(mmoPlayer, skillValue);
            gigaDrillBreakerLength = gigaDrillStrings[0];
            gigaDrillBreakerLengthEndurance = gigaDrillStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(@NotNull OnlineMMOPlayer mmoPlayer) {
        canGigaDrill = Permissions.gigaDrillBreaker(mmoPlayer.getPlayer()) && RankUtils.hasUnlockedSubskill(mmoPlayer, SubSkillType.EXCAVATION_GIGA_DRILL_BREAKER);
        canTreasureHunt = canUseSubskill(mmoPlayer, SubSkillType.EXCAVATION_ARCHAEOLOGY);
    }

    @Override
    protected @NotNull List<String> statsDisplay(@NotNull OnlineMMOPlayer mmoPlayer, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<>();

        ExcavationManager excavationManager = ((McMMOPlayer) (mmoPlayer)).getExcavationManager();

        if (canGigaDrill) {
            messages.add(getStatMessage(SubSkillType.EXCAVATION_GIGA_DRILL_BREAKER, gigaDrillBreakerLength)
                    + (hasEndurance ? LocaleLoader.getString("Perks.ActivationTime.Bonus", gigaDrillBreakerLengthEndurance) : ""));

            //messages.add(LocaleLoader.getString("Excavation.Effect.Length", gigaDrillBreakerLength) + (hasEndurance ? LocaleLoader.getString("Perks.ActivationTime.Bonus", gigaDrillBreakerLengthEndurance) : ""));
        }

        if(canUseSubskill(mmoPlayer, SubSkillType.EXCAVATION_ARCHAEOLOGY)) {
            messages.add(getStatMessage(false, false, SubSkillType.EXCAVATION_ARCHAEOLOGY,
                    percent.format(excavationManager.getArchaelogyExperienceOrbChance() / 100.0D)));
            messages.add(getStatMessage(true, false, SubSkillType.EXCAVATION_ARCHAEOLOGY,
                    String.valueOf(excavationManager.getExperienceOrbsReward())));

        }

        return messages;
    }

    @Override
    protected @NotNull List<Component> getTextComponents(@NotNull OnlineMMOPlayer mmoPlayer) {
        List<Component> textComponents = new ArrayList<>();

        TextComponentFactory.getSubSkillTextComponents(mmoPlayer, textComponents, PrimarySkillType.EXCAVATION);

        return textComponents;
    }
}
