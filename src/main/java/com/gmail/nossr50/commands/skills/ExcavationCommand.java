package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.TextComponentFactory;
import com.gmail.nossr50.util.skills.RankUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

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
    protected void dataCalculations(Player player, float skillValue, boolean isLucky) {
        // GIGA DRILL BREAKER
        if (canGigaDrill) {
            String gigaDrillStrings[] = calculateLengthDisplayValues(player, skillValue);
            gigaDrillBreakerLength = gigaDrillStrings[0];
            gigaDrillBreakerLengthEndurance = gigaDrillStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canGigaDrill = Permissions.gigaDrillBreaker(player) && RankUtils.hasUnlockedSubskill(player, SubSkillType.EXCAVATION_GIGA_DRILL_BREAKER);
        canTreasureHunt = canUseSubskill(player, SubSkillType.EXCAVATION_ARCHAEOLOGY);
    }

    @Override
    protected List<String> effectsDisplay() {
        List<String> messages = new ArrayList<String>();

        if (canGigaDrill) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Excavation.Effect.0"), LocaleLoader.getString("Excavation.Effect.1")));
        }

        if (canTreasureHunt) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Excavation.Effect.2"), LocaleLoader.getString("Excavation.Effect.3")));
        }

        return messages;
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<String>();

        if (canGigaDrill) {
            messages.add(getStatMessage(SubSkillType.EXCAVATION_GIGA_DRILL_BREAKER, gigaDrillBreakerLength)
                    + (hasEndurance ? LocaleLoader.getString("Perks.ActivationTime.Bonus", gigaDrillBreakerLengthEndurance) : ""));

            //messages.add(LocaleLoader.getString("Excavation.Effect.Length", gigaDrillBreakerLength) + (hasEndurance ? LocaleLoader.getString("Perks.ActivationTime.Bonus", gigaDrillBreakerLengthEndurance) : ""));
        }

        return messages;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        List<TextComponent> textComponents = new ArrayList<>();

        TextComponentFactory.getSubSkillTextComponents(player, textComponents, PrimarySkillType.EXCAVATION);

        return textComponents;
    }
}
