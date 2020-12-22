package com.gmail.nossr50.commands.skills;

import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.skills.crossbows.CrossbowManager;
import com.gmail.nossr50.util.text.TextComponentFactory;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CrossbowsCommand extends SkillCommand {
    public CrossbowsCommand() {
        super(PrimarySkillType.CROSSBOWS);
    }

    @Override
    protected void dataCalculations(@NotNull OnlineMMOPlayer mmoPlayer, float skillValue) {

    }

    @Override
    protected void permissionsCheck(@NotNull OnlineMMOPlayer mmoPlayer) {

    }

    @Override
    protected @NotNull List<String> statsDisplay(@NotNull OnlineMMOPlayer mmoPlayer, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<>();

        CrossbowManager crossbowManager = mmoPlayer.getCrossbowManager();

        if(canUseSubskill(mmoPlayer, SubSkillType.CROSSBOWS_SUPER_SHOTGUN)) {
            String additionalArrowCount = String.valueOf(crossbowManager.getSuperShotgunAdditionalArrowCount());
            messages.add(getStatMessage(SubSkillType.CROSSBOWS_SUPER_SHOTGUN, additionalArrowCount));
        }

        return messages;
    }

    @Override
    protected List<Component> getTextComponents(@NotNull OnlineMMOPlayer mmoPlayer) {
        List<Component> textComponents = new ArrayList<>();

        TextComponentFactory.getSubSkillTextComponents(mmoPlayer, textComponents, PrimarySkillType.CROSSBOWS);

        return textComponents;
    }
}
