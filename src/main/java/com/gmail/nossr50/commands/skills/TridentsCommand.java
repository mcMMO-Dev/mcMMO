package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.text.TextComponentFactory;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TridentsCommand extends SkillCommand {
    public TridentsCommand() {
        super(PrimarySkillType.TRIDENTS);
    }

    @Override
    protected void dataCalculations(@NotNull McMMOPlayer mmoPlayer, float skillValue) {

    }

    @Override
    protected void permissionsCheck(@NotNull McMMOPlayer mmoPlayer) {

    }

    @Override
    protected @NotNull List<String> statsDisplay(@NotNull McMMOPlayer mmoPlayer, float skillValue, boolean hasEndurance, boolean isLucky) {
        return null;
    }

    @Override
    protected @NotNull List<Component> getTextComponents(@NotNull McMMOPlayer mmoPlayer) {
        List<Component> textComponents = new ArrayList<>();

        TextComponentFactory.getSubSkillTextComponents(mmoPlayer, textComponents, PrimarySkillType.TRIDENTS);

        return textComponents;
    }
}
